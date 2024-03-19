package com.example.weatherguide.mapScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class MapViewModelFactory(private val repo: WeatherRepository, private val sharedFlow:MutableSharedFlow<String>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(repo,sharedFlow) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}