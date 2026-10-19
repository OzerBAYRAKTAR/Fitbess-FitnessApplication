package com.bayraktar.healthybackandneck.ui.SplashScreen
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class SplashScreen : AppCompatActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(com.bayraktar.healthybackandneck.utils.LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(com.bayraktar.healthybackandneck.R.style.Theme_HealthyBackandNeck)
        LanguagePreference.getLanguageCode(this)?.let { LocaleHelper.setLocale(this, it) }
        lifecycleScope.launch {
            val store = DataStoreManage.getInstance(applicationContext)
            store.initializeDataStore()
            val destination = if (store.getLog().first()) HomeActivity::class.java else MainActivity::class.java
            startActivity(Intent(this@SplashScreen, destination))
            finish()
        }
    }
}
