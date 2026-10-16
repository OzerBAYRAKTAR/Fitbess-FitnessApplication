package com.bayraktar.healthybackandneck.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.ui.HomeActivity

class WaterReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        dagger.hilt.android.EntryPointAccessors.fromApplication(applicationContext, ReminderDependencies::class.java)
            .reminders().deliver(ReminderKind.WATER)
        return Result.success()
    }
}
