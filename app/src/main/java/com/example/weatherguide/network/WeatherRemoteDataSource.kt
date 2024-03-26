package com.example.weatherguide.network


import com.example.weatherguide.model.Suggestions

import com.example.weatherguide.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getWeatherData(latitude:Double,longitude:Double):Flow<WeatherResponse>
    fun getLocationsSuggestions(query: String): Flow<List<Suggestions>>
}
