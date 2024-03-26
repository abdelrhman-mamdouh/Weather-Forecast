package com.example.weatherguide.alarmScreen.broadcastReceiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.weatherguide.alarmScreen.service.AlertWindowService
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private val sharedFlow = MutableSharedFlow<Pair<Double, Double>>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val spCurrentLocation =
            context.getSharedPreferences("current-location", Context.MODE_PRIVATE)
        val latitude = spCurrentLocation.getFloat("latitudeFromMap", 0.0f).toDouble()
        val longitude = spCurrentLocation.getFloat("longitudeFromMap", 0.0f).toDouble()

        val viewModelStore = ViewModelStore()
        val homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(context)
            ), sharedFlow
        )
        val homeViewModel = ViewModelProvider(viewModelStore, homeViewModelFactory).get(HomeViewModel::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            sharedFlow.emit(latitude to longitude)
        }
        getCurrentWeatherData(homeViewModel, context)
    }
}

private fun getCurrentWeatherData(homeViewModel: HomeViewModel, context: Context) {
    GlobalScope.launch(Dispatchers.Main) {
        homeViewModel.weatherData.collect { state ->
            when (state) {
                is ApiState.Success -> {
                    val serviceIntent = Intent(context, AlertWindowService::class.java)
                    if (state.data.alerts != null) {
                        serviceIntent.putExtra("message", state.data.alerts[0].description)
                        context.startService(serviceIntent)
                    } else {
                        serviceIntent.putExtra(
                            "message",
                            "Weather is fine.There are no alerts or warnings."
                        )
                        context.startService(serviceIntent)
                    }
                }

                else -> {
                    Log.i("TAG", "getCurrentWeatherData: ")
                }
            }
        }
    }
}

