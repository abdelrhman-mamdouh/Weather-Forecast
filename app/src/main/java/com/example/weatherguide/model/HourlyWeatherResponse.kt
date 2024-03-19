package com.example.weatherguide.model

import com.google.gson.annotations.SerializedName


data class HourlyWeatherResponse(
    val list: List<WeatherData>,
    val city: City
)


data class WeatherData(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherDes>,
    val clouds: CloudsData,
    val wind: WindData,
    val visibility: Int,
    val pop: Double,
    val rain: Rain,
    val sys: SysData,
    @SerializedName("dt_txt") val dtTxt: String
)

data class MainData(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("grnd_level") val grndLevel: Int,
    val humidity: Int,
    @SerializedName("temp_kf") val tempKf: Double
)


data class WeatherDes(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)


data class CloudsData(
    val all: Int
)


data class WindData(
    val speed: Double,
    val deg: Int,
    val gust: Double
)


data class SysData(
    val pod: String
)


data class Rain(
    @SerializedName("1h") val oneHour: Double
)

data class City(
    val id: Int,
    val name: String,
    val coord: CoordData,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class CoordData(
    val lat: Double,
    val lon: Double
)
