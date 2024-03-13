package com.example.weatherguide.network

import com.example.weatherguide.Constants
import com.example.weatherguide.model.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    val retrofitInstance = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()


    val service: WeatherApiService = retrofitInstance.create(WeatherApiService::class.java)
}

