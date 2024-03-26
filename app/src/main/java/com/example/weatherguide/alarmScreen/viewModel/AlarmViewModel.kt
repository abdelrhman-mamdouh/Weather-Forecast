package com.example.weatherguide.alarmScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel(private val iRepository: WeatherRepository) : ViewModel(){
    private val _alarm: MutableStateFlow<ApiState<List<AlarmDate>>> =
        MutableStateFlow(ApiState.Loading)
    val alarm: StateFlow<ApiState<List<AlarmDate>>> = _alarm.asStateFlow()

    init {
        getAllAlarms()
    }
    fun removeAlarm(alarm: AlarmDate) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepository.remove(alarm)
        }
    }
    fun addAlarm(alarm: AlarmDate) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepository.add(alarm)
        }
    }
    private fun getAllAlarms() {
        viewModelScope.launch {
            _alarm.value = ApiState.Loading
            iRepository.getAllAlarms().catch { e ->
                _alarm.value = ApiState.Failure(e)
            }.collect {
                _alarm.value = ApiState.Success(it)
            }
        }
    }

}

