package com.example.weatherguide.homeScreen

sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Failure(val error: Throwable) : ApiState<Nothing>()
}