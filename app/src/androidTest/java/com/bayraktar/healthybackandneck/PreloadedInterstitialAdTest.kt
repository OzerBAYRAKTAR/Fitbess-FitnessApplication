package com.bayraktar.healthybackandneck

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.utils.PreloadedInterstitialAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreloadedInterstitialAdTest {
    @Test
    fun missingAdContinuesImmediatelyAndLateLoadNeverShowsItself() = onMain {
        val owner = ScreenOwner()
        var callback: InterstitialAdLoadCallback? = null
        val controller = PreloadedInterstitialAd(Activity(), owner, "test", loadAd = { _, c -> callback = c })
        var transitions = 0

        controller.showOrContinue { transitions++ }
        assertEquals(1, transitions)
        val ad = FakeAd()
        callback!!.onAdLoaded(ad)
        assertEquals(0, ad.shows)

        controller.showOrContinue { transitions++ }
        assertEquals(1, ad.shows)
        assertEquals(1, transitions)
        ad.getFullScreenContentCallback()!!.onAdDismissedFullScreenContent()
        ad.getFullScreenContentCallback()!!.onAdDismissedFullScreenContent()
        assertEquals(2, transitions)
        owner.registry.currentState = Lifecycle.State.DESTROYED
    }

    @Test
    fun loadAndShowFailuresDoNotBlockOrDuplicateTheTransition() = onMain {
        val owner = ScreenOwner()
        var callback: InterstitialAdLoadCallback? = null
        val controller = PreloadedInterstitialAd(Activity(), owner, "test", loadAd = { _, c -> callback = c })
        var transitions = 0
        callback!!.onAdFailedToLoad(LoadAdError(3, "No connection", "test", null, null))
        controller.showOrContinue { transitions++ }
        assertEquals(1, transitions)

        val ad = FakeAd()
        callback!!.onAdLoaded(ad)
        controller.showOrContinue { transitions++ }
        // Repeated input while fullscreen is active must not begin another action.
        controller.showOrContinue { transitions++ }
        assertEquals(1, ad.shows)
        ad.getFullScreenContentCallback()!!.onAdFailedToShowFullScreenContent(AdError(0, "Unavailable", "test"))
        ad.getFullScreenContentCallback()!!.onAdDismissedFullScreenContent()
        assertEquals(2, transitions)
        owner.registry.currentState = Lifecycle.State.DESTROYED
    }

    @Test
    fun expiredAdIsSkippedAndDestroyedScreenIgnoresLateCallbacks() = onMain {
        val owner = ScreenOwner()
        var callback: InterstitialAdLoadCallback? = null
        var time = 0L
        var loads = 0
        val controller = PreloadedInterstitialAd(Activity(), owner, "test", now = { time }, loadAd = { _, c ->
            loads++
            callback = c
        })
        val expiredAd = FakeAd()
        callback!!.onAdLoaded(expiredAd)
        time = 55 * 60 * 1000L
        var transitions = 0
        controller.showOrContinue { transitions++ }
        assertEquals(1, transitions)
        assertEquals(0, expiredAd.shows)
        assertEquals(2, loads)

        owner.registry.currentState = Lifecycle.State.DESTROYED
        val lateAd = FakeAd()
        callback!!.onAdLoaded(lateAd)
        controller.showOrContinue { transitions++ }
        assertEquals(0, lateAd.shows)
        assertEquals(1, transitions)
    }

    private fun onMain(block: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
    }

    private class ScreenOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = registry
        init { registry.currentState = Lifecycle.State.RESUMED }
    }

    private class FakeAd : InterstitialAd() {
        var shows = 0
        private var callback: FullScreenContentCallback? = null
        private var paidListener: OnPaidEventListener? = null
        override fun getAdUnitId(): String = "test"
        override fun getFullScreenContentCallback(): FullScreenContentCallback? = callback
        override fun setFullScreenContentCallback(value: FullScreenContentCallback?) { callback = value }
        override fun getOnPaidEventListener(): OnPaidEventListener? = paidListener
        override fun setOnPaidEventListener(value: OnPaidEventListener?) { paidListener = value }
        override fun getResponseInfo(): ResponseInfo = error("ResponseInfo is not used by the transition controller")
        override fun setImmersiveMode(value: Boolean) = Unit
        override fun show(activity: Activity) { shows++ }
    }
}
