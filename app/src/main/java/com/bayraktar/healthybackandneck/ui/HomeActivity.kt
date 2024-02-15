package com.bayraktar.healthybackandneck.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ActivityHomeBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.homeFragmentListener
import com.bayraktar.healthybackandneck.utils.LanguagePreference
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), homeFragmentListener {

    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navControllerBar: NavController
    private lateinit var binding: ActivityHomeBinding

    companion object {
       var mInterstitialAd: InterstitialAd? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // window.setFlags(
      //     WindowManager.LayoutParams.FLAG_FULLSCREEN,
      //     WindowManager.LayoutParams.FLAG_FULLSCREEN
      // )

      // var adRequest = AdRequest.Builder().build()

      // InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
      //     override fun onAdFailedToLoad(adError: LoadAdError) {
      //         mInterstitialAd = null
      //     }

      //     override fun onAdLoaded(interstitialAd: InterstitialAd) {
      //         mInterstitialAd = interstitialAd
      //     }
      // })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.id_homepage_fragment
                && destination.id != R.id.id_profile_fragment
                && destination.id != R.id.id_statistics_fragment
                && destination.id != R.id.id_food_fragment
                && destination.id != R.id.foodDetailFragment) {
                binding.bottomBar.visibility = View.GONE
            } else {
                binding.bottomBar.visibility = View.VISIBLE
            }
        }

        setUpBotMenu()


    }

    private fun setUpBotMenu() = with(binding) {
        val popupMenu = android.widget.PopupMenu(applicationContext, null)
        popupMenu.inflate(R.menu.bottom_menu)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun hideConstraintLayout() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.cnnm3)
        constraintLayout?.visibility = View.GONE
    }

}