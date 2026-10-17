package com.bayraktar.healthybackandneck.utils
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
object LocaleHelper {
    fun wrap(context: Context): Context {
        val language = LanguagePreference.getLanguageCode(context) ?: return context
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        return context.createConfigurationContext(config)
    }
    fun setLocale(context: Context, language: String) {
        Locale.setDefault(Locale.forLanguageTag(language))
    }
}
