package com.bayraktar.healthybackandneck.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ActivityHomeBinding
import com.bayraktar.healthybackandneck.ui.Exercise.ExerciseFragment
import com.bayraktar.healthybackandneck.ui.HomePage.HomePageFragment
import com.bayraktar.healthybackandneck.ui.Profile.ProfileFragment
import com.bayraktar.healthybackandneck.ui.Settings.SettingsFragment
import com.bayraktar.healthybackandneck.ui.Statistics.StatisticsFragment
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_home)
        toogle= ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        navigationView= binding.navView
        toogle.syncState()

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {item: MenuItem ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.mainSC ->{
                    fragment = HomePageFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.exercisescreen ->{
                    fragment = ExerciseFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.statisticscreen ->{
                    fragment = StatisticsFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.profilescreen ->{
                    fragment = ProfileFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.settings ->{
                    fragment = SettingsFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.connect ->{
                    fragment = HomePageFragment()
                    loadFragment(fragment)
                    true
                }
                else -> {
                    false
                }
            }

        })

    }
    private fun loadFragment(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame,fragment).commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        fragmentTransaction.addToBackStack(null)
    }
}