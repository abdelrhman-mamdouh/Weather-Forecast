package com.example.weatherguide.model

import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
    fun getWeatherData(sharedFlowObject: SharedFlowObject): Flow<WeatherResponse>
    fun getLocationSuggestions(query: String): Flow<List<Suggestions>>

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun delete(favoriteLocation: FavoriteLocation)
    suspend fun insert(favoriteLocation: FavoriteLocation)

    fun getAllAlarms(): Flow<List<AlarmDate>>
    suspend fun remove(alarmDate: AlarmDate)
    suspend fun removeById(alarmId: Long)
    suspend fun add(alarmDate: AlarmDate)

}