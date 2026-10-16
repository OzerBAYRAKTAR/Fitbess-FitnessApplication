package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import android.os.CountDownTimer
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragment
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragment
import com.bayraktar.healthybackandneck.ui.ExerciseMovesRest.ExerciseMovesRestFragment
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutNavigationTest {
    private val day = ExerciseDay(-900, -900, -900, 7, 4, 8, false, -900)
    private val exercises = Array(7) { index ->
        ExerciseDayExercise(-900 - index, -900 - index, index, "Regression", -900,
            "Test exercise ${index + 1}", "Synthetic exercise", R.drawable.placehlder, false, -900)
    }

    @Test fun readyTimerAndButtonBothStartAtFirstExerciseAndNavigateOnce() = withHome { host ->
        host.navController.navigate(R.id.exerciseMovesFragment, args())
        host.childFragmentManager.executePendingTransactions()
        val ready = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesReadyFragment
        val timer = timer(ready)
        ready.binding.goNextbtn.performClick()
        ready.binding.goNextbtn.performClick()
        timer.onFinish()
        host.childFragmentManager.executePendingTransactions()
        assertEquals(R.id.exerciseMovesFragment2, host.navController.currentDestination?.id)
        assertEquals(0, host.navController.currentBackStackEntry!!.arguments!!.getInt("exerciseIndex"))
    }

    @Test fun readyTimerStartsAtFirstExercise() = withHome { host ->
        host.navController.navigate(R.id.exerciseMovesFragment, args())
        host.childFragmentManager.executePendingTransactions()
        val ready = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesReadyFragment
        timer(ready).onFinish()
        host.childFragmentManager.executePendingTransactions()
        assertEquals(R.id.exerciseMovesFragment2, host.navController.currentDestination?.id)
        assertEquals(0, host.navController.currentBackStackEntry!!.arguments!!.getInt("exerciseIndex"))
    }

    @Test fun rapidNextSkipAndStaleTimersReachSeventhExerciseAndEndOnce() = withHome { host ->
        host.navController.navigate(R.id.exerciseMovesFragment2, args(0))
        host.childFragmentManager.executePendingTransactions()
        repeat(7) { index ->
            val move = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesFragment
            assertEquals((index + 1).toString(), move.binding.txtRank.text.toString())
            val button = move.binding.goNext
            val oldTimer = timer(move)
            button.performClick()
            button.performClick()
            oldTimer.onFinish()
            host.childFragmentManager.executePendingTransactions()
            if (index < 6) {
                assertEquals(R.id.exerciseMovesRestFragment, host.navController.currentDestination?.id)
                val rest = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesRestFragment
                val skip = rest.binding.skipRest
                val restTimer = timer(rest)
                // Alternate which event wins; both must lead to the same next index.
                if (index % 2 == 0) skip.performClick() else restTimer.onFinish()
                skip.performClick()
                restTimer.onFinish()
                host.childFragmentManager.executePendingTransactions()
                assertEquals(R.id.exerciseMovesFragment2, host.navController.currentDestination?.id)
                assertEquals(index + 1, host.navController.currentBackStackEntry!!.arguments!!.getInt("exerciseIndex"))
                // Even after the next view is active, callbacks from the departed view do nothing.
                restTimer.onFinish()
            }
        }
        assertEquals(R.id.exerciseMovesEndFragment, host.navController.currentDestination?.id)
    }

    @Test fun backFromRestCancelsDepartedViewTimer() = withHome { host ->
        host.navController.navigate(R.id.exerciseMovesRestFragment, args(5))
        host.childFragmentManager.executePendingTransactions()
        val rest = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesRestFragment
        val oldTimer = timer(rest)
        assertTrue(host.navController.popBackStack())
        host.childFragmentManager.executePendingTransactions()
        oldTimer.onFinish()
        assertEquals(R.id.id_homepage_fragment, host.navController.currentDestination?.id)
    }

    @Test fun pausedScreenResumesTimerWithoutLosingCompletion() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            var ready: ExerciseMovesReadyFragment? = null
            var oldTimer: CountDownTimer? = null
            scenario.onActivity { activity ->
                val host = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                host.navController.navigate(R.id.exerciseMovesFragment, args())
                host.childFragmentManager.executePendingTransactions()
                ready = host.childFragmentManager.primaryNavigationFragment as ExerciseMovesReadyFragment
                oldTimer = timer(ready!!)
            }
            scenario.moveToState(Lifecycle.State.STARTED)
            scenario.onActivity { oldTimer!!.onFinish() }
            scenario.moveToState(Lifecycle.State.RESUMED)
            scenario.onActivity { activity ->
                assertNotSame(oldTimer, timer(ready!!))
                timer(ready!!).onFinish()
                val host = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                host.childFragmentManager.executePendingTransactions()
                assertEquals(R.id.exerciseMovesFragment2, host.navController.currentDestination?.id)
                assertEquals(0, host.navController.currentBackStackEntry!!.arguments!!.getInt("exerciseIndex"))
            }
        }
    }

    @Test fun renderedScreensKeepControlsInsideSystemBars() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            capture(scenario, "home", R.id.bottomBar)
            scenario.onActivity { activity ->
                host(activity).navController.navigate(R.id.exerciseMovesFragment, args())
            }
            capture(scenario, "ready", R.id.goNextbtn)
            scenario.onActivity { activity ->
                val ready = host(activity).childFragmentManager.primaryNavigationFragment as ExerciseMovesReadyFragment
                ready.binding.goNextbtn.performClick()
            }
            capture(scenario, "workout", R.id.goNext)
            scenario.onActivity { activity ->
                val move = host(activity).childFragmentManager.primaryNavigationFragment as ExerciseMovesFragment
                move.binding.goNext.performClick()
            }
            capture(scenario, "rest", R.id.skipRest)
            scenario.onActivity { activity ->
                // Jump to the last synthetic exercise to inspect the completion screen as well.
                host(activity).navController.navigate(R.id.exerciseMovesFragment2, args(6))
            }
            capture(scenario, "workout7", R.id.goNext)
            scenario.onActivity { activity ->
                val move = host(activity).childFragmentManager.primaryNavigationFragment as ExerciseMovesFragment
                move.binding.goNext.performClick()
            }
            capture(scenario, "end", R.id.btnEnd)
        }
    }

    private fun host(activity: HomeActivity) = activity.supportFragmentManager
        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    private fun capture(scenario: ActivityScenario<HomeActivity>, name: String, controlId: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        scenario.onActivity { activity ->
            val root = activity.findViewById<View>(R.id.drawer_home)
            val bars = ViewCompat.getRootWindowInsets(root)!!.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val control = activity.findViewById<View>(controlId)
            assertNotNull(name, control)
            val position = IntArray(2)
            control.getLocationOnScreen(position)
            assertTrue("$name top control overlaps bars", position[1] >= bars.top)
            assertTrue("$name bottom control overlaps bars", position[1] + control.height <= root.height - bars.bottom)
        }
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(instrumentation.targetContext.cacheDir, "edge-$name.png").outputStream().use {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        bitmap.recycle()
    }

    private fun args(index: Int = 0) = Bundle().apply {
        putInt("exerciseIndex", index)
        putParcelableArray("exerciseNewList", exercises)
        putParcelable("exerciseDayModel", day)
    }

    private fun timer(fragment: Fragment): CountDownTimer = fragment.javaClass.getDeclaredField("timecountDown")
        .apply { isAccessible = true }.get(fragment) as CountDownTimer

    private fun withHome(block: (NavHostFragment) -> Unit) {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val host = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                block(host)
            }
        }
    }
}
