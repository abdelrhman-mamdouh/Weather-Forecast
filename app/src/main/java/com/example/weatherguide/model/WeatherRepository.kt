package com.example.weatherguide.model

import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
     fun  getCurrentWeatherData(latitude:Double, longitude:Double): Flow<CurrentWeatherResponse>
     fun getHourlyWeatherData(latitude:Double, longitude:Double): Flow<HourlyWeatherResponse>
     fun getDaysWeatherData(latitude:Double, longitude:Double): Flow<DaysWeatherResponse>
     fun getLocationSuggestions(query: String): Flow<List<Suggestions>>

     fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
     suspend fun delete(favoriteLocation: FavoriteLocation)
     suspend fun insert(favoriteLocation: FavoriteLocation)

}