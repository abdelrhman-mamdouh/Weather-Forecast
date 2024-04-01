package com.example.weatherguide.data.remote


import com.example.weatherguide.model.LocationSuggestions
import com.example.weatherguide.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("onecall")
    suspend fun getCurrentWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("lang") language: String = "en",
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>


    @GET("geocode/search")
    suspend fun searchLocations(
        @Query("text") query: String,
        @Query("apiKey") apiKey: String
    ): Response<LocationSuggestions>

}