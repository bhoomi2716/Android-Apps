package com.example.meditation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.meditation.R
import com.example.meditation.databinding.ActivityPlayMusicDarkBinding
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
import com.dev.meditation.model.ModelMyFragment
import com.dev.meditation.`object`.GlobalMusicState
import com.dev.meditation.`object`.Utils
import com.dev.meditation.service.MusicPlayerService
import io.realm.Realm
import java.io.IOException

class PlayMusicDarkActivity : AppCompatActivity(), ServiceConnection
{
    private lateinit var binding : ActivityPlayMusicDarkBinding
    private val handler = Handler(Looper.getMainLooper())
    private var musicService: MusicPlayerService? = null
    private val likedPositions = mutableSetOf<Int>()
    private var musicList: ArrayList<ModelBottomRecyclerViewSleepFragment> = arrayListOf()
    private var currentPosition: Int = 0
    private var isLiked = false
    private lateinit var realmDb : Realm

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMusicDarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        addListener()
    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop

            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams = binding.lytRelativeMusicDark.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytRelativeMusicDark.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this,true)

        musicList = intent.getParcelableArrayListExtra("musicList") ?: arrayListOf()
        currentPosition = intent.getIntExtra("position", 0)

        updateLikeIcon()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK), 200)
            } else {
                startMusicService()
            }
        } else {
            startMusicService()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addListener(){
        binding.ivCancelMusicDark.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.ivLikeMusicDark.setOnClickListener {
            isLiked = !isLiked
            realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)
            val currentSong = musicList[currentPosition]

            realmDb.executeTransaction { r ->
                if (isLiked) {
                    likedPositions.add(currentPosition) // Track position
                    val exists = r.where(ModelMyFragment::class.java)
                        .equalTo("songTitle", currentSong.name.trim())
                        .equalTo("songDuration", currentSong.time.trim())
                        .equalTo("songLyrics", currentSong.song)
                        .equalTo("songType", "sleep")
                        .findFirst()
                    if (exists == null) {
                        val modelObj = r.createObject(ModelMyFragment::class.java)
                        modelObj.songTitle = currentSong.name.trim()
                        modelObj.songDuration = currentSong.time.trim()
                        modelObj.songLyrics = currentSong.song
                        modelObj.songType = "sleep"
                        Toast.makeText(applicationContext, "Added To Likes", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    likedPositions.remove(currentPosition) // Remove position
                    val toDelete = r.where(ModelMyFragment::class.java)
                        .equalTo("songTitle", currentSong.name.trim())
                        .equalTo("songDuration", currentSong.time.trim())
                        .equalTo("songLyrics", currentSong.song)
                        .equalTo("songType", "sleep")
                        .findAll()
                    toDelete.deleteAllFromRealm()
                    Toast.makeText(applicationContext, "Removed From Likes", Toast.LENGTH_SHORT).show()
                }
            }

            updateLikeIcon()
        }

        binding.ivDownloadMusicDark.setOnClickListener {
            val currentDrawable = binding.ivDownloadMusicDark.drawable
            val downloadedDrawable = resources.getDrawable(R.drawable.btn_downloaded, null)

            if (currentDrawable.constantState == downloadedDrawable.constantState) {
                Toast.makeText(this, "Music already downloaded", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    requestFileCreation()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_AUDIO), 1002)
                }
            } else {
                requestFileCreation()
            }
        }
    }

    private fun startMusicService() {
        val serviceIntent = Intent(this, MusicPlayerService::class.java).apply {
            putParcelableArrayListExtra("songList", musicList)
            putExtra("currentPosition", currentPosition)
        }
        bindService(serviceIntent, this, BIND_AUTO_CREATE)
        startService(serviceIntent)
    }

    @Deprecated("Deprecated but still needed for SAF result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1003 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                saveMusicFileToUri(uri)
            }
        }
    }

    private fun saveMusicFileToUri(uri: Uri) {
        val inputStream = resources.openRawResource(musicList[currentPosition].song)

        // Show "Download started" notification
        showDownloadNotification("Download Started", "Downloading ${musicList[currentPosition].name}", true, 101)

        contentResolver.openOutputStream(uri)?.use { outputStream ->
            try {
                val buffer = ByteArray(4096)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()

                // Update notification to show "Download Complete"
                binding.ivDownloadMusicDark.setImageDrawable(resources.getDrawable(R.drawable.btn_downloaded))
                showDownloadNotification("Download Complete", "${musicList[currentPosition].name} saved", false, 101)
                Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
            } finally {
                inputStream.close()
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun showDownloadNotification(title: String, content: String, isOngoing: Boolean, notificationId: Int) {
        val channelId = "music_download_channel"
        val channelName = "Music Download Notifications"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                description = "Notifications for music download"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, PlayMusicLightActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon) // Replace with your icon
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(!isOngoing)
            .setOngoing(isOngoing)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun requestFileCreation() {
        val currentSong = musicList[currentPosition]
        val fileName = "${currentSong.name}_${System.currentTimeMillis()}.mp3"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/mpeg"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, 1003)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMusicService()
        } else {
            Toast.makeText(this, "Permission required to play music in background", Toast.LENGTH_SHORT).show()
        }
        if (requestCode == 1002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestFileCreation()
        } else {
            Toast.makeText(this, "Permission Denied for Audio Access", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateLikeIcon() {
        realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)
        val currentSong = musicList[currentPosition]

        // Check if current position is liked
        isLiked = likedPositions.contains(currentPosition) ||
                realmDb.where(ModelMyFragment::class.java)
                    .equalTo("songTitle", currentSong.name.trim())
                    .equalTo("songDuration", currentSong.time.trim())
                    .equalTo("songLyrics", currentSong.song)
                    .equalTo("songType", "sleep")
                    .findFirst() != null

        binding.ivLikeMusicDark.setImageDrawable(
            if (isLiked)
                resources.getDrawable(R.drawable.selected_like_toolbar)
            else
                resources.getDrawable(R.drawable.btn_like_toolbar)
        )
    }

    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.dev.meditation.PLAYBACK_STATE_CHANGED") {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val newPosition = intent.getIntExtra("position", currentPosition)

                currentPosition = newPosition
                binding.ivPlayPauseDark.setImageResource(
                    if (isPlaying) R.drawable.btn_pause_dark else R.drawable.btn_play_dark
                )

                Handler(Looper.getMainLooper()).post {
                    val song = musicList.getOrNull(currentPosition)
                    song?.let {
                        binding.tvTitleMusicDark.text = it.name
                        val duration = musicService?.mediaPlayer?.duration ?: 0
                        binding.seekbarMusicDark.max = duration
                        binding.tvEndTimeMusicDark.text = formatTime(duration)
                        updateSeekBar()
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(playbackReceiver, IntentFilter("com.dev.meditation.PLAYBACK_STATE_CHANGED"),Context.RECEIVER_EXPORTED)
        }else{
            registerReceiver(playbackReceiver,IntentFilter("com.dev.meditation.PLAYBACK_STATE_CHANGED"))
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(playbackReceiver)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicPlayerService.MusicBinder
        musicService = binder.currentService()
        musicService?.play(currentPosition)
        setupPlaybackControls()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    private fun setupPlaybackControls() {
        val scaleAnim = AnimationUtils.loadAnimation(this, R.anim.second_scal_anim)

        binding.ivPlayPauseDark.setOnClickListener {
            musicService?.togglePlayPause()
        }

        binding.ivNextMusicDark.setOnClickListener {
            it.startAnimation(scaleAnim)
            if (currentPosition < musicList.size - 1) {
                currentPosition++
                musicService?.play(currentPosition)
                updateLikeIcon()
            } else {
                Toast.makeText(this, "This is the last song", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivPreviousMusicDark.setOnClickListener {
            it.startAnimation(scaleAnim)
            if (currentPosition > 0) {
                currentPosition--
                musicService?.play(currentPosition)
                updateLikeIcon()
            } else {
                Toast.makeText(this, "This is the first song", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivForwardSecondDark.setOnClickListener {
            it.startAnimation(scaleAnim)
            musicService?.mediaPlayer?.let { player ->
                val newPosition = (player.currentPosition + 15000).coerceAtMost(player.duration)
                player.seekTo(newPosition)
                binding.seekbarMusicDark.progress = newPosition
                binding.tvStartTimeMusicDark.text = formatTime(newPosition)
            }
        }

        binding.ivBackwardSecondDark.setOnClickListener {
            it.startAnimation(scaleAnim)
            musicService?.mediaPlayer?.let { player ->
                val newPosition = (player.currentPosition - 15000).coerceAtLeast(0)
                player.seekTo(newPosition)
                binding.seekbarMusicDark.progress = newPosition
                binding.tvStartTimeMusicDark.text = formatTime(newPosition)
            }
        }

        binding.seekbarMusicDark.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.mediaPlayer?.seekTo(progress)
                    binding.tvStartTimeMusicDark.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                musicService?.mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition
                        binding.seekbarMusicDark.progress = currentPosition
                        binding.tvStartTimeMusicDark.text = formatTime(currentPosition)
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }, 0)
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
//        musicService?.mediaPlayer?.stop()
//        musicService?.mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
//        musicService?.mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
    }
}