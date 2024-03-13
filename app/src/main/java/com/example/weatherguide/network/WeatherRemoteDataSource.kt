package com.example.weatherguide.network

import com.example.weatherguide.model.WeatherResponse

interface WeatherRemoteDataSource {
    suspend fun getWeatherData():WeatherResponse
}
