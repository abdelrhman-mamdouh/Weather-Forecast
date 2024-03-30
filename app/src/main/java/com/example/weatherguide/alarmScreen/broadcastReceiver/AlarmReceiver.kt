package com.example.weatherguide.alarmScreen.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.weatherguide.alarmScreen.service.AlertWindowService
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.network.ApiState
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import com.example.weatherguide.utills.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private val sharedFlow = MutableSharedFlow<SharedFlowObject>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", -1)

        var myObject = Util.getSharedFlowObject(context)

        val viewModelStore = ViewModelStore()
        val homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(context)
            ), sharedFlow
        )
        val homeViewModel =
            ViewModelProvider(viewModelStore, homeViewModelFactory).get(HomeViewModel::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            sharedFlow.emit(myObject)
        }
        getCurrentWeatherData(homeViewModel, context, alarmId)
    }
}

private fun getCurrentWeatherData(homeViewModel: HomeViewModel, context: Context, alarmId: Long) {
    GlobalScope.launch(Dispatchers.Main) {
        homeViewModel.weatherData.collect { state ->
            when (state) {
                is ApiState.Success -> {
                    val serviceIntent = Intent(context, AlertWindowService::class.java)
                    if (state.data.alerts != null) {
                        val description: String? = state.data.alerts
                            .mapNotNull { it.description }
                            .firstOrNull { it.isNotEmpty() }
                        serviceIntent.putExtra("message", description)
                        Log.i("TAG", "getCurrentWeatherData:${description} ")
                        serviceIntent.putExtra("alarmId", alarmId)
                        context.startService(serviceIntent)
                    } else {
                        serviceIntent.putExtra(
                            "message",
                            "Weather is fine.There are no alerts or warnings."
                        )
                        serviceIntent.putExtra("alarmId", alarmId)
                        context.startService(serviceIntent)
                    }
                }

                else -> {
                    Log.i("TAG", "not Success: ")
                }
            }
        }
    }
}

