package com.bayraktar.healthybackandneck

import android.os.Bundle
import android.graphics.Bitmap
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class WorkoutFlowDesignTest {
 private val inst = InstrumentationRegistry.getInstrumentation()
 private var previousLanguage: String? = null
 private val previousLocale = java.util.Locale.getDefault()
 private val language = InstrumentationRegistry.getArguments().getString("language", "en")
 @org.junit.Before fun setLanguage() {
  val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext)
  previousLanguage = prefs.getString("pref_language_code", null)
  prefs.edit().putString("pref_language_code", language).commit()
 }
 @org.junit.After fun restoreLanguage() {
  android.preference.PreferenceManager.getDefaultSharedPreferences(inst.targetContext).edit().apply {
   if (previousLanguage == null) remove("pref_language_code") else putString("pref_language_code", previousLanguage)
  }.commit()
  java.util.Locale.setDefault(previousLocale)
 }
 private fun nav(a: HomeActivity) = (a.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
 private fun await(s: ActivityScenario<HomeActivity>, condition: (HomeActivity)->Boolean) {
  val end=android.os.SystemClock.elapsedRealtime()+15000; var ok=false
  while(!ok && android.os.SystemClock.elapsedRealtime()<end) { s.onActivity { ok=condition(it) }; if(!ok) android.os.SystemClock.sleep(50) }
  assertTrue("Expected screen/state",ok); inst.waitForIdleSync()
 }
 private fun shot(name:String) {
  inst.waitForIdleSync(); android.os.SystemClock.sleep(400)
  val bitmap=inst.uiAutomation.takeScreenshot()
  val dir=File(inst.targetContext.getExternalFilesDir(null),"workout-design-verification").apply{mkdirs()}
  File(dir,"$language-$name.png").outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}; bitmap.recycle()
 }
 @Test fun levelWeeksAndDayData() {
  ActivityScenario.launch(HomeActivity::class.java).use { s ->
   for((index,id) in listOf(R.id.programBeginner,R.id.programIntermediate,R.id.programAdvanced).withIndex()) {
    await(s){it.findViewById<View>(id)!=null}; s.onActivity{it.findViewById<View>(id).performClick()}
    await(s){it.findViewById<RecyclerView>(R.id.listDaysOfWeek)?.adapter?.itemCount==21}
    shot("level-${index+1}")
    s.onActivity{it.findViewById<View>(R.id.week3).performClick()}
    await(s){(it.findViewById<RecyclerView>(R.id.listDaysOfWeek).layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()==14}
    s.onActivity{it.findViewById<View>(R.id.week1).performClick()}
    await(s){it.findViewById<RecyclerView>(R.id.listDaysOfWeek).findViewHolderForAdapterPosition(0)!=null}
    s.onActivity{it.findViewById<RecyclerView>(R.id.listDaysOfWeek).findViewHolderForAdapterPosition(0)!!.itemView.performClick()}
    await(s){nav(it).currentDestination?.id==R.id.detailDayFragment}
    shot("detail-${index+1}")
    s.onActivity{it.findViewById<View>(R.id.backButton).performClick()}
    await(s){it.findViewById<View>(R.id.week1)!=null}
    s.onActivity{it.findViewById<View>(R.id.imageBack).performClick()}
   }
  }
 }
 @Test fun playerPauseRecreateAndRestSkip() {
  val context=inst.targetContext
  val moves=WorkoutCatalogParser.readExercises(context,1).filter{it.dayId==1}.take(3)
  assertTrue(moves.size>=2)
  val day=WorkoutCatalogParser.readDays(context,"firstday.json").first()
  ActivityScenario.launch(HomeActivity::class.java).use { s ->
   s.onActivity{nav(it).navigate(R.id.exerciseMovesFragment2,Bundle().apply{putInt("exerciseIndex",0);putParcelableArray("exerciseNewList",moves.toTypedArray());putParcelable("exerciseDayModel",day)})}
   await(s){it.findViewById<View>(R.id.playpause)!=null}
   s.onActivity{it.findViewById<View>(R.id.playpause).performClick()}
   var paused=""; s.onActivity{paused=it.findViewById<TextView>(R.id.txtTimeLeft).text.toString()}
   s.recreate(); inst.waitForIdleSync(); android.os.SystemClock.sleep(1100)
   s.onActivity{assertEquals(paused,it.findViewById<TextView>(R.id.txtTimeLeft).text.toString());assertEquals(it.getString(R.string.resume_workout),it.findViewById<View>(R.id.playpause).contentDescription)}
   shot("player-paused")
   s.onActivity{it.findViewById<View>(R.id.exerciseInfo).performClick()};inst.waitForIdleSync()
   inst.uiAutomation.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
   inst.waitForIdleSync();android.os.SystemClock.sleep(400)
   s.onActivity{assertEquals(paused,it.findViewById<TextView>(R.id.txtTimeLeft).text.toString());it.findViewById<View>(R.id.goNext).performClick()}
   await(s){nav(it).currentDestination?.id==R.id.exerciseMovesRestFragment}
   android.os.SystemClock.sleep(1300)
   var before="";s.onActivity{before=it.findViewById<TextView>(R.id.txtTimeLeft).text.toString()}
   s.recreate();inst.waitForIdleSync()
   s.onActivity{assertTrue(it.findViewById<TextView>(R.id.txtTimeLeft).text.toString()<=before)}
   shot("rest")
   s.onActivity{val button=it.findViewById<View>(R.id.skipRest);button.performClick();button.performClick()}
   await(s){nav(it).currentDestination?.id==R.id.exerciseMovesFragment2}
   s.onActivity{assertEquals("2",it.findViewById<TextView>(R.id.txtRank).text.toString());it.findViewById<View>(R.id.playpause).performClick();it.findViewById<View>(R.id.goBack).performClick();assertEquals("1",it.findViewById<TextView>(R.id.txtRank).text.toString())}
  }
 }
}
