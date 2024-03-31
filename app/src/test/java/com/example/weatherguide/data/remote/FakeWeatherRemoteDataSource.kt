package com.example.weatherguide.data.remote

import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.Suggestions
import com.example.weatherguide.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRemoteDataSource(
    private val dummyWeatherResponse: WeatherResponse,
    private val dummySuggestions: MutableList<Suggestions> = mutableListOf()
) : WeatherRemoteDataSource {

    override fun getWeatherData(sharedFlowObject: SharedFlowObject): Flow<WeatherResponse> {
        return flow { emit(dummyWeatherResponse) }
    }

    override fun getLocationsSuggestions(query: String): Flow<List<Suggestions>> {
        return flow { emit(dummySuggestions) }
    }
}