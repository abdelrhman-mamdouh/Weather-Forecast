package com.example.weatherguide.homeScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.homeScreen.ApiState.*
import com.example.weatherguide.model.CurrentWeatherResponse
import com.example.weatherguide.model.DaysWeatherResponse
import com.example.weatherguide.model.HourlyWeatherResponse
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val iRepository: WeatherRepository,
    private val sharedFlow: MutableSharedFlow<Pair<Double, Double>>
) : ViewModel() {

    private val _weatherData: MutableStateFlow<ApiState<CurrentWeatherResponse>> =
        MutableStateFlow(Loading)
    val weatherData: StateFlow<ApiState<CurrentWeatherResponse>> = _weatherData.asStateFlow()

    private val _weatherHourlyData: MutableStateFlow<ApiState<HourlyWeatherResponse>> =
        MutableStateFlow(Loading)
    val weatherHourlyData: StateFlow<ApiState<HourlyWeatherResponse>> =
        _weatherHourlyData.asStateFlow()

    private val _weatherDaysData: MutableStateFlow<ApiState<DaysWeatherResponse>> =
        MutableStateFlow(Loading)
    val weatherDaysData: StateFlow<ApiState<DaysWeatherResponse>> =
        _weatherDaysData.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            sharedFlow.collect { (latitude, longitude) ->
                getData(latitude, longitude)
            }
        }
    }


    private fun getData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherData.value = Loading
            iRepository.getCurrentWeatherData(latitude,longitude).catch {e->
                _weatherData.value= Failure(e)
            }.collect{
                _weatherData.value= Success(it)
            }

            _weatherHourlyData.value = Loading
            iRepository.getHourlyWeatherData(latitude,longitude).catch {e->
                _weatherHourlyData.value= Failure(e)
            }.collect{
                _weatherHourlyData.value= Success(it)
            }

            _weatherDaysData.value = Loading
            iRepository.getDaysWeatherData(latitude,longitude).catch {e->
                _weatherDaysData.value= Failure(e)
            }.collect{
                _weatherDaysData.value= Success(it)
            }

        }
    }
}