package com.example.videoplayerappdemo

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val _videoFolders = MutableLiveData<List<VideoFolder>>()
    val videoFolders: LiveData<List<VideoFolder>> = _videoFolders

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadVideos()
    }

    fun loadVideos() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val videoList = withContext(Dispatchers.IO) {
                queryVideos()
            }
            if (videoList.isEmpty()) {
                _errorMessage.postValue("No videos found or permission denied.")
            } else {
                _videoFolders.postValue(groupVideosByFolder(videoList))
            }
            _isLoading.postValue(false)
        }
    }

    private fun queryVideos(): List<Video> {
        val videos = mutableListOf<Video>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA // Path column
        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        getApplication<Application>().contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val path = cursor.getString(dataColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    collection,
                    id
                )

                videos.add(Video(id, name, contentUri, duration, size, path))
            }
        }
        return videos
    }

    private fun groupVideosByFolder(videos: List<Video>): List<VideoFolder> {
        val folderMap = mutableMapOf<String, VideoFolder>()
        videos.forEach { video ->
            val folderPath = video.folderPath
            val folderName = video.folderName
            if (!folderMap.containsKey(folderPath)) {
                folderMap[folderPath] = VideoFolder(folderName, folderPath, mutableListOf())
            }
            folderMap[folderPath]?.videos?.add(video)
        }
        return folderMap.values.sortedBy { it.name }
    }

    fun renameVideo(video: Video, newName: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver = getApplication<Application>().contentResolver
            try {
                // For Android 10+ and MediaStore managed files
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = android.content.ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
                    }
                    val rowsAffected = contentResolver.update(
                        video.uri,
                        contentValues,
                        null,
                        null
                    )
                    if (rowsAffected > 0) {
                        // Update the local video object for UI refresh
                        video.title = newName
                        // Trigger UI update
                        _videoFolders.postValue(groupVideosByFolder(videoFolders.value?.flatMap { it.videos } ?: emptyList()))
                    } else {
                        _errorMessage.postValue("Failed to rename video: ${video.title}")
                    }
                } else { // For Android 9 and below, use direct File API
                    val oldFile = File(video.path)
                    val newFile = File(oldFile.parent, newName + "." + oldFile.extension) // Append original extension
                    if (oldFile.renameTo(newFile)) {
                        // Update MediaStore entry for older versions
                        val oldUri = video.uri
                        contentResolver.delete(oldUri, null, null) // Delete old entry
                        val values = android.content.ContentValues().apply {
                            put(MediaStore.MediaColumns.DATA, newFile.absolutePath)
                            put(MediaStore.MediaColumns.DISPLAY_NAME, newFile.name)
                            put(MediaStore.MediaColumns.MIME_TYPE, "video/*") // Add correct MIME type
                            put(MediaStore.Video.Media.SIZE, newFile.length())
                        }
                        contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values) // Insert new entry

                        // Update the local video object for UI refresh
                        video.title = newName
                        // Trigger UI update
                        _videoFolders.postValue(groupVideosByFolder(videoFolders.value?.flatMap { it.videos } ?: emptyList()))
                    } else {
                        _errorMessage.postValue("Failed to rename file directly: ${video.title}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error renaming video: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // You would add more functions here for copy, cut, paste using MediaStore APIs/Storage Access Framework.
    // These are significantly more complex and often involve user interaction for destination selection.
    // For example, copying would involve reading the input stream of the source video and writing it to a new MediaStore entry.
    // Moving (cut/paste) would be similar to copy + delete original.
}