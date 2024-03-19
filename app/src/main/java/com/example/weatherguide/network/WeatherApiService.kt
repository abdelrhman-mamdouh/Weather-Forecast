package com.example.weatherguide.network


import com.example.weatherguide.model.CurrentWeatherResponse
import com.example.weatherguide.model.DaysWeatherResponse
import com.example.weatherguide.model.HourlyWeatherResponse
import com.example.weatherguide.model.LocationSuggestions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>

    @GET("forecast/hourly")
    suspend fun getHourlyForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<HourlyWeatherResponse>

    @GET("forecast/daily")
    suspend fun getDaysForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<DaysWeatherResponse>
    @GET("geocode/search")
    suspend fun searchLocations(
        @Query("text") query: String,
        @Query("apiKey") apiKey: String
    ): Response<LocationSuggestions>

}