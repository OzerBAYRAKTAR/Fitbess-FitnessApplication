package com.bayraktar.healthybackandneck

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseCrashlyticsIntegrationTest {
    @Test fun firebaseInitializesCrashlyticsCollectionByDefault() {
        assertNotNull("The Firebase default app should initialize from google-services.json", FirebaseApp.getInstance())
        assertTrue("Crashlytics collection should be enabled for this app", FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled())
    }
}
