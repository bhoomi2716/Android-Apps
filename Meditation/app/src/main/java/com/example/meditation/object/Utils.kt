package com.dev.meditation.`object`

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import java.io.File

object Utils {
    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarColor(activity: Activity, isLightText : Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = activity.window
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        )
                statusBarColor = Color.TRANSPARENT
            }
            window.decorView.systemUiVisibility = if (isLightText) {
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    fun getImageFoldersWithPaths(context: Context): List<Pair<String, String>> {
        val folderSet = linkedSetOf<Pair<String, String>>() // Keeps order, avoids duplicates
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(columnIndex)
                val file = File(filePath)
                val folder = file.parentFile
                if (folder != null && folder.exists()) {
                    folderSet.add(Pair(folder.name, folder.absolutePath))
                }
            }
        }
        return folderSet.toList()
    }
}