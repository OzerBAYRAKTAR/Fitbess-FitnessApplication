package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import com.bayraktar.healthybackandneck.ui.HomeActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.File

@RunWith(AndroidJUnit4::class)
class PlayerLayoutMediaTest {
    private val inst = InstrumentationRegistry.getInstrumentation()
    private var oldLanguage: String? = null
    private val oldLocale = java.util.Locale.getDefault()
    @org.junit.Before fun chooseLanguage() {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext)
        oldLanguage = prefs.getString("pref_language_code", null)
        prefs.edit().putString("pref_language_code", InstrumentationRegistry.getArguments().getString("language", "en")).commit()
    }
    @org.junit.After fun restoreLanguage() {
        android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext).edit().apply {
            if (oldLanguage == null) remove("pref_language_code") else putString("pref_language_code", oldLanguage)
        }.commit()
        java.util.Locale.setDefault(oldLocale)
    }
    @Test fun equalRegionCardsAndResponsiveAnimatedPlayer() {
        val context = inst.targetContext
        val moves = WorkoutCatalogParser.readExercises(context, 1).filter { it.dayId == 3 }
        val index = moves.indexOfFirst { it.exerciseName == "Crab Walk" }
        assertTrue(index >= 0)
        val staleMoves = moves.map { it.copy(image = R.drawable.home_training_hero) }.toTypedArray()
        val day = WorkoutCatalogParser.readDays(context, "firstday.json").first { it.day == 3 }
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            inst.waitForIdleSync()
            android.os.SystemClock.sleep(700)
            scenario.onActivity { activity ->
                val grid = activity.findViewById<GridLayout>(R.id.regionGrid)
                assertTrue(grid.childCount > 1)
                val heights = (0 until grid.childCount).map { grid.getChildAt(it).height }.toSet()
                assertEquals("All region cards have one height", 1, heights.size)
                for (child in 0 until grid.childCount) {
                    val label = grid.getChildAt(child).findViewById<TextView>(R.id.regionLabel)
                    assertTrue(label.height >= label.layout.height)
                    for (line in 0 until label.lineCount) assertEquals(0, label.layout.getEllipsisCount(line))
                }
                val nav = (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
                nav.navigate(R.id.exerciseMovesFragment2, Bundle().apply {
                    putInt("exerciseIndex", index); putParcelableArray("exerciseNewList", staleMoves); putParcelable("exerciseDayModel", day)
                })
            }
            inst.waitForIdleSync()
            android.os.SystemClock.sleep(500)
            var controlsY = 0
            scenario.onActivity { activity ->
                val gif = activity.findViewById<GifImageView>(R.id.moveGif)
                assertTrue(gif.drawable is GifDrawable)
                assertTrue((gif.drawable as GifDrawable).numberOfFrames > 1)
                assertEquals("Crab Walk", activity.findViewById<TextView>(R.id.exerciceName).text.toString())
                val title = activity.findViewById<View>(R.id.exerciceName)
                val heart = activity.findViewById<View>(R.id.favoritebtn)
                assertTrue(heart.left >= title.right)
                assertTrue(gif.width > 0 && gif.height > 0)
                val controls = activity.findViewById<View>(R.id.playerControls)
                val position = IntArray(2); controls.getLocationOnScreen(position); controlsY = position[1]
                activity.findViewById<NestedScrollView>(R.id.playerScroll).fullScroll(View.FOCUS_DOWN)
                activity.findViewById<View>(R.id.playpause).performClick()
            }
            inst.waitForIdleSync()
            scenario.onActivity { activity ->
                val position = IntArray(2); activity.findViewById<View>(R.id.playerControls).getLocationOnScreen(position)
                assertEquals("Controls stay fixed while content scrolls", controlsY, position[1])
                val pause = activity.findViewById<View>(R.id.playpause)
                assertTrue(pause.isShown)
                val bounds = android.graphics.Rect()
                assertTrue(pause.getGlobalVisibleRect(bounds))
                assertEquals(pause.height, bounds.height())
                assertEquals(activity.getString(R.string.resume_workout), pause.contentDescription)
                val timer = activity.findViewById<TextView>(R.id.txtTimeLeft)
                assertTrue(timer.layout.getLineWidth(0) <= timer.width)
            }
            val bitmap = inst.uiAutomation.takeScreenshot()
            val folder = File(context.getExternalFilesDir(null), "media-layout-verification").apply { mkdirs() }
            val size = "${context.resources.displayMetrics.widthPixels}-${context.resources.configuration.fontScale}"
            File(folder, "player-$size.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }
}
