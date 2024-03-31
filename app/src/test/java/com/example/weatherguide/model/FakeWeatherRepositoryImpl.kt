package com.example.weatherguide.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepositoryImpl : WeatherRepository {

   var dummyWeatherResponse = WeatherResponse(
       37.7749, -122.4194,
       "America/Los_Angeles",
       -25200,
       CurrentWeather(
           1617088740,
           1617065100,
           1617110420,
           15.75,
           13.91,
           1013,
           82,
           12.93,
           5.79,
           90,
           10000,
           5.14,
           360,
           7.72,
           listOf(
               WeatherDescription(
                   804,
                   "Clouds",
                   "overcast clouds",
                   "04d"
               )
           )
       ),
       listOf(
           HourlyWeather(
               1617090000,
               15.71,
               13.81,
               1013,
               82,
               12.89,
               5.82,
               90,
               10000,
               5.03,
               360,
               7.65,
               listOf(
                   WeatherDescription(
                       804,
                       "Clouds",
                       "overcast clouds",
                       "04d"
                   )
               ),
               0.12
           )
       ),
       daily = listOf(
           DailyWeather(
               1617084000,
               1617065100,
               1617110420,
               1617138660,
               1617082860,
               0.61,
               "Overcast clouds",
               Temperature(
                   16.22,
                   14.6,
                   16.22,
                   15.72,
                   16.22,
                   14.6
               ),
               FeelsLike(
                   14.3,
                   13.95,
                   14.3,
                   13.95
               ),
               1013,
               82,
               13.06,
               5.15,
               360,
               7.64,
               listOf(
                   WeatherDescription(
                       804,
                       "Clouds",
                       "overcast clouds",
                       "04d"
                   )
               ),
               90,
               0.7,
               6.17,
               6.17
           )
       ),
       null
   )

    private val dummyLocationSuggestions = listOf(
        Suggestions(
            properties = Properties(
                formatted = "San Francisco, CA, USA",
                lon = -122.4194,
                lat = 37.7749
            )
        ),
        Suggestions(
            properties = Properties(
                formatted = "New York, NY, USA",
                lon = -74.0059,
                lat = 40.7128
            )
        )
    )

    private var dummyFavoriteLocations: MutableList<FavoriteLocation> = mutableListOf()


    private val dummyAlarms = listOf(
        AlarmDate(dateTime = "2024-04-01 08:00"),
        AlarmDate(dateTime = "2024-04-02 09:30"),
        AlarmDate(dateTime = "2024-04-03 07:45")
    )



    override fun getWeatherData(sharedFlowObject: SharedFlowObject): Flow<WeatherResponse> {
        return flow {
                emit(dummyWeatherResponse)
        }
    }

    override fun getLocationSuggestions(query: String): Flow<List<Suggestions>> {
        return flow {
            emit(dummyLocationSuggestions)
        }
    }
    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return flow {
            emit(dummyFavoriteLocations)
        }
    }

    override suspend fun delete(favoriteLocation: FavoriteLocation) {
        dummyFavoriteLocations.remove(favoriteLocation)
    }

    override suspend fun insert(favoriteLocation: FavoriteLocation) {
        dummyFavoriteLocations.add(favoriteLocation)
    }

    override fun getAllAlarms(): Flow<List<AlarmDate>> {
        return flow {
            emit(dummyAlarms)
        }
    }

    override suspend fun remove(alarmDate: AlarmDate) {
        TODO()
    }

    override suspend fun removeById(alarmId: Long) {
        TODO()
    }

    override suspend fun add(alarmDate: AlarmDate) {
        TODO()
    }
}