package com.bayraktar.healthybackandneck

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.ui.Statistics.ProgressViewModel
import com.bayraktar.healthybackandneck.ui.Statistics.BodyCalculatorSnapshot
import com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments.BodyCalculatorFragment
import com.bayraktar.healthybackandneck.ui.Statistics.DialogFragments.BMIFragment
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.awaitCancellation
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProgressCalculatorsDesignTest {
    class DelayedInitialRead : BodyCalculatorFragment(Kind.BMI) {
        override suspend fun readMeasurements(): BodyCalculatorSnapshot = awaitCancellation()
    }
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val manager get() = DataStoreManage.getInstance(context)
    private val variant get() = InstrumentationRegistry.getArguments().getString("variant") ?: "normal"
    private val calculatorKeys: List<Preferences.Key<*>> = listOf(DataStoreManage.TXT_HEIGHT, DataStoreManage.TXT_WEIGHT,
        DataStoreManage.TXT_AGE, DataStoreManage.TXT_ACTIVITY_LEVEL, DataStoreManage.TXT_GENDER, DataStoreManage.TXT_NECK,
        DataStoreManage.TXT_BELLY, DataStoreManage.TXT_BOOTY, DataStoreManage.TXT_INDEKS, DataStoreManage.TXT_CATEGORY,
        DataStoreManage.TXT_CALORIE, DataStoreManage.TXT_FATRATE)

    @Test fun realPeriodTotalsEmptyStateChartSelectionAndProminentCalculatorHub() {
        val previousLanguage = LanguagePreference.getLanguageCode(context)
        val previousLocale = Locale.getDefault()
        val preferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
        LanguagePreference.setLanguageCode(context, "tr")
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                host(activity).childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager, fragment: Fragment, state: android.os.Bundle?) {
                        if (fragment is com.bayraktar.healthybackandneck.ui.Statistics.StatisticsFragment) {
                            val factory = object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    ProgressViewModel(WorkoutRepository(database), context) as T
                            }
                            ViewModelProvider(fragment, factory)[ProgressViewModel::class.java]
                        }
                    }
                }, false)
            }
            navigate(scenario, R.id.id_statistics_fragment)
            await(scenario) { it.findViewById<LinearLayout>(R.id.weekBars)?.childCount == 7 && it.findViewById<TextView>(R.id.periodDate)?.text?.isNotBlank() == true }
            capture(scenario, "progress-empty")
            scenario.onActivity { activity ->
                assertEquals("0", activity.findViewById<TextView>(R.id.weekWorkouts).text.toString())
                assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.chartEmpty).visibility)
            }
            val now = System.currentTimeMillis()
            runBlocking {
                suspend fun row(id: String, daysAgo: Int, minutes: Int, moves: Int, planned: Int, day: Int, level: Int = 1) {
                    val end = java.util.Calendar.getInstance().apply { timeInMillis = now; add(java.util.Calendar.DAY_OF_YEAR, -daysAgo) }.timeInMillis - 1000
                    database.workoutDao().insert(WorkoutSession(id, end-minutes*60000L, end, minutes*60000L, moves, planned, level, day, "Synthetic"))
                }
                row("ui-eight", 5, 8, 6, 6, 2)
                row("ui-twelve", 3, 12, 8, 8, 3)
                row("ui-seven", 2, 7, 2, 4, 0, 0)
                row("ui-fifteen", 0, 15, 8, 8, 4)
                row("ui-old", 10, 1, 2, 2, 1)
            }
            await(scenario) { it.findViewById<TextView>(R.id.weekWorkouts).text.toString() == "3" && it.findViewById<TextView>(R.id.weekMoves).text.toString() == "24" }
            scenario.onActivity { activity ->
                assertEquals("42", activity.findViewById<TextView>(R.id.weekMinutes).text.toString())
                assertEquals("4 / 21", activity.findViewById<TextView>(R.id.programCount).text.toString())
                activity.findViewById<LinearLayout>(R.id.weekBars).getChildAt(6).performClick()
                assertTrue(activity.findViewById<TextView>(R.id.chartSelection).text.isNotBlank())
            }
            capture(scenario, "progress-recorded-seven")
            reveal(scenario, R.id.chartTitle)
            scenario.onActivity { activity ->
                val bars = activity.findViewById<LinearLayout>(R.id.weekBars)
                for (index in 0 until bars.childCount) {
                    val column = bars.getChildAt(index)
                    val rect = Rect()
                    assertTrue(column.getGlobalVisibleRect(rect))
                    assertEquals("Every chart column must fit horizontally", column.width, rect.width())
                }
            }
            capture(scenario, "activity-chart")
            scenario.onActivity { it.findViewById<View>(R.id.periodFourWeeks).performClick() }
            scenario.onActivity { activity ->
                assertEquals(4, activity.findViewById<LinearLayout>(R.id.weekBars).childCount)
                val summary = com.bayraktar.healthybackandneck.ui.Statistics.ProgressSummary.from(
                    runBlocking { database.workoutDao().observeSessions().first() },
                    System.currentTimeMillis(),
                    com.bayraktar.healthybackandneck.ui.Statistics.ProgressPeriod.LAST_FOUR_WEEKS)
                val bucket = summary.buckets.last()
                val dates = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)
                val expected = dates.format(java.util.Date(bucket.start)) + " \u2013 " +
                    dates.format(java.util.Date(bucket.endExclusive - 1))
                val selection = activity.findViewById<TextView>(R.id.chartSelection)
                assertEquals(expected, selection.text.toString().lineSequence().first())
                File(context.cacheDir, "pc-$variant-date-detail.txt").writeText(selection.text.toString())
            }
            capture(scenario, "progress-recorded-four-weeks")
            reveal(scenario, R.id.chartSelection)
            scenario.onActivity { activity ->
                val selection = activity.findViewById<TextView>(R.id.chartSelection)
                assertTrue(selection.layout.lineCount >= 2)
                for (line in 0 until selection.layout.lineCount) {
                    assertEquals("Date and duration must not be ellipsized", 0, selection.layout.getEllipsisCount(line))
                }
            }
            capture(scenario, "selected-range-detail")
            reveal(scenario, R.id.programProgress)
            capture(scenario, "program-ring")
            scenario.onActivity { it.findViewById<Spinner>(R.id.programLevel).setSelection(1) }
            await(scenario) { it.findViewById<TextView>(R.id.programCount).text.toString() == "0 / 21" }
            scenario.onActivity { it.findViewById<Spinner>(R.id.programLevel).setSelection(0) }
            await(scenario) { it.findViewById<TextView>(R.id.programCount).text.toString() == "4 / 21" }
            reveal(scenario, R.id.openCalculators)
            scenario.onActivity { it.findViewById<View>(R.id.openCalculators).performClick() }
            await(scenario) { host(it).navController.currentDestination?.id == R.id.bodyCalculatorsHubFragment }
            scenario.onActivity { activity ->
                assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.bottomBar).visibility)
                assertTrue(activity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomBar).menu.findItem(R.id.id_statistics_fragment).isChecked)
                activity.findViewById<View>(R.id.bmiAction).performClick()
            }
            awaitReady(scenario)
            scenario.onActivity { it.findViewById<View>(R.id.returnHub).performClick() }
            await(scenario) { host(it).navController.currentDestination?.id == R.id.bodyCalculatorsHubFragment }
            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            await(scenario) { host(it).navController.currentDestination?.id == R.id.id_statistics_fragment }
            scenario.recreate()
            await(scenario) { it.findViewById<LinearLayout>(R.id.weekBars)?.childCount == 4 }
        }
        } finally {
            database.close()
            if (previousLanguage == null) preferences.edit().remove("pref_language_code").commit()
            else preferences.edit().putString("pref_language_code", previousLanguage).commit()
            Locale.setDefault(previousLocale)
        }
    }

    @Test fun calculatorValidationAtomicSaveDraftRecreationAndStaleResultInvalidation() = withCalculatorPreferences {
        runBlocking { manager.saveBmiCalculation(180, 80, "24.69", "Legacy") }
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            navigate(scenario, R.id.id_statistics_fragment)
            for (destination in listOf(R.id.BMIFragment, R.id.dailyCalorieFragment, R.id.bodyFatRateFragment)) {
                navigate(scenario, destination)
                awaitReady(scenario)
                for (invalid in listOf("", "0", "999999999999999999999999")) {
                    scenario.onActivity {
                        it.findViewById<EditText>(R.id.inputHeight).setText(invalid)
                        it.findViewById<View>(R.id.btnSavee).performClick()
                        assertNotNull(it.findViewById<EditText>(R.id.inputHeight).error)
                    }
                    assertEquals(180, runBlocking { manager.getHeight().first() })
                }
                scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                instrumentation.waitForIdleSync()
            }
            navigate(scenario, R.id.BMIFragment)
            awaitReady(scenario)
            scenario.onActivity {
                it.findViewById<EditText>(R.id.inputHeight).setText("180")
                it.findViewById<EditText>(R.id.inputWeightDesc).setText("81")
                it.findViewById<View>(R.id.btnSavee).performClick()
            }
            await(scenario) { it.findViewById<View>(R.id.resultCard)?.visibility == View.VISIBLE }
            scenario.onActivity { assertEquals(it.getString(R.string.pc_category_over), it.findViewById<TextView>(R.id.resultDetail).text.toString()) }
            scenario.recreate()
            awaitReady(scenario)
            scenario.onActivity {
                assertEquals("81", it.findViewById<EditText>(R.id.inputWeightDesc).text.toString())
                assertEquals(View.VISIBLE, it.findViewById<View>(R.id.resultCard).visibility)
                it.findViewById<EditText>(R.id.inputWeightDesc).setText("82")
                assertEquals(View.GONE, it.findViewById<View>(R.id.resultCard).visibility)
                it.findViewById<View>(R.id.goBack).performClick()
            }
            await(scenario) { host(it).navController.currentDestination?.id == R.id.id_statistics_fragment }
        }
    }

    @Test fun unfinishedInitialReadIsRetriedAfterSavedViewRecreation() = withCalculatorPreferences {
        runBlocking { manager.saveBmiCalculation(180, 80, "24.69", "Legacy") }
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            navigate(scenario, R.id.BMIFragment)
            scenario.onActivity { activity ->
                val fragments = host(activity).childFragmentManager
                val original = fragments.primaryNavigationFragment!!
                val container = (original.requireView().parent as View).id
                val delayed = DelayedInitialRead()
                fragments.beginTransaction().replace(container, delayed).setPrimaryNavigationFragment(delayed).commitNow()
                assertFalse(delayed.requireView().findViewById<View>(R.id.btnSavee).isEnabled)
                val saved = fragments.saveFragmentInstanceState(delayed)
                val restored = BMIFragment().apply { setInitialSavedState(saved) }
                fragments.beginTransaction().replace(container, restored).setPrimaryNavigationFragment(restored).commitNow()
            }
            awaitReady(scenario)
            scenario.onActivity {
                assertEquals("180", it.findViewById<EditText>(R.id.inputHeight).text.toString())
                assertEquals("80", it.findViewById<EditText>(R.id.inputWeightDesc).text.toString())
            }
        }
    }

    @Test fun localizedSavedValuesSexRestorationAndAllThreeResultScreens() = withCalculatorPreferences {
        val languagePrefs = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        val previousLanguage = LanguagePreference.getLanguageCode(context)
        val previousLocale = Locale.getDefault()
        try {
            for (language in listOf("tr", "en", "de", "ru")) {
                runBlocking {
                    manager.saveBmiCalculation(180, 80, "24,69", "Legacy")
                    manager.saveCalorieCalculation(180, 80, 30, 1, "Erkek", "2236,08")
                    manager.saveFatCalculation(180, 40, 90, null, "Erkek", "18,367")
                }
                LanguagePreference.setLanguageCode(context, language)
                ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
                    navigate(scenario, R.id.id_statistics_fragment)
                    await(scenario) { it.findViewById<LinearLayout>(R.id.weekBars)?.childCount == 7 }
                    capture(scenario, "progress-$language")
                    reveal(scenario, R.id.openCalculators)
                    scenario.onActivity { it.findViewById<View>(R.id.openCalculators).performClick() }
                    await(scenario) { it.findViewById<TextView>(R.id.bmiSaved)?.text.toString().contains("24") }
                    capture(scenario, "calculator-hub-$language")
                    reveal(scenario, R.id.editfatrate)
                    capture(scenario, "calculator-hub-lower-$language")
                    for ((destination, name) in listOf(R.id.BMIFragment to "bmi", R.id.dailyCalorieFragment to "calories", R.id.bodyFatRateFragment to "fat")) {
                        navigate(scenario, destination)
                        awaitReady(scenario)
                        if (destination == R.id.BMIFragment) {
                            scenario.onActivity { it.findViewById<NestedScrollView>(R.id.calculatorScroll).scrollTo(0, 0) }
                            capture(scenario, "bmi-form-$language")
                        }
                        scenario.onActivity { activity ->
                            if (destination != R.id.BMIFragment) assertTrue(activity.findViewById<RadioButton>(R.id.checkMale).isChecked)
                            if (destination == R.id.bodyFatRateFragment) assertEquals(View.GONE, activity.findViewById<View>(R.id.hipLayout).visibility)
                            activity.findViewById<View>(R.id.btnSavee).performClick()
                        }
                        await(scenario) { it.findViewById<View>(R.id.resultCard)?.visibility == View.VISIBLE &&
                            it.findViewById<View>(R.id.btnSavee).isEnabled && it.findViewById<TextView>(R.id.resultValue).text.isNotBlank() }
                        reveal(scenario, R.id.resultCard)
                        capture(scenario, "$name-result-$language")
                        scenario.onActivity { activity ->
                            if (destination == R.id.dailyCalorieFragment) {
                                activity.findViewById<Spinner>(R.id.spnrActivity).setSelection(2)
                            } else if (destination == R.id.bodyFatRateFragment) {
                                activity.findViewById<RadioButton>(R.id.checkFemale).isChecked = true
                            }
                        }
                        instrumentation.waitForIdleSync()
                        if (destination != R.id.BMIFragment) scenario.onActivity {
                            assertEquals(View.GONE, it.findViewById<View>(R.id.resultCard).visibility)
                        }
                        scenario.onActivity { it.findViewById<View>(R.id.goBack).performClick() }
                        instrumentation.waitForIdleSync()
                    }
                }
            }
        } finally {
            if (previousLanguage == null) languagePrefs.edit().remove("pref_language_code").commit()
            else languagePrefs.edit().putString("pref_language_code", previousLanguage).commit()
            Locale.setDefault(previousLocale)
        }
    }

    @Test fun invalidFatFormulaDoesNotSaveAndMaleDoesNotNeedHip() = withCalculatorPreferences {
        runBlocking { manager.saveFatCalculation(180, 40, 90, 100, "Mann", "18.367") }
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            navigate(scenario, R.id.bodyFatRateFragment)
            awaitReady(scenario)
            scenario.onActivity {
                assertTrue(it.findViewById<RadioButton>(R.id.checkMale).isChecked)
                it.findViewById<EditText>(R.id.inputHeight).setText("190")
                it.findViewById<EditText>(R.id.neckDesc).setText("100")
                it.findViewById<EditText>(R.id.bellyDesc).setText("90")
                it.findViewById<View>(R.id.btnSavee).performClick()
                assertEquals(View.VISIBLE, it.findViewById<View>(R.id.formError).visibility)
            }
            assertEquals(180, runBlocking { manager.getHeight().first() })
            scenario.onActivity {
                it.findViewById<EditText>(R.id.neckDesc).setText("40")
                it.findViewById<EditText>(R.id.buttDesc).setText("")
                it.findViewById<View>(R.id.btnSavee).performClick()
            }
            await(scenario) { it.findViewById<View>(R.id.resultCard)?.visibility == View.VISIBLE }
            assertEquals(100, runBlocking { manager.getBooty().first() })
            scenario.onActivity {
                it.findViewById<RadioButton>(R.id.checkFemale).isChecked = true
                it.findViewById<View>(R.id.btnSavee).performClick()
                assertNotNull(it.findViewById<EditText>(R.id.buttDesc).error)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun withCalculatorPreferences(block: () -> Unit) {
        val dataStore = DataStoreManage::class.java.getDeclaredField("dataStore").apply { isAccessible = true }.get(manager) as DataStore<Preferences>
        val original = runBlocking { dataStore.data.first() }
        try { block() } finally {
            runBlocking { dataStore.edit { preferences ->
                calculatorKeys.forEach { key ->
                    val typed = key as Preferences.Key<Any>
                    val value = original.asMap()[key]
                    if (value == null) preferences.remove(typed) else preferences[typed] = value
                }
            } }
        }
    }
    private fun host(activity: HomeActivity) = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    private fun navigate(scenario: ActivityScenario<HomeActivity>, destination: Int) {
        scenario.onActivity { host(it).navController.navigate(destination) }
        instrumentation.waitForIdleSync()
    }
    private fun awaitReady(scenario: ActivityScenario<HomeActivity>) = await(scenario) { it.findViewById<View>(R.id.btnSavee)?.isEnabled == true }
    private fun await(scenario: ActivityScenario<HomeActivity>, predicate: (HomeActivity) -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + 15000
        while (true) {
            var ready = false; scenario.onActivity { ready = predicate(it) }
            if (ready) return
            if (SystemClock.elapsedRealtime() >= deadline) fail("Timed out awaiting progress/calculator UI")
            SystemClock.sleep(50)
        }
    }
    private fun reveal(scenario: ActivityScenario<HomeActivity>, id: Int) {
        await(scenario) { it.findViewById<View>(id)?.let { target -> target.isLaidOut && target.height > 0 } == true }
        scenario.onActivity { activity ->
            val target = activity.findViewById<View>(id)
            val scroll = activity.findViewById<NestedScrollView>(R.id.progressScroll)
                ?: activity.findViewById<NestedScrollView>(R.id.calculatorScroll)
                ?: activity.findViewById<NestedScrollView>(R.id.hubScroll)
            scroll.isSmoothScrollingEnabled = false
            val a = IntArray(2); val b = IntArray(2)
            target.getLocationOnScreen(a); scroll.getLocationOnScreen(b)
            scroll.scrollTo(0, scroll.scrollY + a[1] - b[1])
        }
        instrumentation.waitForIdleSync()
        await(scenario) { activity -> activity.findViewById<View>(id).let { target ->
            val visible = Rect()
            target.getGlobalVisibleRect(visible) && visible.height() >= target.height
        } }
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
        assertTrue("A committed UI frame is required before capture", drawn.await(10, TimeUnit.SECONDS))
        instrumentation.waitForIdleSync()
        instrumentation.uiAutomation.waitForIdle(100, 5000)
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(context.cacheDir, "pc-$variant-$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
