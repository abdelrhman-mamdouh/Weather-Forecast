package com.example.weatherguide.mapScreen

import com.example.weatherguide.model.Suggestions

sealed class ApiLocationState {
    data class Success(val list: List<Suggestions>) : ApiLocationState()
    data class Failure(val msg: Throwable) : ApiLocationState()
    object Loading : ApiLocationState()
}