package com.example.weatherguide.model

import com.example.weatherguide.db.WeatherLocalDataSource
import com.example.weatherguide.network.WeatherRemoteDataSource

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



    override suspend fun getAllWeatherData(latitude: Double, longitude: Double): WeatherResponse {
        return weatherRemoteDataSource.getWeatherData(latitude,longitude)
    }
}