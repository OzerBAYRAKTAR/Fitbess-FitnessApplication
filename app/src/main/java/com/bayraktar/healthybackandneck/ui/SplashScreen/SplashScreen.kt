package com.bayraktar.healthybackandneck.ui.SplashScreen
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.ActivitySplashScreenBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.bayraktar.healthybackandneck.utils.fitContentToSystemBars
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class SplashScreen : AppCompatActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(com.bayraktar.healthybackandneck.utils.LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguagePreference.getLanguageCode(this)?.let { LocaleHelper.setLocale(this, it) }
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitContentToSystemBars(binding.root)
        lifecycleScope.launch {
            val store = DataStoreManage.getInstance(applicationContext)
            store.initializeDataStore()
            val destination = if (store.getLog().first()) HomeActivity::class.java else MainActivity::class.java
            startActivity(Intent(this@SplashScreen, destination))
            finish()
        }
    }
}
