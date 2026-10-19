package com.bayraktar.healthybackandneck

import android.content.Intent
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.data.Models.WaterReminderState
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.utils.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProfileAndCatalogTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun earnedModelIsPersistedIdempotentlyWithoutUnlockingAnotherModel() = runBlocking {
        val manager = DataStoreManage.getInstance(context)
        val id = -10512
        val before = withTimeout(5000) { manager.unlockedMoveIds().first() }
        assertFalse(id in before)
        try {
            manager.unlockMove(id); manager.unlockMove(id)
            val restored = withTimeout(5000) { DataStoreManage.getInstance(context).unlockedMoveIds().first { id in it } }
            assertEquals(before + id, restored)
            assertFalse(-10513 in restored)
        } finally {
            @Suppress("UNCHECKED_CAST")
            val store = DataStoreManage::class.java.getDeclaredField("dataStore").apply { isAccessible = true }.get(manager) as DataStore<Preferences>
            store.edit { prefs ->
                val key = stringSetPreferencesKey("earned_catalog_move_ids_v1")
                prefs[key] = prefs[key].orEmpty() - id.toString()
            }
        }
    }

    @Test fun remindersAreUniqueAndCancellationLeavesNoActiveWork() = runBlocking {
        val work = WorkManager.getInstance(context)
        val scheduler = ReminderScheduler(work, "verification_${UUID.randomUUID()}_")
        try {
            repeat(3) { scheduler.apply(ReminderKind.WATER, true) }
            val enabled = workStates(work, scheduler.name(ReminderKind.WATER)) { list -> list.count { !it.state.isFinished } == 1 }
            assertEquals(1, enabled.count { !it.state.isFinished })
            scheduler.apply(ReminderKind.WATER, false)
            val stopped = workStates(work, scheduler.name(ReminderKind.WATER)) { list -> list.all { it.state.isFinished } }
            assertTrue(stopped.all { it.state.isFinished })
        } finally { scheduler.apply(ReminderKind.WATER, false) }
    }

    @Test fun reminderCadencesAreLimitedToDailyTwelveOrSixHours() {
        assertEquals(listOf(24, 12, 6), ReminderInterval.entries.map { it.hours })
        assertEquals(ReminderInterval.EVERY_6_HOURS, ReminderInterval.fromHours(4))
        assertEquals(ReminderInterval.DAILY, ReminderInterval.fromHours(24))
    }

    @Test fun deniedNotificationPermissionAndDisabledPreferenceAreSafeNoOps() {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val denied = object : ContextWrapper(context) {
                override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
                    if (permission == android.Manifest.permission.POST_NOTIFICATIONS) PackageManager.PERMISSION_DENIED else super.checkPermission(permission, pid, uid)
            }
            val coordinator = ReminderCoordinator(denied, database)
            assertFalse(coordinator.deliver(ReminderKind.WATER))
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                database.moveDao().saveWaterReminderState(WaterReminderState(isSwitchChecked = true))
                assertFalse(coordinator.allowed(ReminderKind.WATER))
                assertFalse(coordinator.deliver(ReminderKind.WATER))
            }
            assertNotEquals(ReminderKind.WATER.notificationId, ReminderKind.MOTIVATION.notificationId)
        } finally { database.close() }
    }

    @Test fun enabledRemindersDeliverDistinctNotificationsWithAppTapIntents() {
        val notifications = context.getSystemService(android.app.NotificationManager::class.java)
        val ids = setOf(ReminderKind.WATER.notificationId, ReminderKind.MOTIVATION.notificationId)
        assertTrue("Existing reminder notifications must be preserved before this test", notifications.activeNotifications.none { it.id in ids })
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val coordinator = ReminderCoordinator(context, database)
            assertTrue("Grant notification permission for delivery verification", coordinator.allowed(ReminderKind.WATER))
            assertTrue("Motivation channel must allow notifications", coordinator.allowed(ReminderKind.MOTIVATION))
            database.moveDao().saveWaterReminderState(WaterReminderState(isSwitchChecked = true))
            database.moveDao().saveMotivationNotificationState(com.bayraktar.healthybackandneck.data.Models.MotivationNotificationState(isSwitchChecked = true))
            try {
                assertTrue(coordinator.deliver(ReminderKind.WATER))
                assertTrue(coordinator.deliver(ReminderKind.MOTIVATION))
                val deadline = android.os.SystemClock.elapsedRealtime() + 5000
                var delivered = notifications.activeNotifications.filter { it.id in ids }
                while (delivered.map { it.id }.toSet() != ids && android.os.SystemClock.elapsedRealtime() < deadline) {
                    android.os.SystemClock.sleep(50)
                    delivered = notifications.activeNotifications.filter { it.id in ids }
                }
                assertEquals("Both enqueue requests must reach NotificationManager", ids, delivered.map { it.id }.toSet())
                delivered.forEach {
                    assertEquals(context.packageName, it.notification.contentIntent.creatorPackage)
                    assertTrue(it.notification.flags and android.app.Notification.FLAG_AUTO_CANCEL != 0)
                    if (android.os.Build.VERSION.SDK_INT >= 31) assertTrue(it.notification.contentIntent.isActivity)
                }
            } finally { ids.forEach(notifications::cancel) }
        } finally { database.close() }
    }

    @Test fun rateAndShareUsePublishedPackageWithoutSendingAnything() {
        assertEquals("market://details?id=${context.packageName}", ProfileIntents.store(context).data.toString())
        assertEquals("https://play.google.com/store/apps/details?id=${context.packageName}", ProfileIntents.store(context, true).data.toString())
        val share = ProfileIntents.share(context)
        assertEquals(Intent.ACTION_SEND, share.action)
        assertEquals("text/plain", share.type)
        assertTrue(share.getStringExtra(Intent.EXTRA_TEXT)!!.contains("https://play.google.com/store/apps/details?id=${context.packageName}"))
    }

    private fun workStates(manager: WorkManager, name: String, predicate: (List<WorkInfo>) -> Boolean): List<WorkInfo> {
        val live = manager.getWorkInfosForUniqueWorkLiveData(name)
        val latch = CountDownLatch(1)
        var result = emptyList<WorkInfo>()
        val observer = Observer<List<WorkInfo>> { list -> if (predicate(list)) { result = list; latch.countDown() } }
        instrumentation.runOnMainSync { live.observeForever(observer) }
        try { assertTrue("Work state timed out: $name", latch.await(10, TimeUnit.SECONDS)); return result }
        finally { instrumentation.runOnMainSync { live.removeObserver(observer) } }
    }
}
