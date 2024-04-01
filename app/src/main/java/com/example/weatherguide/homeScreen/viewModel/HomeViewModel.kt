package com.example.weatherguide.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherguide.data.remote.ApiState
import com.example.weatherguide.data.remote.ApiState.Failure
import com.example.weatherguide.data.remote.ApiState.Loading
import com.example.weatherguide.data.remote.ApiState.Success
import com.example.weatherguide.model.SharedFlowObject
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
    private val sharedFlow: MutableSharedFlow<SharedFlowObject>
) : ViewModel() {

    private val _weatherData: MutableStateFlow<ApiState<WeatherResponse>> =
        MutableStateFlow(Loading)
    val weatherData: StateFlow<ApiState<WeatherResponse>> = _weatherData.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.Main) {
            sharedFlow.collect {
                getWeatherData(it)
            }
        }
    }

    private fun getWeatherData(sharedFlowObject: SharedFlowObject) {
        viewModelScope.launch {
            _weatherData.value = Loading

            iRepository.getWeatherData(sharedFlowObject).catch { e ->
                _weatherData.value = Failure(e)
            }.collect {
                _weatherData.value = Success(it)
            }

        }
    }
}