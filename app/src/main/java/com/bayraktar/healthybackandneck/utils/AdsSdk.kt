package com.bayraktar.healthybackandneck.utils

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** SDK initialization cannot block rendering or be repeated for each Fragment. */
object AdsSdk {
    private val ready = MutableStateFlow(false)

    fun initialize(context: Context) {
        val applicationContext = context.applicationContext
        Thread({
            MobileAds.initialize(applicationContext) { ready.value = true }
        }, "admob-initialization").start()
    }

    fun whenReady(owner: LifecycleOwner, load: () -> Unit) {
        owner.lifecycleScope.launch {
            ready.first { it }
            load()
        }
    }
}
