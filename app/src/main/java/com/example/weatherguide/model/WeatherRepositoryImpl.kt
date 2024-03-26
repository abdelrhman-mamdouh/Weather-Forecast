package com.example.weatherguide.model

import com.example.weatherguide.db.WeatherLocalDataSource
import com.example.weatherguide.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource
) : WeatherRepository {

    companion object {
        private var repository: WeatherRepository? = null

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
    override fun getWeatherData(latitude: Double, longitude: Double): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getWeatherData(latitude,longitude)
    }


    override  fun getLocationSuggestions(query: String): Flow<List<Suggestions>> {
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

    override suspend fun add(alarmDate: AlarmDate) {
        weatherLocalDataSource.add(alarmDate)
    }
}