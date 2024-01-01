package com.bayraktar.healthybackandneck.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ActivityHomeBinding
import com.bayraktar.healthybackandneck.utils.homeFragmentListener
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), homeFragmentListener {

    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navControllerBar: NavController
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.id_homepage_fragment
                && destination.id != R.id.id_profile_fragment
                && destination.id != R.id.id_statistics_fragment
                && destination.id != R.id.id_food_fragment
                && destination.id != R.id.foodDetailFragment) {
                // If the destination is not the home fragment, hide the bottom app bar
                binding.bottomBar.visibility = View.GONE
            } else {
                // If navigating back to the home fragment, show the bottom app bar
                binding.bottomBar.visibility = View.VISIBLE
            }
        }


        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
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