package com.bayraktar.healthybackandneck.utils

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAds(private val activity: Activity) {
    private lateinit var rewardedAd: RewardedAd

    fun loadRewardedAds(adUnitIdl: Int) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            activity,
            activity.getString(adUnitIdl),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    rewardedAd = p0
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    //rewardedAd = p0
                }
            }
        )
    }

    fun showRewardAds(adUnitIdl: Int, afterCodeUnlockDay: (RewardItem) -> Unit) {
        rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                //rewardedAd = null
                loadRewardedAds(adUnitIdl)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                //rewardedAd = null
            }
        }
        rewardedAd.show(activity){
            afterCodeUnlockDay(it)
        }
    }
}