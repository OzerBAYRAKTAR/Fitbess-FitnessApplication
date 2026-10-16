package com.bayraktar.healthybackandneck

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.utils.RewardedAds
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RewardedMoveTest {
    @Test fun dismissalDoesNotGrantAndAnEarnedRewardIsGrantedOnlyOnceAfterHostRecreation() = onMain {
        val owner = Owner()
        var loaded: RewardedAdLoadCallback? = null
        val controller = RewardedAds(Activity(), owner, loadAd = { _, callback -> loaded = callback })
        controller.loadRewardedAds(R.string.rewarded_ad1)
        val cancelled = FakeAd()
        loaded!!.onAdLoaded(cancelled)
        var grants = 0
        assertTrue(controller.showRewardAds(retainEarnedReward = true) { grants++ })
        assertFalse(controller.showRewardAds { grants += 100 })
        cancelled.fullScreenContentCallback!!.onAdDismissedFullScreenContent()
        assertEquals(0, grants)
        val earned = FakeAd()
        loaded!!.onAdLoaded(earned)
        assertTrue(controller.showRewardAds(retainEarnedReward = true) { grants++ })
        owner.registry.currentState = Lifecycle.State.DESTROYED
        earned.listener!!.onUserEarnedReward(earned.rewardItem)
        earned.listener!!.onUserEarnedReward(earned.rewardItem)
        assertEquals(1, grants)
    }
    @Test fun failedShowNeverGrantsReward() = onMain {
        val owner = Owner()
        var loaded: RewardedAdLoadCallback? = null
        val controller = RewardedAds(Activity(), owner, loadAd = { _, callback -> loaded = callback })
        controller.loadRewardedAds(R.string.rewarded_ad1)
        val ad = FakeAd()
        loaded!!.onAdLoaded(ad)
        var grants = 0
        controller.showRewardAds { grants++ }
        // The host has gone, so failure handling should not attempt a UI toast.
        owner.registry.currentState = Lifecycle.State.DESTROYED
        ad.fullScreenContentCallback!!.onAdFailedToShowFullScreenContent(AdError(0, "Synthetic failure", "test"))
        assertEquals(0, grants)
    }
    private fun onMain(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
    private class Owner : LifecycleOwner {
        val registry = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
        override val lifecycle: Lifecycle get() = registry
    }
    private class FakeAd : RewardedAd() {
        var listener: OnUserEarnedRewardListener? = null
        private var callback: FullScreenContentCallback? = null
        private var paid: OnPaidEventListener? = null
        private var metadata: OnAdMetadataChangedListener? = null
        override fun getAdUnitId() = "test"
        override fun getAdMetadata() = Bundle()
        override fun getRewardItem(): RewardItem = object : RewardItem {
            override fun getAmount() = 1
            override fun getType() = "move"
        }
        override fun getFullScreenContentCallback() = callback
        override fun setFullScreenContentCallback(value: FullScreenContentCallback?) { callback = value }
        override fun getOnPaidEventListener() = paid
        override fun setOnPaidEventListener(value: OnPaidEventListener?) { paid = value }
        override fun getOnAdMetadataChangedListener() = metadata
        override fun setOnAdMetadataChangedListener(value: OnAdMetadataChangedListener?) { metadata = value }
        override fun getResponseInfo(): ResponseInfo = error("Not used by reward policy")
        override fun setServerSideVerificationOptions(value: ServerSideVerificationOptions?) = Unit
        override fun setImmersiveMode(value: Boolean) = Unit
        override fun show(activity: Activity, value: OnUserEarnedRewardListener) { listener = value }
    }
}
