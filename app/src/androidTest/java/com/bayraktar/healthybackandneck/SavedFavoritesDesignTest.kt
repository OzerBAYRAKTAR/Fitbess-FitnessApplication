package com.bayraktar.healthybackandneck

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import com.bayraktar.healthybackandneck.data.Repository.SubExerciseCatalog
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList.MoveListFragment
import com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList.MoveListViewModel
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.MoveAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SavedFavoritesDesignTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val variant get() = InstrumentationRegistry.getArguments().getString("variant") ?: "normal"
    private fun host(activity: HomeActivity) = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    private fun model(activity: HomeActivity): MoveListViewModel = ViewModelProvider(host(activity).childFragmentManager.primaryNavigationFragment!!)[MoveListViewModel::class.java]

    @Test fun actualSavedTabLegacyPreviewDedupeRemovalRestorationAndFourLocales() {
        val languagePrefs = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        val previousLanguage = LanguagePreference.getLanguageCode(context)
        val previousLocale = Locale.getDefault()
        val store = DataStoreManage.getInstance(context)
        val earnedBefore = runBlocking { store.unlockedMoveIds().first() }
        try {
            for (language in listOf("tr", "en", "de", "ru")) {
                LanguagePreference.setLanguageCode(context, language)
                val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
                try {
                    ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                        scenario.onActivity { activity ->
                            installFactory(activity, db)
                            activity.findViewById<View>(R.id.moveListFragment).performClick()
                        }
                        await(scenario) { host(it).navController.currentDestination?.id == R.id.moveListFragment && model(it).state.value?.loaded == true }
                        scenario.onActivity {
                            assertTrue(model(it).state.value!!.uniqueRecords.isEmpty())
                            assertEquals(R.id.moveListFragment, it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomBar).selectedItemId)
                        }
                        scroll(scenario, 0)
                        capture(scenario, "empty-header-$language")
                        scroll(scenario, 1)
                        capture(scenario, "empty-card-$language")
                        val fixture = fixtures(language)
                        runBlocking(Dispatchers.IO) { db.moveDao().insertExerciseDayExercise(fixture) }
                        scenario.onActivity { model(it).getFavExerciseList() }
                        await(scenario) { model(it).state.value?.let { state -> !state.loading && state.uniqueRecords.size == 3 } == true }
                        scenario.onActivity {
                            val records = model(it).state.value!!.uniqueRecords
                            assertEquals(3, records.map { row -> row.exerciseId }.distinct().size)
                            assertEquals(5, it.findViewById<RecyclerView>(R.id.rvdaydetail).adapter!!.itemCount)
                        }
                        scroll(scenario, 0)
                        capture(scenario, "populated-header-$language")
                        scroll(scenario, 1)
                        capture(scenario, "populated-rows-$language")
                        assertNoAccessChrome(scenario)
                        val locked = fixture.first { it.exerciseId == 86 }
                        assertFalse(MoveAccess.allowed(locked, emptySet(), false))
                        var lockedPosition = 0
                        scenario.onActivity { lockedPosition = model(it).state.value!!.uniqueRecords.indexOfFirst { row -> row.exerciseId == locked.exerciseId } + 1 }
                        scroll(scenario, lockedPosition)
                        clickRow(scenario, lockedPosition, R.id.inspectFavorite)
                        onView(withId(R.id.exerciseFavouriteTitle)).check(matches(withText(locked.exerciseName)))
                        onView(withText(R.string.watch_unlock)).check(doesNotExist())
                        scenario.onActivity { assertEquals(R.id.moveListFragment, host(it).navController.currentDestination!!.id) }
                        capture(scenario, "read-only-info-$language")
                        onView(withId(android.R.id.button1)).perform(click())
                        assertEquals(earnedBefore, runBlocking { store.unlockedMoveIds().first() })
                        clickRow(scenario, lockedPosition, R.id.inspectFavorite)
                        onView(withId(R.id.exerciseFavouriteTitle)).check(matches(isDisplayed()))
                        pressBack()
                        scenario.onActivity { assertEquals(R.id.moveListFragment, host(it).navController.currentDestination!!.id) }
                        if (language == "tr") {
                            // Removing this exerciseId must update both its program and catalog copies.
                            scroll(scenario, 1)
                            clickRow(scenario, 1, R.id.deleteBtn)
                            onView(withId(R.id.dialogNoFV)).perform(click())
                            assertEquals(4, runBlocking(Dispatchers.IO) { db.moveDao().getIsFavouriteTrue().size })
                            clickRow(scenario, 1, R.id.deleteBtn)
                            onView(withId(R.id.dialogYesFV)).perform(click())
                            await(scenario) { model(it).state.value?.let { state -> state.removing == null && state.uniqueRecords.size == 2 } == true }
                            assertTrue(runBlocking(Dispatchers.IO) { db.moveDao().getIsFavouriteTrue().none { it.exerciseId == 29 } })
                            scenario.recreate()
                            scenario.onActivity { installFactory(it, db) }
                            await(scenario) { model(it).state.value?.let { state -> !state.loading && state.uniqueRecords.size == 2 } == true }
                            for (remaining in listOf(1, 0)) {
                                scroll(scenario, 1)
                                clickRow(scenario, 1, R.id.deleteBtn)
                                onView(withId(R.id.dialogYesFV)).perform(click())
                                await(scenario) { model(it).state.value?.let { state -> state.removing == null && state.uniqueRecords.size == remaining } == true }
                            }
                            assertTrue(runBlocking(Dispatchers.IO) { db.moveDao().getIsFavouriteTrue().isEmpty() })
                            scroll(scenario, 1)
                            clickRow(scenario, 1, R.id.btnCheck)
                            await(scenario) { host(it).navController.currentDestination?.id == R.id.id_homepage_fragment }
                            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                            onView(withText(R.string.alertmessage)).check(matches(isDisplayed()))
                            onView(withId(android.R.id.button2)).perform(click())
                            scenario.onActivity { activity ->
                                assertEquals(R.id.id_homepage_fragment, host(activity).navController.currentDestination!!.id)
                                activity.findViewById<View>(R.id.moveListFragment).performClick()
                            }
                            await(scenario) { host(it).navController.currentDestination?.id == R.id.moveListFragment && model(it).state.value?.let { state -> state.loaded && !state.loading } == true }
                            scenario.onActivity { assertTrue(model(it).state.value!!.uniqueRecords.isEmpty()) }
                        }
                    }
                } finally { db.close() }
            }
            assertEquals("Inspect and remove must not grant any persisted entitlement", earnedBefore, runBlocking { store.unlockedMoveIds().first() })
        } finally {
            if (previousLanguage == null) languagePrefs.edit().remove("pref_language_code").commit()
            else languagePrefs.edit().putString("pref_language_code", previousLanguage).commit()
            Locale.setDefault(previousLocale)
        }
    }

    private fun installFactory(activity: HomeActivity, db: AppDatabase) {
                            host(activity).childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                                override fun onFragmentPreCreated(fm: FragmentManager, fragment: Fragment, state: Bundle?) {
                                    if (fragment is MoveListFragment) {
                                        val factory = object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(type: Class<T>): T = MoveListViewModel(RoomRepository(db.moveDao(), SubExerciseCatalog(context, db.moveDao()))) as T
                                        }
                                        ViewModelProvider(fragment, factory)[MoveListViewModel::class.java]
                                    }
                                }
                            }, false)
    }
    private fun fixtures(language: String): List<ExerciseDayExercise> {
        val localized = context.createConfigurationContext(android.content.res.Configuration(context.resources.configuration).apply { setLocale(Locale(language)) })
        val catalog = JSONArray(context.assets.open("subExercises.json").bufferedReader().use { it.readText() })
        val rows = listOf(586, 518, 606).map { id ->
            val value = (0 until catalog.length()).map(catalog::getJSONObject).first { it.getInt("id") == id }
            ExerciseDayExercise(id, value.getInt("exerciseId"), 0, value.getString("titleName"), 0,
                value.getString("exerciseName"), localized.getString(context.resources.getIdentifier(value.getString("exerciseDescription"), "string", context.packageName)),
                0, false, value.getInt("level"), isFavourite = true)
        }
        return rows + rows.first().copy(id = 101, dayId = 1, level = 1)
    }
    private fun await(scenario: ActivityScenario<HomeActivity>, predicate: (HomeActivity) -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + 30000
        while (true) {
            var ready = false
            scenario.onActivity { ready = predicate(it) }
            if (ready) return
            if (SystemClock.elapsedRealtime() >= deadline) fail("Timed out waiting for the saved favorites UI")
            SystemClock.sleep(50)
        }
    }
    private fun scroll(scenario: ActivityScenario<HomeActivity>, position: Int) {
        await(scenario) { activity -> activity.findViewById<RecyclerView>(R.id.rvdaydetail).let { list ->
            list.isLaidOut && !list.hasPendingAdapterUpdates() && !list.isComputingLayout
        } }
        scenario.onActivity { activity -> (activity.findViewById<RecyclerView>(R.id.rvdaydetail).layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0) }
        instrumentation.waitForIdleSync()
        await(scenario) { activity -> activity.findViewById<RecyclerView>(R.id.rvdaydetail).let { list ->
            val item = list.findViewHolderForAdapterPosition(position)?.itemView
            !list.isLayoutRequested && !list.hasPendingAdapterUpdates() && item?.isLaidOut == true &&
                (position != 0 || item.top == list.paddingTop)
        } }
    }
    private fun clickRow(scenario: ActivityScenario<HomeActivity>, position: Int, id: Int) {
        scenario.onActivity { it.findViewById<RecyclerView>(R.id.rvdaydetail).findViewHolderForAdapterPosition(position)!!.itemView.findViewById<View>(id).performClick() }
        instrumentation.waitForIdleSync()
    }
    private fun assertNoAccessChrome(scenario: ActivityScenario<HomeActivity>) {
        scenario.onActivity { activity ->
            val forbidden = listOf(R.string.move_locked, R.string.move_unlocked, R.string.watch_unlock).map(activity::getString)
            fun check(view: View) {
                if (view is TextView) assertFalse("Saved list must not display access status", view.text.toString() in forbidden)
                if (view is ViewGroup) for (index in 0 until view.childCount) check(view.getChildAt(index))
            }
            check(activity.findViewById(R.id.favoritesRoot))
            assertNull(activity.findViewById<View>(R.id.lockBadge))
        }
    }
    private fun capture(scenario: ActivityScenario<HomeActivity>, name: String) {
        val drawn = CountDownLatch(1)
        scenario.onActivity { activity ->
            val decor = activity.window.decorView
            val listener = object : android.view.ViewTreeObserver.OnDrawListener {
                override fun onDraw() {
                    decor.post {
                        if (decor.viewTreeObserver.isAlive) decor.viewTreeObserver.removeOnDrawListener(this)
                        decor.postOnAnimation { drawn.countDown() }
                    }
                }
            }
            decor.viewTreeObserver.addOnDrawListener(listener)
            decor.invalidate()
        }
        assertTrue(drawn.await(15, TimeUnit.SECONDS))
        instrumentation.waitForIdleSync()
        instrumentation.uiAutomation.waitForIdle(100, 5000)
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(context.cacheDir, "favorites-$variant-$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
