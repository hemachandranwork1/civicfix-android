package com.civicfix.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.civicfix.R

object NotificationHelper {
    private const val CHANNEL_ID = "civicfix_updates"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "Issue Updates", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Notifications when your issue status changes" }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun showStatusUpdate(context: Context, issueTitle: String, newStatus: String, id: Int) {
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Issue Updated")
            .setContentText("\"$issueTitle\" is now $newStatus")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(id, notif)
    }
}
