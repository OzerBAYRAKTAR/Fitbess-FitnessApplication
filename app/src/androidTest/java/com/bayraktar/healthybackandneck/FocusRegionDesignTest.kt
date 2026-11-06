package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentArgs
import com.bayraktar.healthybackandneck.ui.HomeActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FocusRegionDesignTest {
    private val inst = InstrumentationRegistry.getInstrumentation()
    private fun nav(a: HomeActivity) = (a.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    private fun await(scenario: ActivityScenario<HomeActivity>, condition: (HomeActivity) -> Boolean) {
        val deadline = android.os.SystemClock.elapsedRealtime() + 15000
        var done = false
        while (!done && android.os.SystemClock.elapsedRealtime() < deadline) {
            scenario.onActivity { done = condition(it) }
            if (!done) android.os.SystemClock.sleep(50)
        }
        assertTrue("Expected region screen did not become ready", done)
        inst.waitForIdleSync()
    }

    private fun capture(name: String) {
        android.os.SystemClock.sleep(350)
        inst.waitForIdleSync()
        val bitmap = inst.uiAutomation.takeScreenshot()
        val dir = File(inst.targetContext.getExternalFilesDir(null), "focus-verification").apply { mkdirs() }
        val prefix = InstrumentationRegistry.getArguments().getString("capturePrefix", "")
        File(dir, "$prefix$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    private fun assertCards(a: HomeActivity) {
        val grid = a.findViewById<GridLayout>(R.id.regionGrid)
        assertEquals(4, grid.childCount)
        val cards = (0 until grid.childCount).map(grid::getChildAt)
        assertEquals(1, cards.map(View::getHeight).toSet().size)
        cards.forEach { card ->
            assertTrue(card.isClickable && card.isFocusable)
            assertTrue(card.height >= 48 * a.resources.displayMetrics.density)
            val label = card.findViewById<TextView>(R.id.regionLabel)
            assertTrue("Label height fits text", label.height >= label.layout.height)
            for (line in 0 until label.lineCount) {
                assertEquals(0, label.layout.getEllipsisCount(line))
                assertTrue("Label ${label.text} fits card width (${label.layout.getLineWidth(line)} <= ${label.width})", label.layout.getLineWidth(line) <= label.width)
            }
        }
    }

    @Test fun fourRegionRoutesKeepTheirOriginalWorkoutsAndSelection() {
        val mappings = linkedMapOf("arms" to ("arm" to 3), "back" to ("bellyneck" to 4), "abs" to ("abs" to 3), "legs" to ("legbutt" to 4))
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            await(scenario) { it.findViewById<View>(R.id.allRegions) != null }
            for ((key, mapping) in mappings) {
                scenario.onActivity { a ->
                    a.findViewById<GridLayout>(R.id.regionGrid).findViewWithTag<View>(key).performClick()
                }
                await(scenario) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && it.findViewById<LinearLayout>(R.id.workoutList)?.childCount == mapping.second }
                scenario.onActivity { a ->
                    assertTrue(a.findViewById<GridLayout>(R.id.regionGrid).findViewWithTag<View>(key).isSelected)
                    a.findViewById<LinearLayout>(R.id.workoutList).getChildAt(0).performClick()
                }
                await(scenario) { nav(it).currentDestination?.id == R.id.detailDayFragment }
                scenario.onActivity { a ->
                    val args = DetailDayFragmentArgs.fromBundle(nav(a).currentBackStackEntry!!.arguments!!)
                    assertNull(args.exerciseDayModel)
                    assertTrue(args.exerciseNewList!!.isNotEmpty())
                    assertTrue(args.exerciseNewList!!.all { it.titleName == mapping.first && it.level == 1 })
                    nav(a).popBackStack()
                }
                await(scenario) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && it.findViewById<LinearLayout>(R.id.workoutList)?.childCount == mapping.second }
                scenario.recreate()
                await(scenario) { it.findViewById<GridLayout>(R.id.regionGrid)?.findViewWithTag<View>(key)?.isSelected == true }
                scenario.onActivity { nav(it).popBackStack() }
                await(scenario) { nav(it).currentDestination?.id == R.id.id_homepage_fragment }
            }
        }
    }

    @Test fun localizedPhotoCardsAndSideBySidePickerFit() {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext)
        val oldLanguage = prefs.getString("pref_language_code", null)
        val oldLocale = java.util.Locale.getDefault()
        try {
            for (language in listOf("tr", "en", "de", "ru")) {
                prefs.edit().putString("pref_language_code", language).commit()
                ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                    await(scenario) { it.findViewById<GridLayout>(R.id.regionGrid)?.height ?: 0 > 0 }
                    scenario.onActivity { a ->
                        assertCards(a)
                        val grid = a.findViewById<GridLayout>(R.id.regionGrid)
                        var parent = grid.parent
                        while (parent !is NestedScrollView) parent = parent.parent
                        (parent as NestedScrollView).scrollTo(0, grid.top - (56 * a.resources.displayMetrics.density).toInt())
                    }
                    capture("home-$language")
                    scenario.onActivity { it.findViewById<View>(R.id.allRegions).performClick() }
                    await(scenario) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && (it.findViewById<GridLayout>(R.id.regionGrid)?.height ?: 0) > 0 }
                    scenario.onActivity { a ->
                        assertCards(a)
                        val hero = a.findViewById<View>(R.id.bodyMap)
                        val grid = a.findViewById<GridLayout>(R.id.regionGrid)
                        assertTrue("Hero remains on the left", hero.right <= grid.left)
                        assertEquals(1, grid.columnCount)
                        assertTrue(hero.width > 0 && hero.height > 0)
                        grid.findViewWithTag<View>("legs").performClick()
                        assertTrue(grid.findViewWithTag<View>("legs").isSelected)
                        assertFalse(grid.findViewWithTag<View>("back").isSelected)
                    }
                    capture("picker-$language")
                }
            }
        } finally {
            prefs.edit().apply { if (oldLanguage == null) remove("pref_language_code") else putString("pref_language_code", oldLanguage) }.commit()
            java.util.Locale.setDefault(oldLocale)
        }
    }
}
