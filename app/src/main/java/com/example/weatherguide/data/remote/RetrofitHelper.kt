package com.example.weatherguide.data.remote

import com.example.weatherguide.utills.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    private val retrofitCurrentWeather: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.WEATHER_URL)
        .build()


    val WeatherService: WeatherApiService = retrofitCurrentWeather.create(WeatherApiService::class.java)


    private val retrofitLocationNames = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL_Locations)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val locationNamesService: WeatherApiService = retrofitLocationNames.create(WeatherApiService::class.java)




}

