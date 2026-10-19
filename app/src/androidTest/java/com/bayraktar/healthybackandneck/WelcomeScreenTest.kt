package com.bayraktar.healthybackandneck

import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.google.android.material.button.MaterialButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {
    @Test
    fun firstLaunchWelcomeShowsBrandImageCopyAndStartAction() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertEquals(
                    activity.getString(R.string.welcome_title),
                    activity.findViewById<TextView>(R.id.welcomeTitle).text.toString()
                )
                assertNotNull(activity.findViewById<ImageView>(R.id.welcomeHeroImage).drawable)
                assertEquals(
                    activity.getString(R.string.begin_training),
                    activity.findViewById<MaterialButton>(R.id.beginTraining).text.toString()
                )
                assertTrue(activity.findViewById<MaterialButton>(R.id.beginTraining).isEnabled)
            }

        }
    }
}
