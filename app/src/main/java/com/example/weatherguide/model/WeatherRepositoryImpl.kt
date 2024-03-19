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



    override fun getCurrentWeatherData(latitude: Double, longitude: Double): Flow<CurrentWeatherResponse> {
        return weatherRemoteDataSource.getWeatherData(latitude,longitude)
    }

    override fun getHourlyWeatherData(latitude: Double, longitude: Double): Flow<HourlyWeatherResponse> {
        return weatherRemoteDataSource.getHourlyForecast(latitude,longitude)
    }

    override fun getDaysWeatherData(latitude: Double, longitude: Double): Flow<DaysWeatherResponse>{
        return weatherRemoteDataSource.getDaysForecast(latitude,longitude)
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
}