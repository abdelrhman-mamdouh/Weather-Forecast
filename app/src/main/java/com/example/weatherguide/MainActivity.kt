package com.example.weatherguide

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weatherguide.HomeScreen.view.HomeFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var locationManager: MyLocationManager
    private val locationChannel = Channel<Pair<Double, Double>>()

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = MyLocationManager(context = this)

        if (locationManager.checkPermissions()) {
            if (locationManager.isLocationEnabled()) {
                locationManager.startLocationUpdates(this)
            } else {
                locationManager.enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.REQUEST_LOCATION_CODE
            )
        }

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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_LOCATION_CODE) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.startLocationUpdates(this)
            }
        }
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this@MainActivity).getFromLocation(longitude!!, latitude!!, 1)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (geocoder != null) {
                    if (geocoder.isNotEmpty()) {
                        val address = geocoder[0]
                        val addressText =
                            "${address?.getAddressLine(0)}, ${address?.locality}, ${address?.countryName}"
                           locationChannel.send(latitude to longitude)
                        withContext(Dispatchers.Main) {

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getLocationChannel(): Channel<Pair<Double, Double>> {
        return locationChannel
    }
    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
    }
}



