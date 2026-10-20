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
    enum class ShowState { PREPARING, SHOWING, UNAVAILABLE }

    private data class PendingShow(
        val retainEarnedReward: Boolean,
        val onStateChanged: ((ShowState) -> Unit)?,
        val onReward: (RewardItem) -> Unit,
    )

    private var rewardedAd: RewardedAd? = null
    private var configuredUnit: Int? = null
    private var loadedAt = 0L
    private var loading = false
    private var showing = false
    private var disposed = false
    private var pendingShow: PendingShow? = null

    init {
        owner.lifecycle.addObserver(this)
        owner.lifecycleScope.launch {
            RevenueCatManager.adFree.collect { status ->
                if (status == true) {
                    rewardedAd = null
                    pendingShow?.onStateChanged?.invoke(ShowState.UNAVAILABLE)
                    pendingShow = null
                }
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
                showPendingIfReady()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loading = false
                rewardedAd = null
                pendingShow?.let { pending ->
                    pendingShow = null
                    pending.onStateChanged?.invoke(ShowState.UNAVAILABLE)
                    if (!disposed) Toast.makeText(activity, R.string.ad_not_ready, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun showRewardAds(
        retainEarnedReward: Boolean = false,
        onStateChanged: ((ShowState) -> Unit)? = null,
        onReward: (RewardItem) -> Unit,
    ): Boolean {
        if (!RevenueCatManager.adsMayLoad()) {
            onStateChanged?.invoke(ShowState.UNAVAILABLE)
            return false
        }
        if (disposed || showing || pendingShow != null || activity.isFinishing || activity.isDestroyed) {
            onStateChanged?.invoke(ShowState.UNAVAILABLE)
            return false
        }
        val ad = rewardedAd
        if (ad == null || SystemClock.elapsedRealtime() - loadedAt >= MAX_AGE_MS) {
            pendingShow = PendingShow(retainEarnedReward, onStateChanged, onReward)
            onStateChanged?.invoke(ShowState.PREPARING)
            val unit = configuredUnit
            if (unit == null) {
                pendingShow = null
                onStateChanged?.invoke(ShowState.UNAVAILABLE)
                return false
            }
            loadRewardedAds(unit)
            return true
        }
        rewardedAd = null
        return showLoadedAd(ad, PendingShow(retainEarnedReward, onStateChanged, onReward))
    }

    private fun showPendingIfReady() {
        val pending = pendingShow ?: return
        if (!owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
        val ad = rewardedAd ?: return
        pendingShow = null
        rewardedAd = null
        showLoadedAd(ad, pending)
    }

    private fun showLoadedAd(ad: RewardedAd, pending: PendingShow): Boolean {
        if (disposed || activity.isFinishing || activity.isDestroyed ||
            !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            rewardedAd = ad
            pending.onStateChanged?.invoke(ShowState.UNAVAILABLE)
            return false
        }
        showing = true
        pending.onStateChanged?.invoke(ShowState.SHOWING)
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                showing = false
                configuredUnit?.let(::loadRewardedAds)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                showing = false
                pending.onStateChanged?.invoke(ShowState.UNAVAILABLE)
                if (!disposed) Toast.makeText(activity, R.string.ad_not_ready, Toast.LENGTH_SHORT).show()
                configuredUnit?.let(::loadRewardedAds)
            }
        }
        var earned = false
        ad.show(activity) { reward ->
            // Dismissal or a load failure must never grant the reward.
            if (!earned && (!disposed || pending.retainEarnedReward)) {
                earned = true
                pending.onReward(reward)
            }
        }
        return true
    }

    override fun onResume(owner: LifecycleOwner) {
        showPendingIfReady()
        configuredUnit?.let(::loadRewardedAds)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposed = true
        rewardedAd = null
        pendingShow?.onStateChanged?.invoke(ShowState.UNAVAILABLE)
        pendingShow = null
        owner.lifecycle.removeObserver(this)
    }

    private companion object {
        const val MAX_AGE_MS = 55 * 60 * 1000L
        const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }
}
