package com.example.weatherguide.network

import android.util.Log
import com.example.weatherguide.utills.Constants
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRemoteSourceDataImpl private constructor() : WeatherRemoteDataSource {

    private val weatherService: WeatherApiService by lazy {
        RetrofitHelper.WeatherService
    }
    private val locationNamesService: WeatherApiService by lazy {
        RetrofitHelper.locationNamesService
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

    override fun getWeatherData(latitude:Double,longitude:Double)=flow{
        try {
            val response = weatherService.getCurrentWeatherForecast(latitude, longitude, Constants.API_KEY)
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    emit(weatherResponse)
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

    override fun getLocationsSuggestions(query:String) = flow {
        try {
            val response =  locationNamesService.searchLocations(query, Constants.API_KEY_LOCATIONS)
            if (response.isSuccessful) {
                val locationsSuggestions = response.body()?.features
                if (locationsSuggestions != null) {
                    emit(locationsSuggestions)
                } else {
                    val errorMessage = "location data is null"
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