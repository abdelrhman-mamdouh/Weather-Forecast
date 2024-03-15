package com.example.weatherguide.model

interface WeatherRepository {
    suspend fun getAllWeatherData(latitude:Double,longitude:Double): WeatherResponse
}