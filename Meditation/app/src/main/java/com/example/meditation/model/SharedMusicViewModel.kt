package com.example.meditation.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedMusicViewModel : ViewModel() {
    val isPlaying = MutableLiveData<Boolean>()
    val currentPosition = MutableLiveData<Int>()

    fun updateState(playing: Boolean, position: Int) {
        isPlaying.value = playing
        currentPosition.value = position
    }
}