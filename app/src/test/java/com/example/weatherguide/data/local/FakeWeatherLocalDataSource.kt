package com.example.weatherguide.data.local

import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherLocalDataSource(
    private val dummyFavoriteLocations: MutableList<FavoriteLocation> = mutableListOf(),
    private val dummyAlarmDates: MutableList<AlarmDate> = mutableListOf()
) : WeatherLocalDataSource {

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return flow { emit(dummyFavoriteLocations) }
    }

    override suspend fun delete(favoriteLocation: FavoriteLocation) {
        dummyFavoriteLocations.remove(favoriteLocation)
    }

    override suspend fun insert(favoriteLocation: FavoriteLocation) {
        dummyFavoriteLocations.add(favoriteLocation)
    }

    override fun getAlerts(): Flow<List<AlarmDate>> {
        return flow { emit(dummyAlarmDates) }
    }

    override suspend fun remove(alarmDate: AlarmDate) {
        dummyAlarmDates.remove(alarmDate)
    }

    override suspend fun removeById(alarmId: Long) {
        val alarmToRemove = dummyAlarmDates.find { it.id == alarmId }
        alarmToRemove?.let {
            dummyAlarmDates.remove(it)
        }
    }

    override suspend fun add(alarmDate: AlarmDate) {
        dummyAlarmDates.add(alarmDate)
    }
}