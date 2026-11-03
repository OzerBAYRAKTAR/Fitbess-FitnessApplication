package com.bayraktar.healthybackandneck

import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.ui.HomeActivity
import androidx.navigation.fragment.NavHostFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutStartNavigationTest {
    @Test
    fun startExerciseNavigatesDirectlyToMovesWithoutWaitingForInterstitial() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val move = ExerciseDayExercise(
            90501,
            90501,
            1,
            "stretch",
            0,
            "Verification move",
            "Test",
            R.drawable.e52_a11_lying_shoulder_extension,
            false,
            1,
        )
        val args = Bundle().apply {
            putParcelableArray("exerciseNewList", arrayOf(move))
            putParcelable("exerciseDayModel", null)
            putString("exerciseLevel", "Verification")
        }

        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val navHost = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHost.navController.navigate(R.id.detailDayFragment, args)
            }

            val deadline = android.os.SystemClock.elapsedRealtime() + 10_000
            var ready = false
            while (!ready && android.os.SystemClock.elapsedRealtime() < deadline) {
                scenario.onActivity { activity ->
                    val button = activity.findViewById<View>(R.id.startExercise)
                    ready = button?.isEnabled == true
                }
                if (!ready) android.os.SystemClock.sleep(50)
            }
            assertTrue("Start exercise button did not become enabled", ready)

            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.startExercise).performClick()
                val navHost = activity.supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                assertEquals(R.id.exerciseMovesFragment, navHost.navController.currentDestination?.id)
            }
            instrumentation.waitForIdleSync()
        }
    }
}
