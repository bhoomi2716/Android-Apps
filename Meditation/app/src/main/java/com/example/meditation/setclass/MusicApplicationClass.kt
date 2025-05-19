package com.dev.meditation.setclass

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import io.realm.Realm
import io.realm.RealmConfiguration

class MusicApplicationClass : Application() {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "Music Playback"
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("Liked Music.db")
            .deleteRealmIfMigrationNeeded()
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .build()

        Realm.setDefaultConfiguration(config)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for music playback notifications"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
