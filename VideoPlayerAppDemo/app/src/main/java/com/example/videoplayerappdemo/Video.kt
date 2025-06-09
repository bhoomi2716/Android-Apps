package com.example.videoplayerappdemo

import android.net.Uri
import android.os.Parcelable
import java.io.File

@Parcelized
data class Video(
    val id: Long,
    var title: String,
    val uri: Uri,
    val duration: Long, // in milliseconds
    val size: Long,    // in bytes
    val path: String   // Full file path, useful for older Android versions
) : Parcelable {
    val folderPath: String
        get() = File(path).parent ?: ""

    val folderName: String
        get() = File(path).parentFile?.name ?: "Unknown Folder"
}

data class VideoFolder(
    val name: String,
    val path: String,
    val videos: MutableList<Video>
)