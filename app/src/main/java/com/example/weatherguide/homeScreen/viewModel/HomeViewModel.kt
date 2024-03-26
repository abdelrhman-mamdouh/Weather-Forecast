package com.example.weatherguide.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.homeScreen.ApiState.*
import com.example.weatherguide.model.WeatherRepository
import com.example.weatherguide.model.WeatherResponse
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

    private val _weatherData: MutableStateFlow<ApiState<WeatherResponse>> =
        MutableStateFlow(Loading)
    val weatherData: StateFlow<ApiState<WeatherResponse>> = _weatherData.asStateFlow()


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
            iRepository.getWeatherData(latitude,longitude).catch { e->
                _weatherData.value= Failure(e)
            }.collect{
                _weatherData.value= Success(it)
            }

        }
    }
}