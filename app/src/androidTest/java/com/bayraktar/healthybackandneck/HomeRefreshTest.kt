package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class HomeRefreshTest {
    private val inst = InstrumentationRegistry.getInstrumentation()
    private fun nav(a: HomeActivity) = (a.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    private fun await(s: ActivityScenario<HomeActivity>, predicate: (HomeActivity) -> Boolean) {
        val until = android.os.SystemClock.elapsedRealtime() + 15000
        var done = false
        while (!done && android.os.SystemClock.elapsedRealtime() < until) {
            s.onActivity { done = predicate(it) }
            if (!done) android.os.SystemClock.sleep(50)
        }
        assertTrue("Expected destination/layout", done)
        inst.waitForIdleSync()
    }
    private fun capture(name: String) {
        android.os.SystemClock.sleep(350)
        inst.waitForIdleSync()
        val bitmap = inst.uiAutomation.takeScreenshot()
        val dir = File(inst.targetContext.getExternalFilesDir(null), "home-refresh").apply { mkdirs() }
        val prefix = InstrumentationRegistry.getArguments().getString("capturePrefix", "")
        File(dir, "$prefix$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
    @Test fun homeActionsAndBottomTabsReachExistingDestinations() {
        ActivityScenario.launch(HomeActivity::class.java).use { s ->
            await(s) { it.findViewById<View>(R.id.heroAction)?.height ?: 0 > 0 }
            for ((button, destination) in listOf(R.id.heroAction to R.id.programLevelFragment, R.id.allRegions to R.id.bodyRegionFragment, R.id.warmupAction to R.id.bodyRegionFragment, R.id.breathAction to R.id.breathFragment)) {
                s.onActivity { it.findViewById<View>(button).performClick() }
                await(s) { nav(it).currentDestination?.id == destination }
                if (button == R.id.warmupAction) s.onActivity { assertEquals(View.GONE, it.findViewById<View>(R.id.regionPicker).visibility) }
                if (button == R.id.allRegions) s.onActivity { assertEquals(View.VISIBLE, it.findViewById<View>(R.id.regionPicker).visibility) }
                s.onActivity { nav(it).popBackStack() }
                await(s) { nav(it).currentDestination?.id == R.id.id_homepage_fragment }
            }
            for (destination in listOf(R.id.moveListFragment, R.id.id_statistics_fragment, R.id.id_profile_fragment, R.id.id_homepage_fragment)) {
                s.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomBar).selectedItemId = destination }
                await(s) { nav(it).currentDestination?.id == destination }
            }
        }
    }
    @Test fun localizedHomeAndRegionScreensRemainReadable() {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext)
        val previous = prefs.getString("pref_language_code", null)
        val locale = java.util.Locale.getDefault()
        try {
            for (language in listOf("tr", "en", "de", "ru")) {
                prefs.edit().putString("pref_language_code", language).commit()
                ActivityScenario.launch(HomeActivity::class.java).use { s ->
                    await(s) { it.findViewById<View>(R.id.heroAction)?.height ?: 0 > 0 }
                    s.onActivity { a ->
                        for (id in listOf(R.id.homeMotivation, R.id.programTitle, R.id.regionCardTitle, R.id.wellnessTitle, R.id.heroAction, R.id.allRegions, R.id.warmupAction, R.id.breathAction)) {
                            val text = a.findViewById<TextView>(id)
                            assertTrue("Text height: " + text.text, text.height - text.compoundPaddingTop - text.compoundPaddingBottom >= text.layout.height)
                            for (line in 0 until text.lineCount) {
                                assertEquals(0, text.layout.getEllipsisCount(line))
                                assertTrue("Text width: " + text.text, text.layout.getLineMax(line) <= text.layout.width + 1)
                            }
                        }
                    }
                    capture("home-$language")
                    s.onActivity { a -> a.findViewById<NestedScrollView>(R.id.homeScroll).fullScroll(View.FOCUS_DOWN) }
                    capture("home-bottom-$language")
                    s.onActivity { it.findViewById<View>(R.id.allRegions).performClick() }
                    await(s) { nav(it).currentDestination?.id == R.id.bodyRegionFragment && (it.findViewById<View>(R.id.bodyMap)?.height ?: 0) > 0 }
                    capture("region-$language")
                }
            }
        } finally {
            prefs.edit().apply { if (previous == null) remove("pref_language_code") else putString("pref_language_code", previous) }.commit()
            java.util.Locale.setDefault(locale)
        }
    }
}
