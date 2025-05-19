    package com.dev.meditation.service
    
    import android.annotation.SuppressLint
    import android.app.Notification
    import android.app.PendingIntent
    import android.app.Service
    import android.content.Intent
    import android.media.MediaPlayer
    import android.os.Binder
    import android.os.IBinder
    import android.os.Parcelable
    import android.widget.RemoteViews
    import android.widget.Toast
    import androidx.core.app.NotificationCompat
    import androidx.media.app.NotificationCompat.MediaStyle
    import com.example.meditation.R
    import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
    import com.dev.meditation.model.ModelMusicPlayerViewPager
    import com.dev.meditation.`object`.GlobalMusicState
    import com.dev.meditation.setclass.MusicApplicationClass.Companion.CHANNEL_ID
    import com.example.meditation.activity.PlayMusicDarkActivity
    import com.example.meditation.activity.PlayMusicLightActivity

    class MusicPlayerService : Service() {

        object MusicPlayerState {
            var isPlaying: Boolean = false
            var currentPosition: Int = 0
        }

        private val binder = MusicBinder()
        var mediaPlayer: MediaPlayer? = null
        private var musicList: ArrayList<Parcelable> = arrayListOf()
        private var position: Int = 0
        private var notification = Notification()

        companion object {
            const val PLAY_PAUSE = "play_pause"
            const val NEXT = "next"
            const val PREVIOUS = "previous"
        }

        inner class MusicBinder : Binder() {
            fun currentService(): MusicPlayerService = this@MusicPlayerService
        }

        override fun onBind(intent: Intent?): IBinder {
            return binder
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            intent?.let {
                when (it.action) {
                    PREVIOUS -> playPreviousSong()
                    PLAY_PAUSE -> togglePlayPause()
                    NEXT -> playNextSong()
                    else -> {
                        // Initial playback setup
                        val receivedList = it.getParcelableArrayListExtra<Parcelable>("songList")
                        if (receivedList != null) {
                            musicList = receivedList
                        }
                        position = it.getIntExtra("currentPosition", 0)
                        play(position)
                    }
                }
            }
            return START_STICKY // Keep service running
        }

        fun togglePlayPause() {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                MusicPlayerState.isPlaying = false
            } else {
                mediaPlayer?.start()
                MusicPlayerState.isPlaying = true
            }
            showNotification() // Always update notification after state change
            sendPlaybackBroadcast()
        }

        private fun playNextSong() {
            if (position < musicList.size - 1) {
                position++
                play(position)
            } else {
                Toast.makeText(this, "This is the last song", Toast.LENGTH_SHORT).show()
            }
        }

        private fun playPreviousSong() {
            if (position > 0) {
                position--
                play(position)
            } else {
                Toast.makeText(this, "This is the first song", Toast.LENGTH_SHORT).show()
            }
        }

        fun play(position: Int) {
            this.position = position
            MusicPlayerState.currentPosition = position
            mediaPlayer?.release()

            val item = musicList.getOrNull(position) ?: return
            val songResId = when (item) {
                is ModelMusicPlayerViewPager -> item.song
                is ModelBottomRecyclerViewSleepFragment -> item.song
                else -> return
            }

            mediaPlayer = MediaPlayer.create(this, songResId)
            mediaPlayer?.start()
            MusicPlayerState.isPlaying = true

            mediaPlayer?.setOnCompletionListener {
                playNextSong()
            }

            showNotification() // Update notification with new state
            sendPlaybackBroadcast()
        }


        @SuppressLint("RemoteViewLayout")
        private fun showNotification() {
            val item = musicList.getOrNull(position) ?: return

            val customLayout = when (item) {
                is ModelMusicPlayerViewPager -> RemoteViews(packageName, R.layout.design_notification_light)
                is ModelBottomRecyclerViewSleepFragment -> RemoteViews(packageName, R.layout.design_notification_dark)
                else -> return
            }

            // Get current play state from MediaPlayer directly
            val isPlaying = mediaPlayer?.isPlaying ?: false
            MusicPlayerState.isPlaying = isPlaying // Update global state

            val playPauseIcon = when (item) {
                is ModelMusicPlayerViewPager -> if (isPlaying) R.drawable.btn_pause_notification_light else R.drawable.btn_play_notification_light
                is ModelBottomRecyclerViewSleepFragment -> if (isPlaying) R.drawable.btn_pause_notification_dark else R.drawable.btn_play_notification_dark
                else -> R.drawable.btn_play_notification_light
            }


            when (item) {
                is ModelMusicPlayerViewPager -> {
                    customLayout.setTextViewText(R.id.tvTitleNotificationLight, item.songTitle)
                    customLayout.setImageViewResource(R.id.ivPlayPauseNotificationLight, playPauseIcon)
                    customLayout.setOnClickPendingIntent(R.id.ivPlayPauseNotificationLight, getPendingIntent(PLAY_PAUSE))
                    customLayout.setOnClickPendingIntent(R.id.ivPreviousMusicNotificationLight, getPendingIntent(PREVIOUS))
                    customLayout.setOnClickPendingIntent(R.id.ivNextMusicNotificationLight, getPendingIntent(NEXT))
                }

                is ModelBottomRecyclerViewSleepFragment -> {
                    customLayout.setTextViewText(R.id.tvTitleNotificationDark, item.name)
                    customLayout.setImageViewResource(R.id.ivPlayPauseNotificationDark, playPauseIcon)
                    customLayout.setOnClickPendingIntent(R.id.ivPlayPauseNotificationDark, getPendingIntent(PLAY_PAUSE))
                    customLayout.setOnClickPendingIntent(R.id.ivPreviousMusicNotificationDark, getPendingIntent(PREVIOUS))
                    customLayout.setOnClickPendingIntent(R.id.ivNextMusicNotificationDark, getPendingIntent(NEXT))
                }
            }

            // 👇 Set activity class dynamically based on the type of song
            val activityClass = when (item) {
                is ModelMusicPlayerViewPager -> PlayMusicLightActivity::class.java
                is ModelBottomRecyclerViewSleepFragment -> PlayMusicDarkActivity::class.java
                else -> return
            }

            // 👇 Include position and list in the notification tap intent
            val activityIntent = Intent(this, activityClass).apply {
                putParcelableArrayListExtra("musicList", musicList)
                putExtra("position", position)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val contentPendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setStyle(MediaStyle())
                .setCustomContentView(customLayout)
                .setCustomBigContentView(customLayout)
                .setContentIntent(contentPendingIntent) // 👈 Required!
                .setOnlyAlertOnce(true)
                .setOngoing(isPlaying)
                .build()

            startForeground(1, notification)
        }

        private fun getPendingIntent(action: String): PendingIntent {
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                this.action = action
            }
            return PendingIntent.getService(
                this,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun sendPlaybackBroadcast() {
            // Update global state
            GlobalMusicState.isPlaying = MusicPlayerState.isPlaying
            GlobalMusicState.currentPlayingPosition = MusicPlayerState.currentPosition

            // Get current song for global state
            val currentItem = musicList.getOrNull(MusicPlayerState.currentPosition)
            when(currentItem){
                is ModelMusicPlayerViewPager-> { GlobalMusicState.currentSong = currentItem }
                is ModelBottomRecyclerViewSleepFragment-> {GlobalMusicState.currentSongSleep = currentItem}
            }

            // Determine fragment type
            val fragmentType = when {
                currentItem is ModelMusicPlayerViewPager && musicList.any {
                    it is ModelMusicPlayerViewPager && it.songTitle.contains("female", true)
                } -> "female"
                currentItem is ModelMusicPlayerViewPager && musicList.any {
                    it is ModelMusicPlayerViewPager && it.songTitle.contains("male", true)
                } -> "male"
                else -> "meditate"
            }

            // Send broadcast
            val intent = Intent("com.dev.meditation.PLAYBACK_STATE_CHANGED").apply {
                putExtra("isPlaying", MusicPlayerState.isPlaying)
                putExtra("position", MusicPlayerState.currentPosition)
                putExtra("fragmentType", fragmentType)
            }
            sendBroadcast(intent)

            // Notify adapters if exists
            GlobalMusicState.currentViewPagerAdapter?.notifyDataSetChanged()
            GlobalMusicState.currentMyFragmentMeditate?.notifyDataSetChanged()
            GlobalMusicState.currentMyFragmentSleep?.notifyDataSetChanged()
        }


        override fun onDestroy() {
            mediaPlayer?.release()
            mediaPlayer = null
            super.onDestroy()
        }
    }