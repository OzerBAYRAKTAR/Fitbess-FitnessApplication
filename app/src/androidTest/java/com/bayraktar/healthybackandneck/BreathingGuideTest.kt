package com.bayraktar.healthybackandneck

import android.graphics.Bitmap
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.HomeActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BreathingGuideTest {
 @Test fun techniquesExpandRestoreAndReturnHome() {
  val instrumentation = InstrumentationRegistry.getInstrumentation()
  ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
   scenario.onActivity { it.findViewById<View>(R.id.breathAction).performClick() }
   instrumentation.waitForIdleSync()
   scenario.onActivity {
    val list = it.findViewById<LinearLayout>(R.id.techniqueList)
    assertEquals(8, list.childCount)
    assertEquals(View.VISIBLE, list.getChildAt(0).findViewById<View>(R.id.techniqueDescription).visibility)
    assertEquals(it.getString(R.string.seyir_desc), list.getChildAt(6).findViewById<android.widget.TextView>(R.id.techniqueDescription).text.toString())
    list.getChildAt(0).findViewById<View>(R.id.techniqueHeader).performClick()
    assertEquals(View.GONE, list.getChildAt(0).findViewById<View>(R.id.techniqueDescription).visibility)
    list.getChildAt(1).findViewById<View>(R.id.techniqueHeader).performClick()
    assertEquals(View.GONE, list.getChildAt(0).findViewById<View>(R.id.techniqueDescription).visibility)
    assertEquals(View.VISIBLE, list.getChildAt(1).findViewById<View>(R.id.techniqueDescription).visibility)
   }
   scenario.recreate()
   instrumentation.waitForIdleSync()
   scenario.onActivity {
    val list = it.findViewById<LinearLayout>(R.id.techniqueList)
    assertEquals(8, list.childCount)
    assertEquals(View.VISIBLE, list.getChildAt(1).findViewById<View>(R.id.techniqueDescription).visibility)
   }
   android.os.SystemClock.sleep(400)
   val bitmap = instrumentation.uiAutomation.takeScreenshot()
   val dir = File(instrumentation.targetContext.getExternalFilesDir(null), "breathing-verification").apply { mkdirs() }
   File(dir,"breathing.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }
   bitmap.recycle()
   scenario.onActivity { it.findViewById<View>(R.id.backButton).performClick() }
   instrumentation.waitForIdleSync()
   scenario.onActivity {
    val nav = (it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    assertEquals(R.id.id_homepage_fragment, nav.currentDestination!!.id)
   }
  }
 }
}
