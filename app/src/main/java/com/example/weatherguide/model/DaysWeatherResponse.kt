package com.example.weatherguide.model

import com.google.gson.annotations.SerializedName

data class DaysWeatherResponse(
    val city: DaysCity,
    val list: List<DaysWeatherData>
)

data class DaysCity(
    val id: Int,
    val name: String,
    val coord: DaysCoord,
    val country: String,
    val population: Int,
    val timezone: Int
)

data class DaysCoord(
    val lon: Double,
    val lat: Double
)

data class DaysWeatherData(
    val dt: Long,
    val temp: Temperature,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val weather: List<DaysWeather>,
    val speed: Double,
    val deg: Int,
    val gust: Double,
    val clouds: Int,
    val pop: Double,
    val rain: Double?
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

data class DaysWeather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)