package com.bayraktar.healthybackandneck.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

/** Keep the banner in its existing layout slot and release it with the Fragment view. */
fun AdView.loadForLifecycle(owner: LifecycleOwner) {
    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) = resume()
        override fun onPause(owner: LifecycleOwner) = pause()
        override fun onDestroy(owner: LifecycleOwner) {
            destroy()
            owner.lifecycle.removeObserver(this)
        }
    })
    AdsSdk.whenReady(owner) { loadAd(AdRequest.Builder().build()) }
}
