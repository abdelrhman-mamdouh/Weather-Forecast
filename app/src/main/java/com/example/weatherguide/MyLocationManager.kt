package com.example.weatherguide
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherguide.homeScreen.view.LocationListener
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.utills.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices

class MyLocationManager(private val context: Context) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun enableLocationServices() {
        Toast.makeText(context, "Turn on Locations", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(listener: LocationListener) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val sharedPreferences = context.getSharedPreferences("current-location", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putFloat("latitudeFromMap", location.latitude.toFloat())
                    editor.putFloat("longitudeFromMap", location.longitude.toFloat())
                    editor.apply()
                    var myObject = Util.getSharedFlowObject(context)
                    listener.onLocationChanged(myObject)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error getting location: $exception")
            }
    }
    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}