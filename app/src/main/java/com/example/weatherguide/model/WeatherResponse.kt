package com.example.weatherguide.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)

data class WeatherItem(
    val dt: String,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
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

data class Sys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)

fun createWeatherAllDaysList(weatherDataList: WeatherResponse): List<WeatherDaysItem> {
    val weatherItemList = mutableListOf<WeatherDaysItem>()
    val encounteredDays = mutableSetOf<String>()

    weatherDataList.list.forEach { weatherData ->
        val dayName = getShortDayNameFromDate(weatherData.dt_txt)

        if (!encounteredDays.contains(dayName)) {
            val temperatureCelsius = convertKelvinToCelsius(weatherData.main.temp)
            val weatherIconResource = weatherData.weather.firstOrNull()?.icon ?: ""
            val weatherDescription = weatherData.weather.firstOrNull()?.description ?: ""
            weatherItemList.add(WeatherDaysItem(dayName, weatherIconResource, weatherDescription, temperatureCelsius))
            encounteredDays.add(dayName)
        }
    }

    return weatherItemList
}
 fun getCurrentDayWeatherData(weatherDataList: List<WeatherItem>): WeatherItem? {
    // Get current hour range
    val currentHourRange = getCurrentHourRange()

    // Get current date
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDateString = dateFormat.format(currentDate).substring(0, 10)

    // Filter weather data for current date and hour range
    val filteredData = weatherDataList.filter { weatherData ->
        val dateString = weatherData.dt_txt.substring(0, 10)
        dateString == currentDateString
    }.filter { weatherData ->
        val date = dateFormat.parse(weatherData.dt_txt)
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hour in currentHourRange
        } else {
            false
        }
    }

    // Return the first matching item or null if no match is found
    return filteredData.firstOrNull()
}
private fun getCurrentHourRange(): IntRange {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val startHour = (currentHour / 3) * 3
    return startHour..(startHour + 3)
}
fun getShortDayNameFromDate(dateString: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    val shortDayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return shortDayFormat.format(date)
}
fun createWeatherHoursList(weatherDataList: WeatherResponse): List<WeatherHourItem> {
    val weatherItemList = mutableListOf<WeatherHourItem>()
    val hourPattern = Regex("\\d{4}-\\d{2}-\\d{2}\\s(\\d{2}):\\d{2}:\\d{2}")

    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentDateString = dateFormat.format(currentDate)
        .substring(0, 10) // Extract current date in "yyyy-MM-dd" format

    weatherDataList.list.filter { weatherData ->
        val dateString = weatherData.dt_txt.substring(0, 10)
        dateString == currentDateString
    }.forEach { weatherData ->
        val matchResult = hourPattern.find(weatherData.dt_txt)
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
    val day: String,
    val weatherIconResource: String,
    val description: String,
    val temp: Int,
)

fun convertKelvinToCelsius(kelvin: Double): Int {
    return (kelvin - 273.15).toInt()
}
