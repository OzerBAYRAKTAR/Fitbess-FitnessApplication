package com.bayraktar.healthybackandneck.ui.SplashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.ActivityMainBinding
import com.bayraktar.healthybackandneck.databinding.ActivitySplashScreenBinding
import com.bayraktar.healthybackandneck.ui.FirstActivity.MainActivity
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var dataStoreManager: DataStoreManage

    private var downToUp : Animation ?= null
    private var upToDown : Animation ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreManager = DataStoreManage.getInstance(applicationContext)


        runBlocking {
            dataStoreManager.initializeDataStore()
        }

        downToUp = AnimationUtils.loadAnimation(applicationContext,R.anim.downtoup)
        upToDown = AnimationUtils.loadAnimation(applicationContext,R.anim.uptodown)
        binding.fit.animation = downToUp
        binding.keep.animation = upToDown

        navigateHome()
        changeStatusBarColor(resources.getColor(R.color.white, theme))



        val savedLanguageCode = LanguagePreference.getLanguageCode(this)
        savedLanguageCode?.let {
            LocaleHelper.setLocale(this, it)
        }



    }
    private fun navigateHome() {
        Handler(Looper.getMainLooper()).postDelayed({
        this.lifecycleScope.launch {
            dataStoreManager.getLog().collect() { isUserLogged ->
                if (isUserLogged) {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                }else {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
        }
        }, 2000)

    }
    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For devices with light status bar, change the icon colors to be darker
            val decor = window.decorView
            if (color == resources.getColor(R.color.black, theme)) {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decor.systemUiVisibility = 0
            }
        }
    }
}