package com.bayraktar.healthybackandneck

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.bayraktar.healthybackandneck.ui.SplashScreen.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirstRunAdFreeOfferTest {
    @Test
    fun onboardingCompletionShowsOfferAndNotNowReturnsHomeOnce() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = DataStoreManage.getInstance(context)
        @Suppress("UNCHECKED_CAST")
        val store = DataStoreManage::class.java.getDeclaredField("dataStore")
            .apply { isAccessible = true }
            .get(manager) as DataStore<Preferences>
        val shownKey = booleanPreferencesKey("first_run_ad_free_offer_shown_v1")
        val previousValue = runBlocking { store.data.first()[shownKey] }
        val previousLoginState = runBlocking { manager.getLog().first() }

        try {
            runBlocking { store.edit { it[shownKey] = false } }

            ActivityScenario.launch(MainActivity::class.java).use {
                onView(withId(R.id.beginTraining)).perform(click())
                onView(withId(R.id.notNowButton)).check(matches(isDisplayed())).perform(click())
                onView(withId(R.id.notNowButton)).check(doesNotExist())
                onView(withId(R.id.bottomBar)).check(matches(isDisplayed()))
            }

            ActivityScenario.launch(SplashScreen::class.java).use {
                onView(withId(R.id.notNowButton)).check(doesNotExist())
                onView(withId(R.id.bottomBar)).check(matches(isDisplayed()))
            }

            assertFalse("The first-run offer must not be claimable again", runBlocking {
                manager.claimFirstRunAdFreeOffer()
            })
        } finally {
            runBlocking {
                store.edit { preferences ->
                    if (previousValue == null) preferences.remove(shownKey)
                    else preferences[shownKey] = previousValue
                }
                manager.saveLog(previousLoginState)
            }
        }
    }
}
