package com.example.weatherguide.network

import android.util.Log
import com.example.weatherguide.Constants
import com.example.weatherguide.model.WeatherResponse
import retrofit2.HttpException
import java.io.IOException

class WeatherRemoteSourceDataImpl private constructor() : WeatherRemoteDataSource {

    private val latitude = 54.525963
    private val longitude = 15.255119
    private val service: WeatherApiService by lazy {
        RetrofitHelper.service
    }

    companion object {
        private const val TAG = "WeatherDataActivity"
        private var instance: WeatherRemoteDataSource? = null

        fun getInstance(): WeatherRemoteDataSource {
            if (instance == null) {
                instance = WeatherRemoteSourceDataImpl()
            }
            return instance!!
        }
    }

    override suspend fun getWeatherData(): WeatherResponse {
        try {
            val response = service.getWeatherForecast(latitude, longitude, Constants.API_KEY)
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    return weatherResponse
                } else {
                    val errorMessage = "Weather data is null"
                    Log.e(TAG, errorMessage)
                    throw NullPointerException(errorMessage)
                }
            } else {
                val errorMessage = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                throw HttpException(response)
            }
        } catch (e: IOException) {
            val errorMessage = "Network error: ${e.message}"
            Log.e(TAG, errorMessage, e)
            throw IOException(errorMessage, e)
        } catch (e: Exception) {
            val errorMessage = "Unexpected error: ${e.message}"
            Log.e(TAG, errorMessage, e)
            throw e
        }
    }
}