package com.example.weatherguide.HomeScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherguide.model.WeatherRepository
import com.example.weatherguide.model.WeatherResponse
import com.example.weatherguide.network.RetrofitHelper
import kotlinx.coroutines.launch

class HomeViewModel(private val iRepository: WeatherRepository) : ViewModel() {

    private var  _weatherData: MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()

    val weatherData: LiveData<WeatherResponse> = _weatherData

    fun getWeatherData() {
        viewModelScope.launch {
            try {
                _weatherData.postValue(iRepository.getAllWeatherData())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}