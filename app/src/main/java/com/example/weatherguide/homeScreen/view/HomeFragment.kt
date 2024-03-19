package com.example.weatherguide.homeScreen.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.Constants
import com.example.weatherguide.MyLocationManager
import com.example.weatherguide.R
import com.example.weatherguide.db.WeatherLocalDataSourceImpl

import com.example.weatherguide.homeScreen.ApiState
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.createWeatherAllDaysList
import com.example.weatherguide.model.createWeatherHoursList
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), LocationListener {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var weatherIconImageView: ImageView
    private lateinit var hoursRecyclerView: RecyclerView
    private lateinit var daysRecyclerView: RecyclerView
    private lateinit var hoursLinearManger: LinearLayoutManager
    private lateinit var daysLinearManager: LinearLayoutManager
    private lateinit var pressureTextView: TextView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var cloudTextView: TextView
    private lateinit var seaLevelTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationManager: MyLocationManager
    private val sharedFlow = MutableSharedFlow<Pair<Double, Double>>()
    private lateinit var loader: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization(view)

        locationManager = MyLocationManager(context = requireContext())
        loader = view.findViewById(R.id.loader)

        hoursRecyclerView = view.findViewById(R.id.hoursRecyclerView)
        hoursRecyclerView.setHasFixedSize(true)
        hoursLinearManger = LinearLayoutManager(requireContext())
        hoursLinearManger.orientation = RecyclerView.HORIZONTAL
        hoursRecyclerView.layoutManager = hoursLinearManger
        daysRecyclerView = view.findViewById(R.id.daysRecyclerView)
        daysRecyclerView.setHasFixedSize(true)
        daysLinearManager = LinearLayoutManager(requireContext())
        daysLinearManager.orientation = RecyclerView.VERTICAL
        daysRecyclerView.layoutManager = daysLinearManager
        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            ), sharedFlow
        )
        homeViewModel =
            ViewModelProvider(requireActivity(), homeViewModelFactory)[HomeViewModel::class.java]

        if (locationManager.checkPermissions()) {
            if (locationManager.isLocationEnabled()) {
                onLocationSourceSelected()

            } else {
                locationManager.enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.REQUEST_LOCATION_CODE
            )
        }


        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)
                        locationTextView.text = "${state.data.name}"
                        pressureTextView.text = "${state.data.main.pressure} hPa"
                        seaLevelTextView.text = "${state.data.main.seaLevel} hPa"
                        humidityTextView.text = "${state.data.main.humidity}%"
                        windTextView.text = "${state.data.wind.speed} m/s"
                        cloudTextView.text = "${state.data.clouds.all} %"
                        visibilityTextView.text = "${state.data.visibility} m"
                        weatherDescriptionTextView.text = state.data.weather[0].description
                        temperatureTextView.text =
                            "${convertKelvinToCelsius(state.data.main.temp)}°C"
                        dateTextView.text = getCurrentDateFormatted()
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherHourlyData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)
                        Log.i("TAG", "weatherHourlyData: ${state.data.list}")
                           val weatherHoursList = createWeatherHoursList(state.data.list)
                            val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
                            hoursRecyclerView.adapter = hoursAdapter
                           hoursAdapter.notifyDataSetChanged()
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherDaysData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)
                        Log.i("TAG", "weatherDaysData: ${state.data.list}")
                        val weatherDaysList = createWeatherAllDaysList(state.data.list)
                             val daysAdapter = DaysWeatherAdapter(requireContext(), weatherDaysList)
                              daysRecyclerView.adapter = daysAdapter
                              daysAdapter.notifyDataSetChanged()
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }


        //       val weatherDaysList = createWeatherAllDaysList(weatherResponse)
        //       val daysAdapter = DaysWeatherAdapter(requireContext(), weatherDaysList)
        //       daysRecyclerView.adapter = daysAdapter
        //       daysAdapter.notifyDataSetChanged()
        //       val currentWeatherData = getCurrentDayWeatherData(weatherResponse.list)

    }


    private fun initialization(view: View) {
        weatherIconImageView = view.findViewById<ImageView>(R.id.weatherIconImageView)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windTextView = view.findViewById(R.id.windTextView)
        cloudTextView = view.findViewById(R.id.cloudTextView)
        seaLevelTextView = view.findViewById(R.id.seaLevelTextView)
        visibilityTextView = view.findViewById(R.id.visibilityTextView)
        locationTextView = view.findViewById(R.id.locationTextView)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        temperatureTextView = view.findViewById(R.id.temperatureTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
    }



    private fun convertKelvinToCelsius(kelvin: Double): Int {
        return (kelvin - 273.15).toInt()
    }

    private fun getCurrentDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_LOCATION_CODE) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.startLocationUpdates(this)
            }
        }
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.Main) {
            val pair = Pair(latitude, longitude)
            sharedFlow.emit(pair)
        }
    }

    private fun onLocationSourceSelected() {
        sharedPreferences =
            requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("selected_option", null)
        when (selectedOption) {
            "Map" -> {
                val sharedPreferences =
                    requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                val latitude = sharedPreferences.getFloat("latitudeFromMap", 0.0f).toDouble()
                val longitude = sharedPreferences.getFloat("longitudeFromMap", 0.0f).toDouble()
                lifecycleScope.launch(Dispatchers.Main) {
                    sharedFlow.emit(latitude to longitude)
                }

            }
            else -> {
                val latitude = sharedPreferences.getFloat("latitudeFromSearch", 0.0f).toDouble()
                val longitude = sharedPreferences.getFloat("longitudeFromSearch", 0.0f).toDouble()
                if (latitude != null) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        sharedFlow.emit(latitude to longitude)
                    }
                } else
                    locationManager.startLocationUpdates(this)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
