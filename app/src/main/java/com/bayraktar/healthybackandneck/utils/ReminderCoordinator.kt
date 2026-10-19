package com.bayraktar.healthybackandneck.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.await
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.MotivationNotificationState
import com.bayraktar.healthybackandneck.data.Models.WaterReminderState
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.ui.HomeActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class ReminderInterval(val hours: Int) {
    DAILY(24), EVERY_12_HOURS(12), EVERY_6_HOURS(6);

    companion object {
        fun fromHours(hours: Int) = entries.firstOrNull { it.hours == hours } ?: EVERY_6_HOURS
    }
}

enum class ReminderKind(val defaultInterval: ReminderInterval, val preferenceKey: String, val channel: String, val notificationId: Int, val legacyTag: String) {
    WATER(ReminderInterval.EVERY_6_HOURS, "water_reminder_interval_hours", "WaterReminderChannel", 101, "waterReminderWorkerTag"),
    MOTIVATION(ReminderInterval.EVERY_12_HOURS, "motivation_reminder_interval_hours", "FitnessReminderChannel", 102, "motivateNotif")
}

/** Unique work prevents duplicate reminders; platform scheduling remains approximate. */
class ReminderScheduler(private val manager: WorkManager, private val prefix: String = "reminder_v3_") {
    fun name(kind: ReminderKind) = prefix + kind.name
    suspend fun apply(kind: ReminderKind, enabled: Boolean, interval: ReminderInterval = kind.defaultInterval) {
        if (!enabled) {
            manager.cancelUniqueWork(name(kind)).await()
            return
        }
        val worker = if (kind == ReminderKind.WATER) WaterReminderWorker::class.java else NotificationWorker::class.java
        val request = PeriodicWorkRequest.Builder(worker, interval.hours.toLong(), TimeUnit.HOURS)
            .setInitialDelay(interval.hours.toLong(), TimeUnit.HOURS).addTag(name(kind)).build()
        manager.enqueueUniquePeriodicWork(name(kind), ExistingPeriodicWorkPolicy.KEEP, request).await()
    }
}

@Singleton
class ReminderCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
) {
    private val mutex = Mutex()
    private val manager get() = WorkManager.getInstance(context)
    private val scheduler get() = ReminderScheduler(manager)
    private val preferences get() = DataStoreManage.getInstance(context)

    suspend fun enabled(): Map<ReminderKind, Boolean> = withContext(Dispatchers.IO) {
        ReminderKind.values().associateWith(::isEnabled)
    }

    suspend fun intervals(): Map<ReminderKind, ReminderInterval> = withContext(Dispatchers.IO) {
        ReminderKind.values().associateWith { kind ->
            ReminderInterval.fromHours(preferences.getReminderIntervalHours(kind.preferenceKey, kind.defaultInterval.hours))
        }
    }

    suspend fun setInterval(kind: ReminderKind, interval: ReminderInterval) = mutex.withLock {
        withContext(Dispatchers.IO) {
            preferences.saveReminderIntervalHours(kind.preferenceKey, interval.hours)
            if (isEnabled(kind)) {
                manager.cancelUniqueWork(scheduler.name(kind)).await()
                scheduler.apply(kind, enabled = true, interval = interval)
            }
        }
    }

    suspend fun setEnabled(kind: ReminderKind, enabled: Boolean) = mutex.withLock {
        withContext(Dispatchers.IO) {
            val dao = database.moveDao()
            if (kind == ReminderKind.WATER) dao.saveWaterReminderState(WaterReminderState(isSwitchChecked = enabled))
            else dao.saveMotivationNotificationState(MotivationNotificationState(isSwitchChecked = enabled))
            // Retire old non-unique jobs without cancelling the new unique request.
            manager.cancelAllWorkByTag(kind.legacyTag).await()
            val interval = ReminderInterval.fromHours(preferences.getReminderIntervalHours(kind.preferenceKey, kind.defaultInterval.hours))
            scheduler.apply(kind, enabled, interval)
            if (!enabled) NotificationManagerCompat.from(context).cancel(kind.notificationId)
        }
    }

    suspend fun reconcile() = mutex.withLock {
        withContext(Dispatchers.IO) {
            NotificationManagerCompat.from(context).cancel(1)
            ReminderKind.values().forEach { kind ->
                manager.cancelAllWorkByTag(kind.legacyTag).await()
                manager.cancelUniqueWork("reminder_v2_${kind.name}").await()
                val interval = ReminderInterval.fromHours(preferences.getReminderIntervalHours(kind.preferenceKey, kind.defaultInterval.hours))
                scheduler.apply(kind, isEnabled(kind), interval)
            }
        }
    }

    fun isEnabled(kind: ReminderKind): Boolean = when (kind) {
        ReminderKind.WATER -> database.moveDao().getWaterReminderState()?.isSwitchChecked ?: false
        ReminderKind.MOTIVATION -> database.moveDao().getMotivationNotificationState()?.isSwitchChecked ?: false
    }

    fun allowed(kind: ReminderKind? = null): Boolean {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= 26 && kind != null) {
            val channel = context.getSystemService(NotificationManager::class.java).getNotificationChannel(kind.channel)
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) return false
        }
        return true
    }

    /** Called on a Worker thread; revoked permission or an opt-out is a successful no-op. */
    fun deliver(kind: ReminderKind): Boolean = kotlinx.coroutines.runBlocking(Dispatchers.IO) {
        mutex.withLock { deliverLocked(kind) }
    }

    private fun deliverLocked(kind: ReminderKind): Boolean {
        if (!isEnabled(kind) || !allowed(kind)) return false
        val localized = LocaleHelper.wrap(context)
        val title = localized.getString(if (kind == ReminderKind.WATER) R.string.label_waterreminder else R.string.label_fitnessreminder)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel(kind.channel, title, NotificationManager.IMPORTANCE_DEFAULT))
        val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(context, kind.notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, kind.channel)
            .setContentTitle(title)
            .setContentText(localized.getString(if (kind == ReminderKind.WATER) R.string.label_drinkwater else R.string.label_dofitness))
            .setSmallIcon(R.drawable.ic_fitness_icon).setContentIntent(pending).setAutoCancel(true).build()
        return try { manager.notify(kind.notificationId, notification); true } catch (_: SecurityException) { false }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderDependencies { fun reminders(): ReminderCoordinator }
