package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentArgs
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TrainingDiscoveryNavigationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private fun nav(activity: HomeActivity) = (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    private fun await(scenario: ActivityScenario<HomeActivity>, condition: (HomeActivity) -> Boolean) {
        val deadline = android.os.SystemClock.elapsedRealtime() + 15000
        var done = false
        while (!done && android.os.SystemClock.elapsedRealtime() < deadline) {
            scenario.onActivity { done = condition(it) }
            if (!done) android.os.SystemClock.sleep(50)
        }
        assertTrue("Screen did not reach expected state", done)
        instrumentation.waitForIdleSync()
    }
    private fun capture(name: String) {
        instrumentation.waitForIdleSync()
        // Let the navigation transition commit its rendered frame before taking evidence.
        android.os.SystemClock.sleep(350)
        instrumentation.waitForIdleSync()
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "discovery-verification").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test fun localizedDiscoveryScreensRemainReadable() {
        val context = instrumentation.targetContext
        val preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val previous = preferences.getString("pref_language_code", null)
        val previousLocale = java.util.Locale.getDefault()
        val prefix = InstrumentationRegistry.getArguments().getString("capturePrefix", "")
        try {
            for (language in listOf("tr", "en", "de", "ru")) {
                preferences.edit().putString("pref_language_code", language).commit()
                ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                    await(scenario) { it.findViewById<View>(R.id.heroAction) != null }
                    capture("${prefix}home-$language")
                    scenario.onActivity { it.findViewById<View>(R.id.allRegions).performClick() }
                    await(scenario) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && it.findViewById<View>(R.id.bodyMap) != null }
                    capture("${prefix}body-$language")
                    scenario.onActivity { nav(it).popBackStack() }
                    await(scenario) { it.findViewById<View>(R.id.programBeginner) != null }
                    scenario.onActivity { it.findViewById<View>(R.id.programBeginner).performClick() }
                    await(scenario) { it.findViewById<RecyclerView>(R.id.listDaysOfWeek)?.findViewHolderForAdapterPosition(0) != null }
                    scenario.onActivity { it.findViewById<RecyclerView>(R.id.listDaysOfWeek).findViewHolderForAdapterPosition(0)!!.itemView.findViewById<View>(R.id.cardview1).performClick() }
                    await(scenario) { nav(it).currentDestination?.id == R.id.detailDayFragment && it.findViewById<View>(R.id.workoutTitle) != null }
                    capture("${prefix}day-$language")
                }
            }
        } finally {
            preferences.edit().apply {
                if (previous == null) remove("pref_language_code") else putString("pref_language_code", previous)
            }.commit()
            java.util.Locale.setDefault(previousLocale)
        }
    }

    @Test fun realHomeRegionAndDayFlowsRetainSelectionAndData() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            await(scenario) { it.findViewById<View>(R.id.heroAction) != null }
            capture("home")
            scenario.onActivity { it.findViewById<View>(R.id.heroAction).performClick() }
            await(scenario) { nav(it).currentDestination?.id in setOf(R.id.exerciseDetailFirstFragment, R.id.exerciseDetailSecondFragment, R.id.exerciseDetailThirdFragment) }
            scenario.onActivity { nav(it).popBackStack() }
            await(scenario) { it.findViewById<View>(R.id.programBeginner) != null }
            scenario.onActivity { it.findViewById<View>(R.id.programBeginner).performClick() }
            await(scenario) { (it.findViewById<RecyclerView>(R.id.listDaysOfWeek)?.adapter?.itemCount ?: 0) == 21 }
            await(scenario) { it.findViewById<RecyclerView>(R.id.listDaysOfWeek)?.findViewHolderForAdapterPosition(0) != null }
            scenario.onActivity { it.findViewById<RecyclerView>(R.id.listDaysOfWeek).findViewHolderForAdapterPosition(0)!!.itemView.findViewById<View>(R.id.cardview1).performClick() }
            await(scenario) { nav(it).currentDestination?.id == R.id.detailDayFragment && it.findViewById<View>(R.id.workoutTitle) != null }
            scenario.onActivity {
                val args = DetailDayFragmentArgs.fromBundle(nav(it).currentBackStackEntry!!.arguments!!)
                assertEquals(1, args.exerciseDayModel!!.day)
                assertTrue(args.exerciseNewList!!.isNotEmpty())
                assertEquals(args.exerciseNewList!!.size.toString(), it.findViewById<TextView>(R.id.txtWorkoutCount).text.toString())
                assertEquals(args.exerciseLevel.trim(), it.findViewById<TextView>(R.id.workoutTitle).text.toString())
            }
            capture("day-detail")
            scenario.onActivity { nav(it).popBackStack(R.id.id_homepage_fragment, false) }
            await(scenario) { it.findViewById<View>(R.id.allRegions) != null }
            scenario.onActivity { it.findViewById<View>(R.id.allRegions).performClick() }
            await(scenario) { it.findViewById<GridLayout>(R.id.regionGrid)?.childCount == 4 && nav(it).currentDestination?.id == R.id.bodyRegionFragment }
            scenario.onActivity {
                val grid = it.findViewById<GridLayout>(R.id.regionGrid)
                (0 until grid.childCount).map(grid::getChildAt).first { row -> row.tag == "arms" }.performClick()
            }
            capture("body-region")
            scenario.onActivity { it.findViewById<LinearLayout>(R.id.workoutList).getChildAt(0).performClick() }
            await(scenario) { nav(it).currentDestination?.id == R.id.detailDayFragment && it.findViewById<View>(R.id.workoutTitle) != null }
            scenario.onActivity {
                val args = DetailDayFragmentArgs.fromBundle(nav(it).currentBackStackEntry!!.arguments!!)
                assertNull(args.exerciseDayModel)
                assertTrue(args.exerciseNewList!!.all { move -> move.titleName == "arm" && move.level == 1 })
                nav(it).popBackStack()
            }
            await(scenario) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && it.findViewById<TextView>(R.id.regionTitle)?.text == it.getString(R.string.region_arms) }
            scenario.recreate()
            await(scenario) { it.findViewById<TextView>(R.id.regionTitle)?.text == it.getString(R.string.region_arms) }
        }
    }
    @Test fun warmupOpensOriginalThreeSetsWithoutRegionPicker() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            await(scenario) { it.findViewById<View>(R.id.warmupAction) != null }
            scenario.onActivity { it.findViewById<View>(R.id.warmupAction).performClick() }
            await(scenario) { it.findViewById<LinearLayout>(R.id.workoutList)?.childCount == 3 }
            scenario.onActivity {
                assertEquals(View.GONE, it.findViewById<View>(R.id.regionPicker).visibility)
                it.findViewById<LinearLayout>(R.id.workoutList).getChildAt(0).performClick()
            }
            await(scenario) { nav(it).currentDestination?.id == R.id.detailDayFragment }
            scenario.onActivity {
                val args = DetailDayFragmentArgs.fromBundle(nav(it).currentBackStackEntry!!.arguments!!)
                assertTrue(args.exerciseNewList!!.all { move -> move.titleName == "stretch" && move.level == 1 })
                nav(it).popBackStack()
            }
            await(scenario) { it.findViewById<LinearLayout>(R.id.workoutList)?.childCount == 3 }
            capture("warmup-original")
            scenario.onActivity { it.findViewById<View>(R.id.backButton).performClick() }
            await(scenario) { nav(it).currentDestination?.id == R.id.id_homepage_fragment }
        }
    }}
