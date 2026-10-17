package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.WorkoutSessionViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RedesignFlowTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val day = ExerciseDay(-901, -901, -901, 2, 1, 0, false, -901)
    private val moves = Array(2) { index ->
        ExerciseDayExercise(-901 - index, -901 - index, index, "Verification", -901,
            "Verification exercise", "Synthetic test session", R.drawable.e52_a11_lying_shoulder_extension, false, -901)
    }
    private val database by lazy {
        androidx.room.Room.databaseBuilder(instrumentation.targetContext, AppDatabase::class.java, "fitness_db")
            .addMigrations(AppDatabase.MIGRATION_11_12).build()
    }

    @org.junit.Before fun cleanExplicitInterruptedTestRecords() {
        InstrumentationRegistry.getArguments().getString("cleanupSessionIds")?.split(",")?.filter { it.isNotBlank() }?.forEach { deleteSession(it) }
    }
    @org.junit.After fun closeTestConnection() { database.close() }

    @Test fun firstLaunchIsOneScreenWithoutBodyQuestions() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertTrue(activity.findViewById<View>(R.id.beginTraining).isShown)
                assertNull(activity.findViewById<View>(R.id.number_picker_year))
            }
            capture("onboarding")
        }
    }

    @Test fun turkishOnboardingUsesTheSelectedLanguage() {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(instrumentation.targetContext)
        val previous = prefs.getString("pref_language_code", null)
        val previousLocale = java.util.Locale.getDefault()
        try {
            prefs.edit().putString("pref_language_code", "tr").commit()
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                scenario.onActivity { assertEquals("Evde Fit", it.getString(R.string.brand_short)) }
                capture("onboarding-turkish")
            }
        } finally {
            if (previous == null) prefs.edit().remove("pref_language_code").commit()
            else prefs.edit().putString("pref_language_code", previous).commit()
            java.util.Locale.setDefault(previousLocale)
        }
    }

    @Test fun pausedRecreatedSessionAndRepeatedEndHaveOneHonestHistoryRecord() {
        var savedId: String? = null
        try {
            ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                capture("home-final")
                scenario.onActivity { activity ->
                    host(activity).navController.navigate(R.id.exerciseMovesFragment, args())
                    host(activity).childFragmentManager.executePendingTransactions()
                    activity.findViewById<View>(R.id.goNextbtn).performClick()
                }
                instrumentation.waitForIdleSync()
                capture("workout-real-gif")
                var pausedText = ""
                scenario.onActivity { activity ->
                    activity.findViewById<View>(R.id.playpause).performClick()
                    pausedText = activity.findViewById<TextView>(R.id.txtTimeLeft).text.toString()
                }
                SystemClock.sleep(1200)
                scenario.recreate()
                scenario.onActivity { activity ->
                    assertEquals(pausedText, activity.findViewById<TextView>(R.id.txtTimeLeft).text.toString())
                    activity.findViewById<View>(R.id.playpause).performClick()
                    timer(host(activity).childFragmentManager.primaryNavigationFragment!!).onFinish()
                    host(activity).childFragmentManager.executePendingTransactions()
                    assertEquals(R.id.exerciseMovesRestFragment, host(activity).navController.currentDestination?.id)
                }
                capture("rest-final")
                scenario.onActivity { activity ->
                    activity.findViewById<View>(R.id.skipRest).performClick()
                    host(activity).childFragmentManager.executePendingTransactions()
                    val next = activity.findViewById<View>(R.id.goNext)
                    next.performClick(); next.performClick()
                }
                var model: WorkoutSessionViewModel? = null
                scenario.onActivity { model = ViewModelProvider(it)[WorkoutSessionViewModel::class.java] }
                val saved = runBlocking { withTimeout(15000) { model!!.saveState.first { it is WorkoutSessionViewModel.SaveState.Saved || it is WorkoutSessionViewModel.SaveState.Failed } } } as WorkoutSessionViewModel.SaveState.Saved
                savedId = saved.session.id
                assertEquals(1, saved.session.completedMoves)
                assertEquals(1, saved.session.skippedMoves)
                assertFalse(saved.session.isComplete)
                capture("end-partial")
                scenario.recreate()
                runBlocking { withTimeout(15000) { model!!.saveState.first { it is WorkoutSessionViewModel.SaveState.Saved || it is WorkoutSessionViewModel.SaveState.Failed } } }
                assertEquals(1, runBlocking { database.workoutDao().observeSessions().first().count { it.id == savedId } })
                scenario.onActivity { activity -> activity.onBackPressedDispatcher.onBackPressed() }
                instrumentation.waitForIdleSync()
                scenario.onActivity { activity ->
                    assertEquals(R.id.id_homepage_fragment, host(activity).navController.currentDestination?.id)
                    host(activity).navController.navigate(R.id.id_statistics_fragment)
                }
                instrumentation.waitForIdleSync()
                capture("progress-recorded")
            }
        } finally {
            savedId?.let { id -> database.openHelper.writableDatabase.execSQL("DELETE FROM workout_sessions WHERE id = ?", arrayOf(id)) }
        }
    }

    @Test fun restBackExitSavesCompletedEffortAsPartial() {
        var savedId: String? = null
        try {
            ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    host(activity).navController.navigate(R.id.exerciseMovesFragment, args())
                    host(activity).childFragmentManager.executePendingTransactions()
                    activity.findViewById<View>(R.id.goNextbtn).performClick()
                }
                instrumentation.waitForIdleSync()
                scenario.onActivity { activity ->
                    timer(host(activity).childFragmentManager.primaryNavigationFragment!!).onFinish()
                    host(activity).childFragmentManager.executePendingTransactions()
                    assertEquals(R.id.exerciseMovesRestFragment, host(activity).navController.currentDestination?.id)
                    activity.onBackPressedDispatcher.onBackPressed()
                }
                var restTime = ""
                scenario.onActivity { restTime = it.findViewById<TextView>(R.id.txtTimeLeft).text.toString() }
                scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)
                scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
                SystemClock.sleep(1200)
                scenario.onActivity { assertEquals(restTime, it.findViewById<TextView>(R.id.txtTimeLeft).text.toString()) }
                onView(withText(R.string.exit_workout)).perform(click())
                val saved = awaitSaved(scenario)
                savedId = saved.session.id
                assertEquals(1, saved.session.completedMoves)
                assertFalse(saved.session.isComplete)
                scenario.onActivity { assertEquals(R.id.exerciseMovesEndFragment, host(it).navController.currentDestination?.id) }
            }
        } finally { savedId?.let { deleteSession(it) } }
    }

    @Test fun repeatedCompletedDayAddsSessionsButOnlyOneProgramDay() {
        val ids = mutableListOf<String>()
        try {
            ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                repeat(2) {
                    scenario.onActivity { activity ->
                        // Day21 has no next-day unlock mutation; only these test-owned session IDs are cleaned up.
                        val arguments = args().apply { putParcelable("exerciseDayModel", ExerciseDay(-902, 21, 1, 2, 1, 0, false, -902)) }
                        host(activity).navController.navigate(R.id.exerciseMovesFragment, arguments)
                        host(activity).childFragmentManager.executePendingTransactions()
                        activity.findViewById<View>(R.id.goNextbtn).performClick()
                    }
                    instrumentation.waitForIdleSync()
                    scenario.onActivity { activity ->
                        timer(host(activity).childFragmentManager.primaryNavigationFragment!!).onFinish()
                        host(activity).childFragmentManager.executePendingTransactions()
                        activity.findViewById<View>(R.id.skipRest).performClick()
                    }
                    instrumentation.waitForIdleSync()
                    scenario.onActivity { activity -> timer(host(activity).childFragmentManager.primaryNavigationFragment!!).onFinish() }
                    val saved = awaitSaved(scenario)
                    ids.add(saved.session.id)
                    assertTrue(saved.session.isComplete)
                    assertEquals(2, saved.session.completedMoves)
                    assertEquals(1, saved.session.level)
                    assertEquals(21, saved.session.day)
                    instrumentation.waitForIdleSync()
                    scenario.onActivity { activity ->
                        assertEquals(activity.getString(R.string.workout_complete_title), activity.findViewById<TextView>(R.id.endTitle).text.toString())
                    }
                    capture("end-completed")
                    scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                    instrumentation.waitForIdleSync()
                }
                val all = runBlocking { database.workoutDao().observeSessions().first() }
                assertEquals(2, all.count { it.id in ids })
                val expectedDays = all.filter { it.level == 1 && it.isComplete && it.day in 1..21 }.map { it.day }.distinct().size
                scenario.onActivity { host(it).navController.navigate(R.id.id_statistics_fragment) }
                instrumentation.waitForIdleSync()
                SystemClock.sleep(500)
                scenario.onActivity { activity ->
                    val progress = activity.findViewById<android.widget.LinearLayout>(R.id.programProgress)
                    assertEquals(activity.getString(R.string.program_progress_format, 1, expectedDays), (progress.getChildAt(0) as TextView).text.toString())
                }
                capture("progress-completed")
            }
        } finally { ids.forEach { deleteSession(it) } }
    }

    @Test fun calculatorsRejectMissingZeroAndOverflowBeforeSaving() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            listOf(R.id.BMIFragment, R.id.dailyCalorieFragment, R.id.bodyFatRateFragment).forEach { destination ->
                scenario.onActivity { host(it).navController.navigate(destination) }
                instrumentation.waitForIdleSync()
                listOf("", "0", "999999999999999999999999999").forEach { invalid ->
                    scenario.onActivity { activity ->
                        activity.findViewById<android.widget.EditText>(R.id.inputHeight).setText(invalid)
                        activity.findViewById<View>(R.id.btnSavee).performClick()
                    }
                    instrumentation.waitForIdleSync()
                    scenario.onActivity { activity ->
                        assertNotNull(activity.findViewById<android.widget.EditText>(R.id.inputHeight).error)
                        if (destination == R.id.dailyCalorieFragment) assertTrue(activity.findViewById<android.widget.Spinner>(R.id.spnrActivity).selectedItemPosition in 0..3)
                    }
                }
                scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                instrumentation.waitForIdleSync()
            }
        }
    }

    private fun awaitSaved(scenario: ActivityScenario<HomeActivity>): WorkoutSessionViewModel.SaveState.Saved {
        var model: WorkoutSessionViewModel? = null
        scenario.onActivity { model = ViewModelProvider(it)[WorkoutSessionViewModel::class.java] }
        return runBlocking { withTimeout(15000) { model!!.saveState.first { it is WorkoutSessionViewModel.SaveState.Saved || it is WorkoutSessionViewModel.SaveState.Failed } } } as WorkoutSessionViewModel.SaveState.Saved
    }
    private fun deleteSession(id: String) {
        database.openHelper.writableDatabase.execSQL("DELETE FROM workout_sessions WHERE id = ?", arrayOf(id))
    }

    private fun host(activity: HomeActivity) = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    private fun timer(fragment: Fragment) = fragment.javaClass.getDeclaredField("timecountDown").apply { isAccessible = true }.get(fragment) as CountDownTimer
    private fun args() = Bundle().apply {
        putInt("exerciseIndex", 0); putParcelable("exerciseDayModel", day); putParcelableArray("exerciseNewList", moves)
    }
    private fun capture(name: String) {
        instrumentation.waitForIdleSync()
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        File(instrumentation.targetContext.cacheDir, "redesign-$name.png").outputStream().use {
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        screenshot.recycle()
    }
}
