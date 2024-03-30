package com.example.weatherguide.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class HomeViewModelFactory(private val repo: WeatherRepository,private val sharedFlow: MutableSharedFlow<SharedFlowObject>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repo,sharedFlow) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}