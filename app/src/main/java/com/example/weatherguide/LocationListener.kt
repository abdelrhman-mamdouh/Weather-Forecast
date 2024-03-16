package com.example.weatherguide

import android.location.Location

interface LocationListener {
    fun onLocationChanged(latitude: Double, longitude: Double)

}