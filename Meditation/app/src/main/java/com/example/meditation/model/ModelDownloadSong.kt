package com.example.meditation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Download_Music")
open class ModelDownloadSong{

    @PrimaryKey(autoGenerate = true)
    var songId : Int = 0

    @ColumnInfo(name = "SongTitle")
    var songTitle : String = ""

    @ColumnInfo(name = "SongDuration")
    var songDuration : String = "00:00"

    @ColumnInfo(name = "SongLyrics")
    var songLyrics: Int = 0
}