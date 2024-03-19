package com.example.weatherguide.db

import android.content.Context
import com.example.productsmvvm.db.WeatherDatabase
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context):WeatherLocalDataSource {


    private val dao: FavoriteLocationDAO by lazy {
        val db = WeatherDatabase.getInstance(context)
        db.weatherDAO()
    }

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavoriteLocations()
    }

    override suspend fun delete(favoriteLocation: FavoriteLocation) {
       dao.delete(favoriteLocation)
    }

    override suspend fun insert(favoriteLocation: FavoriteLocation) {
        dao.insert(favoriteLocation)
    }
}