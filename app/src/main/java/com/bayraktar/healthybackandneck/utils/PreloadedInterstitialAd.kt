package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.bayraktar.healthybackandneck.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/** An ad may be shown only by a user action, never by a load callback. */
internal class PreloadedInterstitialAd(
    private val activity: Activity,
    private val owner: LifecycleOwner,
    private val adUnitId: String,
    private val now: () -> Long = SystemClock::elapsedRealtime,
    private val loadAd: (String, InterstitialAdLoadCallback) -> Unit = { id, callback ->
        AdsSdk.whenReady(owner) {
            InterstitialAd.load(activity.applicationContext, id, AdRequest.Builder().build(), callback)
        }
    }
) : DefaultLifecycleObserver {
    private var cachedAd: InterstitialAd? = null
    private var loadedAt = 0L
    private var loading = false
    private var showing = false
    private var disposed = false

    init {
        owner.lifecycle.addObserver(this)
        preload()
    }

    override fun onResume(owner: LifecycleOwner) {
        preload()
    }

    private fun preload() {
        if (!RevenueCatManager.adsMayLoad()) return
        if (disposed || loading || showing) return
        if (cachedAd != null && now() - loadedAt < MAX_AGE_MS) return
        cachedAd = null
        loading = true
        val id = if (BuildConfig.DEBUG) TEST_AD_UNIT_ID else adUnitId
        loadAd(id, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                loading = false
                if (disposed) return
                cachedAd = ad
                loadedAt = now()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loading = false
                cachedAd = null
            }
        })
    }

    fun showOrContinue(onContinue: () -> Unit) {
        if (!RevenueCatManager.adsMayLoad()) {
            onContinue()
            return
        }
        if (disposed || showing || activity.isFinishing || activity.isDestroyed ||
            !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return

        val ad = cachedAd
        cachedAd = null
        if (ad == null || now() - loadedAt >= MAX_AGE_MS) {
            // The transition does not wait for this or any future network request.
            onContinue()
            preload()
            return
        }

        showing = true
        var completed = false
        fun finish() {
            if (completed) return
            completed = true
            showing = false
            if (!disposed && owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                onContinue()
            }
            preload()
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() = finish()
            override fun onAdFailedToShowFullScreenContent(error: AdError) = finish()
        }
        ad.show(activity)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposed = true
        cachedAd = null
        owner.lifecycle.removeObserver(this)
    }

    private companion object {
        const val MAX_AGE_MS = 55 * 60 * 1000L
        const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}
