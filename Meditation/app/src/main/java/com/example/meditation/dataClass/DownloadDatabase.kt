package com.example.meditation.dataClass

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.meditation.interfaces.DownloadDao
import com.example.meditation.model.ModelDownloadSong

@Database(entities = [ModelDownloadSong::class], version = 1)
abstract class DownloadDatabase : RoomDatabase() {
    abstract fun daoClass() : DownloadDao
}