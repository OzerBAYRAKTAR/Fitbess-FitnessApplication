package com.bayraktar.healthybackandneck.ui

import android.app.Application
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.AdsSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LocaleHelper.setLocale(this, "tr")
        AdsSdk.initialize(this)

    }

}
