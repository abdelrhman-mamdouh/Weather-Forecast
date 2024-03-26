package com.example.weatherguide.favoriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(private val iRepository: WeatherRepository) : ViewModel() {

    private val _favoriteLocations: MutableStateFlow<ApiState<List<FavoriteLocation>>> =
        MutableStateFlow(ApiState.Loading)
    val favoriteLocations: StateFlow<ApiState<List<FavoriteLocation>>> = _favoriteLocations.asStateFlow()

    init {
        getFavoriteLocations()
    }
    fun removeLocation(product: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepository.delete(product)

        }
    }
    private fun getFavoriteLocations() {
        viewModelScope.launch {
            _favoriteLocations.value = ApiState.Loading
            iRepository.getAllFavoriteLocations().catch { e ->
                _favoriteLocations.value = ApiState.Failure(e)
            }.collect {
                _favoriteLocations.value = ApiState.Success(it)
            }
        }
    }

}
