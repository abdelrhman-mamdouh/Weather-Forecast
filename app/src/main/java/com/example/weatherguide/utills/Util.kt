package com.example.weatherguide.utills

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.weatherguide.R
import com.example.weatherguide.model.DailyWeather
import com.example.weatherguide.model.HourlyWeather
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.WeatherDaysItem
import com.example.weatherguide.model.WeatherHourItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Util {
    companion object {
        fun getNameFromDate(unixTimestamp: Long, context: Context): String {
            val date = Date(unixTimestamp * 1000)
            var sharedPreferences =
                context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("language", "")
            if (language == "Arabic") {
                val locale = Locale("ar", "EG")
                val simpleDateFormat = SimpleDateFormat("EEEE", locale)
                return simpleDateFormat.format(date)
            } else {
                val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                return simpleDateFormat.format(date)
            }
        }


        fun createWeatherAllDaysList(
            context: Context,
            weatherDataList: List<DailyWeather>
        ): List<WeatherDaysItem> {
            val weatherItemList = mutableListOf<WeatherDaysItem>()
            val encounteredDays = mutableSetOf<String>()

            weatherDataList.forEach { weatherData ->
                val dayName = getNameFromDate(weatherData.dt, context)
                if (!encounteredDays.contains(dayName)) {

                    val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
                    val weatherDescription = weatherData.weather.firstOrNull()?.description ?: ""
                    weatherItemList.add(
                        WeatherDaysItem(
                            dayName,
                            weatherIconResource,
                            weatherDescription,
                            weatherData.temp.day.toInt()
                        )
                    )
                    encounteredDays.add(dayName)
                }
            }
            weatherItemList[0].dayName = ContextCompat.getString(context, R.string.today)
            weatherItemList[1].dayName = ContextCompat.getString(context, R.string.tomorrow)
            return weatherItemList
        }

        fun createCurrentDayWeatherHoursList(weatherDataList: List<HourlyWeather>): List<WeatherHourItem> {
            val currentDate = Calendar.getInstance().time
            val currentDateString =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)

            return weatherDataList.filter { timestampToDateString(it.dt) == currentDateString }
                .mapNotNull { weatherData ->
                    val time = timestampToTimeString(weatherData.dt)

                    val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
                    WeatherHourItem(time, weatherData.temp.toInt(), weatherIconResource)
                }
        }

        fun timestampToDateString(timestamp: Long): String {
            val date = Date(timestamp * 1000)
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        }

        fun timestampToTimeString(timestamp: Long): String {
            val date = Date(timestamp * 1000)
            return SimpleDateFormat("h a", Locale.getDefault()).format(date)
        }


        fun getSharedFlowObject(context: Context): SharedFlowObject {
            val currentLocation =
                context.getSharedPreferences("current-location", Context.MODE_PRIVATE)
            val latitude = currentLocation.getFloat("latitudeFromMap", 0.0f).toDouble()
            val longitude = currentLocation.getFloat("longitudeFromMap", 0.0f).toDouble()

            val settings = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
            var language = settings.getString("language", "en")
            var temperature = settings.getString("temperature", "")

            var windSpeedUnit = settings.getString("windSpeed", "")
            if (language == "en") {
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

            if (windSpeedUnit == context.resources.getString(R.string.meter_sec)) {
                windSpeedUnit = "Meter"
            } else {
                windSpeedUnit = "Mile"
            }

            return SharedFlowObject(latitude, longitude, language, temperature, windSpeedUnit)
        }

        fun setupSettings(activity: AppCompatActivity) {
            val sharedPreferences =
                activity.getSharedPreferences("MySettings", AppCompatActivity.MODE_PRIVATE)
            val language =
                sharedPreferences.getString("language", activity.getString(R.string.english))
            val theme = sharedPreferences.getString("theme", activity.getString(R.string.light))

            if (language == "ar") {
                setAppLocale(language, "EG", activity)
            } else {
                setAppLocale("en", "", activity)
            }

            if (theme == activity.getString(R.string.dark)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun setAppLocale(language: String, country: String, activity: Activity) {
            val locale = Locale(language, country)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        }
    }
}



