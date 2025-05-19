package com.example.meditation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.room.Room
import com.example.meditation.R
import com.example.meditation.databinding.ActivityPlayMusicLightBinding
import com.dev.meditation.model.ModelMusicPlayerViewPager
import com.dev.meditation.model.ModelMyFragment
import com.dev.meditation.`object`.Utils
import com.dev.meditation.service.MusicPlayerService
import com.example.meditation.dataClass.DownloadDatabase
import com.example.meditation.model.ModelDownloadSong
import io.realm.Realm
import java.io.IOException

class PlayMusicLightActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityPlayMusicLightBinding
    private val handler = Handler(Looper.getMainLooper())
    private var musicList: ArrayList<ModelMusicPlayerViewPager> = arrayListOf()
    private var currentPosition: Int = 0
    private var musicService: MusicPlayerService? = null
    private var isLiked = false
    private lateinit var realmDb : Realm
    private lateinit var downloadDb : DownloadDatabase

    // For Android 13+ permission handling
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestFileCreation()
        } else {
            Toast.makeText(this, "Permission required to download music", Toast.LENGTH_SHORT).show()
        }
    }

    // For Android 11+ file creation
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("audio/mpeg")
    ) { uri ->
        uri?.let {
            saveMusicFileToUri(it)
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMusicLightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        addListeners()
    }

    private fun init(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)
            val layoutParams = binding.lytRelativeMusicLight.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytRelativeMusicLight.layoutParams = layoutParams
            insets
        }

        Utils.setStatusBarColor(this, false)

//        realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)

        musicList = intent.getParcelableArrayListExtra("musicList") ?: arrayListOf()
        currentPosition = intent.getIntExtra("position", 0)

        updateLikeIcon()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK), 200)
            } else {
                startMusicService()
            }
        } else {
            startMusicService()
        }

        downloadDb = Room.databaseBuilder(applicationContext,DownloadDatabase::class.java,"historyDatabase")
            .allowMainThreadQueries()
            .build()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ObsoleteSdkInt")
    private fun addListeners() {
        binding.ivCancelMusicLight.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.ivLikeMusicLight.setOnClickListener {
            isLiked = !isLiked
            realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)
            val currentSong = musicList[currentPosition]

            realmDb.executeTransaction { r ->
                if (isLiked) {
                    val exists = r.where(ModelMyFragment::class.java)
                        .equalTo("songTitle", currentSong.songTitle.trim())
                        .equalTo("songDuration", currentSong.songTime.trim())
                        .equalTo("songLyrics", currentSong.song)
                        .equalTo("songType", "meditate")
                        .findFirst()
                    if (exists == null) {
                        val modelObj = r.createObject(ModelMyFragment::class.java)
                        modelObj.songTitle = currentSong.songTitle.trim()
                        modelObj.songDuration = currentSong.songTime.trim()
                        modelObj.songLyrics = currentSong.song
                        modelObj.songType = "meditate"
                        Toast.makeText(applicationContext, "Added To Likes", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val toDelete = r.where(ModelMyFragment::class.java)
                        .equalTo("songTitle", currentSong.songTitle.trim())
                        .equalTo("songDuration", currentSong.songTime.trim())
                        .equalTo("songLyrics", currentSong.song)
                        .equalTo("songType", "meditate")
                        .findAll()
                    toDelete.deleteAllFromRealm()

                    Toast.makeText(applicationContext, "Removed From Likes", Toast.LENGTH_SHORT).show()
                }
            }

            updateLikeIcon()
        }

        binding.ivDownloadMusicLight.setOnClickListener {

            val currentDrawable = binding.ivDownloadMusicLight.drawable
            val downloadedDrawable = resources.getDrawable(R.drawable.btn_downloaded_light, null)

            if (currentDrawable.constantState == downloadedDrawable.constantState) {
                Toast.makeText(this, "Music already downloaded", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkAndRequestStoragePermission()
        }
    }

    private fun checkAndRequestStoragePermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    requestFileCreation()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11 (R) and above - no permission needed for SAF
                requestFileCreation()
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    requestFileCreation()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
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
        showDownloadNotification("Download Started", "Downloading ${musicList[currentPosition].songTitle}", true, 101)

        Log.d("MusicDownload", "saveMusicFileToUri: ${musicList[currentPosition].songTitle}")
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            try {
                val buffer = ByteArray(4096)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                binding.ivDownloadMusicLight.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.btn_downloaded_light)
                )

                // Update notification to show "Download Complete"
                addDownloadItem()
                showDownloadNotification(
                    "Download Complete",
                    "${musicList[currentPosition].songTitle} saved",
                    false,
                    101
                )
                Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
            } finally {
                inputStream.close()
            }
        }
    }

    private fun addDownloadItem(){
        val modelDownloadSong = ModelDownloadSong()
        modelDownloadSong.songTitle = musicList[currentPosition].songTitle
        modelDownloadSong.songDuration = musicList[currentPosition].songTime
        modelDownloadSong.songLyrics = musicList[currentPosition].song

        downloadDb.daoClass().insertDownloadHistory(modelDownloadSong)
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
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
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
        val fileName = "${currentSong.songTitle}_${System.currentTimeMillis()}.mp3"
        createFileLauncher.launch(fileName)
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
        val exists = realmDb.where(ModelMyFragment::class.java)
            .equalTo("songTitle", currentSong.songTitle.trim())
            .equalTo("songDuration", currentSong.songTime.trim())
            .equalTo("songLyrics", currentSong.song)
            .equalTo("songType", "meditate")
            .findFirst() != null

        isLiked = exists
        binding.ivLikeMusicLight.setImageDrawable(
            if (isLiked)
                resources.getDrawable(R.drawable.selected_like_music_light)
            else
                resources.getDrawable(R.drawable.btn_like_music_light)
        )
    }

    // Update the playbackReceiver in PlayMusicLightActivity.kt
    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.dev.meditation.PLAYBACK_STATE_CHANGED") {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val newPosition = intent.getIntExtra("position", currentPosition)

                currentPosition = newPosition
                binding.ivPlayPauseLight.setImageResource(
                    if (isPlaying) R.drawable.btn_pause_light else R.drawable.btn_play_light
                )

                Handler(Looper.getMainLooper()).post {
                    val song = musicList.getOrNull(currentPosition)
                    song?.let {
                        binding.tvTitleMusicLight.text = it.songTitle
                        val duration = musicService?.mediaPlayer?.duration ?: 0
                        binding.seekbarMusicLight.max = duration
                        binding.tvEndTimeMusicLight.text = formatTime(duration)
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

        binding.ivPlayPauseLight.setOnClickListener {
            musicService?.togglePlayPause()
        }

        binding.ivNextMusicLight.setOnClickListener {
            it.startAnimation(scaleAnim)
            if (currentPosition < musicList.size - 1) {
                currentPosition++
                musicService?.play(currentPosition)
                updateLikeIcon()
            } else {
                Toast.makeText(this, "This is the last song", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivPreviousMusicLight.setOnClickListener {
            it.startAnimation(scaleAnim)
            if (currentPosition > 0) {
                currentPosition--
                musicService?.play(currentPosition)
                updateLikeIcon()
            } else {
                Toast.makeText(this, "This is the first song", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivForwardSecondLight.setOnClickListener {
            it.startAnimation(scaleAnim)
            musicService?.mediaPlayer?.let { player ->
                val newPosition = (player.currentPosition + 15000).coerceAtMost(player.duration)
                player.seekTo(newPosition)
                binding.seekbarMusicLight.progress = newPosition
                binding.tvStartTimeMusicLight.text = formatTime(newPosition)
            }
        }

        binding.ivBackwardSecondLight.setOnClickListener {
            it.startAnimation(scaleAnim)
            musicService?.mediaPlayer?.let { player ->
                val newPosition = (player.currentPosition - 15000).coerceAtLeast(0)
                player.seekTo(newPosition)
                binding.seekbarMusicLight.progress = newPosition
                binding.tvStartTimeMusicLight.text = formatTime(newPosition)
            }
        }

        binding.seekbarMusicLight.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.mediaPlayer?.seekTo(progress)
                    binding.tvStartTimeMusicLight.text = formatTime(progress)
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
                        binding.seekbarMusicLight.progress = currentPosition
                        binding.tvStartTimeMusicLight.text = formatTime(currentPosition)
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
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
//        musicService?.mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
    }
}