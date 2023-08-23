package com.bayraktar.healthybackandneck.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ActivityHomeBinding
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
            when (item.itemId) {
                R.id.mainSC ->{
                    true
                }
                R.id.exercisescreen ->{
                    true
                }
                R.id.statisticscreen ->{
                    true
                }
                R.id.profilescreen ->{
                    true
                }
                R.id.settings ->{
                    true
                }
                R.id.connect ->{
                    true
                }
                else -> {
                    false
                }
            }

        })

    }
}