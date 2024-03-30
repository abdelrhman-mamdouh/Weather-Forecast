package com.example.weatherguide.utills

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherguide.R
import com.example.weatherguide.model.SharedFlowObject

class Util {
    companion object{
        fun getSharedFlowObject(context: Context): SharedFlowObject {
            val currentLocation = context.getSharedPreferences("current-location", Context.MODE_PRIVATE)
            val latitude = currentLocation.getFloat("latitudeFromMap", 0.0f).toDouble()
            val longitude = currentLocation.getFloat("longitudeFromMap", 0.0f).toDouble()

            val settings = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            var language = settings.getString("language", "")
            var temperature = settings.getString("temperature", "")
            var windSpeedUnit = settings.getString("windSpeed", "")
            if (language == context.resources.getString(R.string.english)) {
                language = "en"
            } else {
                language = "ar"
            }

            if (temperature == context.resources.getString(R.string.celsius)) {
                temperature = "Celsius"
            } else if (temperature == context.resources.getString(R.string.kelvin)) {
                temperature = "Kelvin"
            } else {
                temperature = "Fahrenheit"
            }

            if (windSpeedUnit ==context.resources.getString(R.string.meter_sec)) {
                windSpeedUnit = "Meter"
            } else {
                windSpeedUnit = "Mile"
            }

            return SharedFlowObject(latitude, longitude, language, temperature, windSpeedUnit)
        }
    }

}