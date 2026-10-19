package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bayraktar.healthybackandneck.BuildConfig
import com.bayraktar.healthybackandneck.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch

class RewardedAds(
    private val activity: Activity,
    private val owner: LifecycleOwner,
    private val loadAd: (String, RewardedAdLoadCallback) -> Unit = { id, callback ->
        AdsSdk.whenReady(owner) { RewardedAd.load(activity.applicationContext, id, AdRequest.Builder().build(), callback) }
    },
) : DefaultLifecycleObserver {
    private var rewardedAd: RewardedAd? = null
    private var configuredUnit: Int? = null
    private var loadedAt = 0L
    private var loading = false
    private var showing = false
    private var disposed = false

    init {
        owner.lifecycle.addObserver(this)
        owner.lifecycleScope.launch {
            RevenueCatManager.adFree.collect { status ->
                if (status == true) rewardedAd = null
                else if (status == false) configuredUnit?.let(::loadRewardedAds)
            }
        }
    }

    fun loadRewardedAds(adUnitId: Int) {
        configuredUnit = adUnitId
        if (!RevenueCatManager.adsMayLoad()) return
        if (disposed || loading || showing) return
        if (rewardedAd != null && SystemClock.elapsedRealtime() - loadedAt < MAX_AGE_MS) return
        rewardedAd = null
        loading = true
        val id = if (BuildConfig.DEBUG) TEST_AD_UNIT_ID else activity.getString(adUnitId)
        loadAd(id, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                loading = false
                if (disposed) return
                rewardedAd = ad
                loadedAt = SystemClock.elapsedRealtime()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loading = false
                rewardedAd = null
            }
        })
    }

    fun showRewardAds(retainEarnedReward: Boolean = false, onReward: (RewardItem) -> Unit): Boolean {
        if (!RevenueCatManager.adsMayLoad()) return false
        if (disposed || showing || activity.isFinishing || activity.isDestroyed ||
            !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return false
        val ad = rewardedAd
        rewardedAd = null
        if (ad == null || SystemClock.elapsedRealtime() - loadedAt >= MAX_AGE_MS) {
            Toast.makeText(activity, R.string.ad_not_ready, Toast.LENGTH_SHORT).show()
            configuredUnit?.let(::loadRewardedAds)
            return false
        }

        showing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                showing = false
                configuredUnit?.let(::loadRewardedAds)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                showing = false
                if (!disposed) Toast.makeText(activity, R.string.ad_not_ready, Toast.LENGTH_SHORT).show()
                configuredUnit?.let(::loadRewardedAds)
            }
        }
        var earned = false
        ad.show(activity) { reward ->
            // Dismissal or a load failure must never grant the reward.
            if (!earned && (!disposed || retainEarnedReward)) {
                earned = true
                onReward(reward)
            }
        }
        return true
    }

    override fun onResume(owner: LifecycleOwner) {
        configuredUnit?.let(::loadRewardedAds)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposed = true
        rewardedAd = null
        owner.lifecycle.removeObserver(this)
    }

    private companion object {
        const val MAX_AGE_MS = 55 * 60 * 1000L
        const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }
}
