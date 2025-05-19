package com.example.meditation.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.meditation.model.ModelDownloadSong

@Dao
interface DownloadDao {
    @Insert
    fun insertDownloadHistory(modelDownloadSong: ModelDownloadSong)

    @Query("select * from Download_Music")
    fun showDownloadHistory() : MutableList<ModelDownloadSong>
}