package com.example.weatherguide.data.local

import android.content.Context
import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun delete(favoriteLocation: FavoriteLocation)
    suspend fun insert(favoriteLocation: FavoriteLocation)


    fun getAlerts(): Flow<List<AlarmDate>>
    suspend fun remove(alarmDate: AlarmDate)
    suspend fun removeById(alarmId: Long)
    suspend fun add(alarmDate: AlarmDate)

}
