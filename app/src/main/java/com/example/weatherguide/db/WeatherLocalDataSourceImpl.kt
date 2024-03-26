package com.example.weatherguide.db

import android.content.Context
import com.example.productsmvvm.db.WeatherDatabase
import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context):WeatherLocalDataSource {
    private val daoFav: FavoriteLocationDAO by lazy {
        val db = WeatherDatabase.getInstance(context)
        db.weatherDAO()
    }

    private val daoAlert: AlarmDAO by lazy {
        val db = WeatherDatabase.getInstance(context)
        db.alarmDAO()
    }


    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return daoFav.getAllFavoriteLocations()
    }
    override suspend fun delete(favoriteLocation: FavoriteLocation) {
        daoFav.delete(favoriteLocation)
    }
    override suspend fun insert(favoriteLocation: FavoriteLocation) {
        daoFav.insert(favoriteLocation)
    }

    override fun getAlerts(): Flow<List<AlarmDate>> {
       return daoAlert.getAllAlarms()
    }

    override suspend fun remove(alarmDate: AlarmDate) {
        daoAlert.remove(alarmDate)
    }

    override suspend fun add(alarmDate: AlarmDate) {
        daoAlert.add(alarmDate)
    }
}