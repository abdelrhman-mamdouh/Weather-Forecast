package com.example.weatherguide

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.example.weatherguide.databinding.ActivityMainBinding
import com.example.weatherguide.databinding.InitialSettingsDialogBinding
import com.example.weatherguide.utills.Util
import com.github.matteobattilana.weather.PrecipType

class MainActivity : AppCompatActivity(), MainActivityListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setupSettings(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        networkChangeReceiver = NetworkChangeReceiver()
        intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        registerReceiver(networkChangeReceiver, intentFilter)
        if (isFirstRun()) {
            showInitialSettingsDialog()
        } else {
            Util.setupSettings(this)
            startActivity()
        }
    }

    private fun startActivity() {
        binding.apply {
            navigatorLayout.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.homeFragment -> {
                        binding.myToolbar.title = "Home"
                        navigateToFragment(R.id.homeFragment)
                    }

                    R.id.favoritesFragment -> {
                        binding.myToolbar.title = "Favorites"
                        navigateToFragment(R.id.favoritesFragment)
                    }

                    R.id.alertsFragment -> {
                        binding.myToolbar.title = "Alerts"
                        navigateToFragment(R.id.alertsFragment)
                    }

                    R.id.settingsFragment -> {
                        binding.myToolbar.title = "Settings"
                        navigateToFragment(R.id.settingsFragment)
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            binding.myToolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToFragment(fragmentId: Int) {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
        navController.navigate(fragmentId)
    }

    private fun isFirstRun(): Boolean {
        return !sharedPreferences.getBoolean("isFirstRun", false)
    }

    private fun showInitialSettingsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogBinding = InitialSettingsDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialogBinding.btnSave.setOnClickListener {
            with(sharedPreferences.edit()) {
                val locationSelectedId = dialogBinding.locationRadioGroup.checkedRadioButtonId
                val radioButtonLocation =
                    dialogBinding.root.findViewById<RadioButton>(locationSelectedId)

                putString("location", radioButtonLocation.text.toString())

                val notificationSelectedId = dialogBinding.notifyRadioGroup.checkedRadioButtonId
                val radioButtonNotification =
                    dialogBinding.root.findViewById<RadioButton>(notificationSelectedId)

                putString("notification", radioButtonNotification.text.toString())

                putString("language", "en")

                putString("windSpeed", resources.getString(R.string.meter_sec))

                putString("temperature", resources.getString(R.string.celsius))

                putString("theme", resources.getString(R.string.light))

                putBoolean("isFirstRun", true)
                apply()

            }
            dialog.dismiss()
        }
        dialogBuilder.setCancelable(false)
        dialog.show()
    }

    override fun updateBackgroundAnimation(condition: String) {
        try {

            val precipType = PrecipType.valueOf(condition)
            binding.weatherView.setWeatherData(precipType)
            binding.weatherView.angle = 20
            binding.weatherView.speed = 500
            binding.weatherView.scaleFactor = 2f
        } catch (e: IllegalArgumentException) {

            binding.weatherView.setWeatherData(PrecipType.CLEAR)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }
}