package com.example.weatherguide.model

interface WeatherRepository {
    suspend fun getAllWeatherData(): WeatherResponse
}