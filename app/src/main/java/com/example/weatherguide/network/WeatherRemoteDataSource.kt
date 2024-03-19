package com.example.weatherguide.network

import com.example.weatherguide.model.CurrentWeatherResponse
import com.example.weatherguide.model.DaysWeatherData
import com.example.weatherguide.model.DaysWeatherResponse
import com.example.weatherguide.model.HourlyWeatherResponse
import com.example.weatherguide.model.Suggestions
import com.example.weatherguide.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getWeatherData(latitude:Double,longitude:Double):Flow<CurrentWeatherResponse>
    fun getHourlyForecast(latitude:Double,longitude:Double):Flow<HourlyWeatherResponse>
    fun getDaysForecast(latitude:Double,longitude:Double):Flow<DaysWeatherResponse>
    fun getLocationsSuggestions(query: String): Flow<List<Suggestions>>
}
