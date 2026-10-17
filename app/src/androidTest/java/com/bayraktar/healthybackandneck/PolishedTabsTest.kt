package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class PolishedTabsTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private fun host(activity: HomeActivity) = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    @Test fun fourTabsShareTurkishLocaleAndLanguageSelectionUpdatesDates() {
        val context = instrumentation.targetContext
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val previous = prefs.getString("pref_language_code", null)
        val previousLocale = Locale.getDefault()
        val database = androidx.room.Room.databaseBuilder(context, com.bayraktar.healthybackandneck.data.Room.AppDatabase::class.java, "fitness_db")
            .addMigrations(com.bayraktar.healthybackandneck.data.Room.AppDatabase.MIGRATION_11_12).build()
        val beforeFavorites = database.moveDao().getIsFavouriteTrue().map { it.id }.toSet()
        val favorite = move(-30505, 1).copy(exerciseName = "Verification favorite", isFavourite = true)
        try {
            assertFalse(beforeFavorites.contains(favorite.id))
            database.moveDao().insertExerciseDayExercise(listOf(favorite))
            LanguagePreference.setLanguageCode(context, "en")
            LocaleHelper.setLocale(context, "en")
            ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                scenario.onActivity { host(it).navController.navigate(R.id.id_profile_fragment) }
                instrumentation.waitForIdleSync()
                scenario.onActivity { it.findViewById<View>(R.id.consLanguage).performClick() }
                onView(withText("Türkçe")).perform(click())
                instrumentation.waitForIdleSync()
                scenario.onActivity { assertEquals("tr", Locale.getDefault().language); assertEquals("Profil", it.getString(R.string.profile)) }
                capture("profile")
                val tabs = listOf(R.id.id_homepage_fragment to "home", R.id.moveListFragment to "favorites", R.id.id_statistics_fragment to "progress")
                tabs.forEach { (id, name) ->
                    scenario.onActivity { host(it).navController.navigate(id) }
                    instrumentation.waitForIdleSync()
                    if (name == "favorites") awaitView(scenario, R.id.favoriteCount) {
                        (it as TextView).text.toString() != it.context.getString(R.string.access_loading)
                    }
                    if (name == "favorites") scenario.onActivity {
                        val rows = it.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvdaydetail)
                        assertTrue(rows.adapter!!.itemCount >= 1)
                    }
                    SystemClock.sleep(300)
                    capture(name)
                }
                scenario.onActivity { activity ->
                    assertTrue(activity.findViewById<View>(R.id.weekBars).isShown)
                    assertEquals(7, activity.findViewById<android.widget.LinearLayout>(R.id.weekBars).childCount)
                }
            }
            assertEquals(beforeFavorites + favorite.id, database.moveDao().getIsFavouriteTrue().map { it.id }.toSet())
        } finally {
            database.openHelper.writableDatabase.execSQL("DELETE FROM exercise_day_exercise WHERE id = ?", arrayOf(favorite.id))
            database.close()
            if (previous == null) prefs.edit().remove("pref_language_code").commit() else prefs.edit().putString("pref_language_code", previous).commit()
            Locale.setDefault(previousLocale)
        }
    }

    @Test fun lockedCatalogIsEnterableButCannotStartAndFreeMovesCanStart() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            val locked = arrayOf(move(-20512, 2), move(-20513, 2))
            scenario.onActivity { host(it).navController.navigate(R.id.detailDayFragment, args(locked)) }
            awaitView(scenario, R.id.accessStatus) { it.visibility == View.GONE }
            scenario.onActivity {
                assertFalse(it.findViewById<View>(R.id.startExercise).isEnabled)
                val rows = it.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvdaydetail)
                assertEquals(2, rows.adapter!!.itemCount)
                assertEquals(View.VISIBLE, rows.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<View>(R.id.lockBadge).visibility)
            }
            capture("locked-moves")
            scenario.onActivity { host(it).navController.popBackStack() }
            instrumentation.waitForIdleSync()
            scenario.onActivity { host(it).navController.navigate(R.id.detailDayFragment, args(arrayOf(move(505, 1)))) }
            awaitView(scenario, R.id.startExercise) { it.isEnabled }
            capture("free-moves")
            scenario.onActivity {
                // Verify the guarded player route independently of interstitial availability.
                host(it).navController.navigate(R.id.exerciseMovesFragment, args(arrayOf(move(505, 1))))
            }
            instrumentation.waitForIdleSync()
            scenario.moveToState(Lifecycle.State.CREATED)
            SystemClock.sleep(200)
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { assertTrue(it.findViewById<View>(R.id.goNextbtn).isEnabled) }
        }
    }
    private fun move(id: Int, level: Int) = ExerciseDayExercise(id, id, 1, "stretch", 0, "Verification move", "Test", R.drawable.e52_a11_lying_shoulder_extension, false, level)
    private fun args(moves: Array<ExerciseDayExercise>) = Bundle().apply {
        putParcelableArray("exerciseNewList", moves); putParcelable("exerciseDayModel", null); putString("exerciseLevel", "Verification")
    }
    private fun awaitView(scenario: ActivityScenario<HomeActivity>, id: Int, predicate: (View) -> Boolean) {
        val end = SystemClock.elapsedRealtime() + 10000
        var ready = false
        while (!ready && SystemClock.elapsedRealtime() < end) {
            scenario.onActivity { ready = it.findViewById<View>(id)?.let(predicate) == true }
            if (!ready) SystemClock.sleep(50)
        }
        assertTrue("View did not become ready: $id", ready)
    }
    private fun capture(name: String) {
        instrumentation.waitForIdleSync()
        val image = instrumentation.uiAutomation.takeScreenshot()
        File(instrumentation.targetContext.cacheDir, "polish-$name.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
        image.recycle()
    }
}
