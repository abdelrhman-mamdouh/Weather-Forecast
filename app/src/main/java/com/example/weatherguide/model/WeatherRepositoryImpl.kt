package com.example.weatherguide.model

import com.example.weatherguide.data.local.WeatherLocalDataSource
import com.example.weatherguide.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {

    companion object {
        var repository: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource
        ): WeatherRepository {
            if (repository == null) {
                repository = WeatherRepositoryImpl(weatherRemoteDataSource, weatherLocalDataSource)
            }
            return repository!!
        }
    }

    override fun getWeatherData(sharedFlowObject: SharedFlowObject): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getWeatherData(sharedFlowObject)
    }


    override fun getLocationSuggestions(query: String): Flow<List<Suggestions>> {
        return weatherRemoteDataSource.getLocationsSuggestions(query)
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return weatherLocalDataSource.getFavoriteLocations()
    }

    override suspend fun delete(favoriteLocation: FavoriteLocation) {
        weatherLocalDataSource.delete(favoriteLocation)
    }

    override suspend fun insert(favoriteLocation: FavoriteLocation) {
        weatherLocalDataSource.insert(favoriteLocation)
    }

    override fun getAllAlarms(): Flow<List<AlarmDate>> {
        return weatherLocalDataSource.getAlerts()
    }

    override suspend fun remove(alarmDate: AlarmDate) {
        weatherLocalDataSource.remove(alarmDate)
    }

    override suspend fun removeById(alarmId: Long) {
        weatherLocalDataSource.removeById(alarmId)
    }

    override suspend fun add(alarmDate: AlarmDate) {
        weatherLocalDataSource.add(alarmDate)
    }
}