package com.bayraktar.healthybackandneck

import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.View
import android.widget.ScrollView
import java.io.File
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.google.android.gms.ads.AdView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class AdConfigurationTest {
    @Test fun profileBannerUsesTestUnitInDebugLayout() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNull("Home must not retain an advertising slot", activity.findViewById<AdView>(R.id.adView))
                val scroll = activity.findViewById<View>(R.id.legViewPager).parent.parent as ScrollView
                scroll.isSmoothScrollingEnabled = false
                scroll.fullScroll(View.FOCUS_DOWN)
            }
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.waitForIdleSync()
            val image = instrumentation.uiAutomation.takeScreenshot()
            try {
                File(instrumentation.targetContext.cacheDir, "ad-free-home-bottom.png").outputStream().use {
                    image.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            } finally {
                image.recycle()
            }
            scenario.onActivity { activity ->
                val host = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                host.navController.navigate(R.id.id_profile_fragment)
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            scenario.onActivity { activity ->
                assertEquals("ca-app-pub-3940256099942544/6300978111", activity.findViewById<AdView>(R.id.adView).adUnitId)
            }
        }
    }

    @Test fun productionFullscreenIdsDoNotChangeWithLanguage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = mapOf(
            R.string.rewarded_ad1 to "5970466188",
            R.string.rewarded_ad2 to "8758612752",
            R.string.rewarded_ad3 to "8717701412",
            R.string.ad_interstitial_detail_day to "3768357562",
            R.string.ad_interstitial_exercise_end to "6394520905",
        )
        for (language in listOf("tr", "en", "de", "ru")) {
            val configuration = Configuration(context.resources.configuration).apply { setLocale(Locale.forLanguageTag(language)) }
            val localized = context.createConfigurationContext(configuration)
            expected.forEach { (resource, suffix) ->
                assertEquals("ca-app-pub-1896269552628731/$suffix", localized.getString(resource))
            }
        }
    }
}
