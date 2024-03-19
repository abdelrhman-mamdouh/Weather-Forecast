package com.example.weatherguide.homeScreen.view

import android.location.Location

interface LocationListener {
    fun onLocationChanged(latitude: Double, longitude: Double)

}