package com.example.weatherguide.model

import com.google.gson.annotations.SerializedName

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val sys: Sys,
    val dt: Long,
    val name: String
)
data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("grnd_level") val groundLevel: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
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



data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)


data class Coord(
    val lat: Double,
    val lon: Double
)


fun getNameFromDate(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000)
    val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun createWeatherAllDaysList(weatherDataList: List<DaysWeatherData>): List<WeatherDaysItem> {
    val weatherItemList = mutableListOf<WeatherDaysItem>()
    val encounteredDays = mutableSetOf<String>()

    weatherDataList.forEach { weatherData ->
        val dayName = getNameFromDate(weatherData.dt)
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
    weatherItemList[0].day="Today"
    weatherItemList[1].day="Tomorrow"
    return weatherItemList
}

fun createWeatherHoursList(weatherDataList: List<WeatherData>): List<WeatherHourItem> {
    val weatherItemList = mutableListOf<WeatherHourItem>()
    val hourPattern = Regex("\\d{4}-\\d{2}-\\d{2}\\s(\\d{2}):\\d{2}:\\d{2}")

    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDateString = dateFormat.format(currentDate)
        .substring(0, 10) // Extract current date in "yyyy-MM-dd" format

    weatherDataList.filter { weatherData ->
        val dateString = weatherData.dtTxt.substring(0, 10)
        dateString == currentDateString
    }.forEach { weatherData ->
        val matchResult = hourPattern.find(weatherData.dtTxt)
        val hour = matchResult?.groups?.get(1)?.value?.toIntOrNull() ?: return@forEach
        val time = if (hour < 12) {
            "$hour:00 AM"
        } else {
            "${hour - 12}:00 PM"
        }
        val temperatureCelsius = convertKelvinToCelsius(weatherData.main.temp)
        val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
        weatherItemList.add(WeatherHourItem(time, temperatureCelsius, weatherIconResource))
    }

    return weatherItemList
}

data class WeatherHourItem(
    val hour: String,
    val temp: Int,
    val weatherIconResource: String
)

data class WeatherDaysItem(
    var day: String,
    val weatherIconResource: String,
    val description: String,
    val temp: Int,
)

fun convertKelvinToCelsius(kelvin: Double): Int {
    return (kelvin - 273.15).toInt()
}
