package com.bayraktar.healthybackandneck.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.ui.HomeActivity

class NotificationHelper(private val context: Context) {

    fun showNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "WaterReminderChannel"
        val channelName = "Water Reminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.label_waterreminder))
            .setContentText(context.getString(R.string.label_drinkwater))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }

    fun showFitnessNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "FitnessReminderChannel"
        val channelName = "Fitness Reminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.label_fitnessreminder))
            .setContentText(context.getString(R.string.label_dofitness))
            .setSmallIcon(R.drawable.ic_fitness_icon) // Replace with your fitness notification icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(2, notificationBuilder.build())
    }
}