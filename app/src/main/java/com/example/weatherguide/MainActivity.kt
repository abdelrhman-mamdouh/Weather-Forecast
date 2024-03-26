package com.example.weatherguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.productsmvvm.db.WeatherDatabase
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigator_layout)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigator_layout)
        toolbar = findViewById(R.id.my_toolbar)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    toolbar.title = "Home"
                    navigateToFragment(R.id.homeFragment)
                }

                R.id.favoritesFragment -> {
                    toolbar.title = "Favorites"
                    navigateToFragment(R.id.favoritesFragment)
                }

                R.id.alertsFragment -> {
                    toolbar.title = "Alerts"
                    navigateToFragment(R.id.alertsFragment)
                }

                R.id.settingsFragment -> {
                    toolbar.title = "Settings"
                    navigateToFragment(R.id.settingsFragment)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToFragment(fragmentId: Int) {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
        navController.navigate(fragmentId)
    }

}



