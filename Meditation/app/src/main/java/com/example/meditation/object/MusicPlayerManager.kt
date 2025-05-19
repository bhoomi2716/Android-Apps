package com.dev.meditation.`object`

import android.content.Context
import android.media.MediaPlayer
import com.dev.meditation.service.MusicPlayerService

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingResId: Int? = null
    private var musicService = MusicPlayerService()

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun playMusic(context: Context, resId: Int, onPlay: () -> Unit, onStop: () -> Unit) {
        // Only stop if something is already playing
        if (isPlaying() || mediaPlayer != null) {
            stopMusic()
        }

        // Prepare MediaPlayer
        mediaPlayer = MediaPlayer.create(context, resId)
        currentPlayingResId = resId

        mediaPlayer?.apply {
            setOnCompletionListener {
                stopMusic()
                onStop()
            }
            start()
            onPlay()
        }
    }

    fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
            currentPlayingResId = null
        }
    }
}
