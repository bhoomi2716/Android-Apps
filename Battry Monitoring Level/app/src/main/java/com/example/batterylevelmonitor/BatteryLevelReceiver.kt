package com.example.batterylevelmonitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class BatteryLevelReceiver : BroadcastReceiver() {
    private var lastNotifiedLevel = -1

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        if (batteryLevel == lastNotifiedLevel) return

        Log.d("BatteryReceiver", "Battery level: $batteryLevel")

        val title: String
        val message: String

        when {
            batteryLevel <= 20 -> {
                title = "Battery Low"
                message = "Battery is at $batteryLevel%. Please plug in your charger."
            }
            batteryLevel == 65 -> {
                title = "Charging OK"
                message = "Battery is $batteryLevel%. You can unplug the charger."
            }
            batteryLevel in 66..99 -> {
                title = "Almost Full"
                message = "Battery is at $batteryLevel%. Charging will complete soon."
            }
            batteryLevel == 100 -> {
                title = "Fully Charged"
                message = "Battery is 100%. Please unplug your charger."
            }
            else -> return
        }

        lastNotifiedLevel = batteryLevel
        sendNotification(context, title, message)
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "battery_alert_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Battery Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }
}
