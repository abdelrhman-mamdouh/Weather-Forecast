package com.example.weatherguide.model

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.weatherguide.R
import com.google.gson.annotations.SerializedName

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<Alert>?
)
data class Alert(
    @SerializedName("sender_name")
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)
data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>
)


data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>,
    val pop: Double
)


data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val summary: String,
    val temp: Temperature,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>,
    val clouds: Int,
    val pop: Double,
    val rain: Double?,
    val uvi: Double
)


data class Temperature(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)


data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)


fun getNameFromDate(unixTimestamp: Long,context: Context): String {
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


fun createWeatherAllDaysList(context: Context,weatherDataList: List<DailyWeather>): List<WeatherDaysItem> {
    val weatherItemList = mutableListOf<WeatherDaysItem>()
    val encounteredDays = mutableSetOf<String>()

    weatherDataList.forEach { weatherData ->
        val dayName = getNameFromDate(weatherData.dt,context)
        if (!encounteredDays.contains(dayName)) {
            val temperatureCelsius = convertKelvinToCelsius(weatherData.temp.day)
            val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
            val weatherDescription = weatherData.weather.firstOrNull()?.description ?: ""
            weatherItemList.add(
                WeatherDaysItem(
                    dayName,
                    weatherIconResource,
                    weatherDescription,
                    temperatureCelsius
                )
            )
            encounteredDays.add(dayName)
        }
    }
    weatherItemList[0].dayName=getString(context,R.string.today)
    weatherItemList[1].dayName=getString(context,R.string.tomorrow)
    return weatherItemList
}

fun createCurrentDayWeatherHoursList(weatherDataList: List<HourlyWeather>): List<WeatherHourItem> {
    val currentDate = Calendar.getInstance().time
    val currentDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)

    return weatherDataList.filter { timestampToDateString(it.dt) == currentDateString }
        .mapNotNull { weatherData ->
            val time = timestampToTimeString(weatherData.dt)
            val temperatureCelsius = convertKelvinToCelsius(weatherData.temp)
            val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
            WeatherHourItem(time, temperatureCelsius, weatherIconResource)
        }
}
data class WeatherHourItem(
    val time: String,
    val temperature: Int,
    val weatherIconResource: String
)data class WeatherDaysItem(
    var dayName: String,
    val weatherIconResource: String,
    val weatherDescription: String,
    val temperature: Int,
)
fun timestampToDateString(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}

fun timestampToTimeString(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
}
data class LocationSuggestions(
    @SerializedName("features")
    val features: List<Suggestions>
)

data class Suggestions(
    @SerializedName("properties")
    val properties: Properties
)

data class Properties(
    @SerializedName("formatted")
    val formatted: String,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("lat")
    val lat: Double,
)

fun convertKelvinToCelsius(kelvin: Double): Int {
    return (kelvin - 273.15).toInt()
}
