package com.bayraktar.healthybackandneck.utils

import android.content.Context
import android.preference.PreferenceManager

object LanguagePreference {
    private const val PREF_LANGUAGE_CODE = "pref_language_code"

    fun getLanguageCode(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(PREF_LANGUAGE_CODE, null)
    }

    fun setLanguageCode(context: Context, languageCode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(PREF_LANGUAGE_CODE, languageCode).apply()
    }
}