package com.example.weatherguide.network

import com.example.weatherguide.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    private val retrofitCurrentWeather: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.CURRENT_WEATHER_URL)
        .build()


    val currentWeatherService: WeatherApiService = retrofitCurrentWeather.create(WeatherApiService::class.java)


    private val retrofitLocationNames = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL_Locations)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val locationNamesService: WeatherApiService = retrofitLocationNames.create(WeatherApiService::class.java)



    private val retrofitHourlyForecast  = Retrofit.Builder()
        .baseUrl(Constants.HOURLY_FORECAST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val hourlyForecastService: WeatherApiService = retrofitHourlyForecast.create(WeatherApiService::class.java)


    private val retrofitDaysForecast  = Retrofit.Builder()
        .baseUrl(Constants.DAYS_FORECAST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val daysForecastService: WeatherApiService = retrofitDaysForecast.create(WeatherApiService::class.java)

}

