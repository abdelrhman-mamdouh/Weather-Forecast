package com.example.weatherguide.favoriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow


class FavoritesViewModelFactory(private val repo: WeatherRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            FavoritesViewModel(repo) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}