package com.example.weatherguide.network

import com.example.weatherguide.model.WeatherResponse

interface WeatherRemoteDataSource {
    suspend fun getWeatherData(latitude:Double,longitude:Double):WeatherResponse
}
