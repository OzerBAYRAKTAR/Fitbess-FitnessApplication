package com.bayraktar.healthybackandneck.ui

import android.app.Application
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.AdsSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        com.bayraktar.healthybackandneck.utils.LanguagePreference.getLanguageCode(this)?.let { LocaleHelper.setLocale(this, it) }
        AdsSdk.initialize(this)

    }

}
