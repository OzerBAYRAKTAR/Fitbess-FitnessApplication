package com.bayraktar.healthybackandneck.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** Keep the banner in its existing layout slot and release it with the Fragment view. */
fun AdView.loadForLifecycle(owner: LifecycleOwner) {
    visibility = android.view.View.GONE
    var requested = false
    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) = resume()
        override fun onPause(owner: LifecycleOwner) = pause()
        override fun onDestroy(owner: LifecycleOwner) {
            destroy()
            owner.lifecycle.removeObserver(this)
        }
    })
    owner.lifecycleScope.launch {
        RevenueCatManager.adFree.collect { adFree ->
            if (adFree != false) {
                visibility = android.view.View.GONE
                pause()
            } else {
                visibility = android.view.View.VISIBLE
                if (!requested) {
                    requested = true
                    AdsSdk.whenReady(owner) {
                        if (RevenueCatManager.adsMayLoad()) loadAd(AdRequest.Builder().build())
                    }
                }
            }
        }
    }
}
