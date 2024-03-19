package com.example.weatherguide.mapScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherguide.mapScreen.ApiLocationState
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(
    private val iRepository: WeatherRepository,
    private val sharedFlow: MutableSharedFlow<String>
) : ViewModel() {
    private val _locationSuggestions: MutableStateFlow<ApiLocationState?> =
        MutableStateFlow(null)
    val locationSuggestions: StateFlow<ApiLocationState?> = _locationSuggestions.asStateFlow()
    init {
        viewModelScope.launch(Dispatchers.Main) {
            sharedFlow.collect { text ->
                getLocationSuggestions(text)
            }
        }

    }
    fun addFavoriteLocation(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepository.insert(favoriteLocation)
        }
    }
    private fun getLocationSuggestions(query: String) {
        viewModelScope.launch {
            _locationSuggestions.value = ApiLocationState.Loading
            iRepository.getLocationSuggestions(query).catch { e ->
                _locationSuggestions.value = ApiLocationState.Failure(e)
            }.collect { locationList ->
                _locationSuggestions.value = ApiLocationState.Success(locationList)
            }
        }
    }

}