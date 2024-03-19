package com.example.weatherguide.db

import android.content.Context
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun delete(favoriteLocation: FavoriteLocation)
    suspend fun insert(favoriteLocation: FavoriteLocation)
}
