package com.example.weatherguide.homeScreen.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherguide.utills.Constants
import com.example.weatherguide.MyLocationManager
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FragmentHomeBinding
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.network.ApiState
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.createCurrentDayWeatherHoursList
import com.example.weatherguide.model.createWeatherAllDaysList
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), LocationListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var hoursLinearManger: LinearLayoutManager
    private lateinit var daysLinearManager: LinearLayoutManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationManager: MyLocationManager
    private val sharedFlow = MutableSharedFlow<Pair<Double, Double>>()
    private lateinit var loader: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        locationManager = MyLocationManager(context = requireContext())
        loader = view.findViewById(R.id.loader)

        binding.hoursRecyclerView.setHasFixedSize(true)
        hoursLinearManger = LinearLayoutManager(requireContext())
        hoursLinearManger.orientation = RecyclerView.HORIZONTAL
        binding.hoursRecyclerView.layoutManager = hoursLinearManger
        binding.daysRecyclerView.setHasFixedSize(true)
        daysLinearManager = LinearLayoutManager(requireContext())
        daysLinearManager.orientation = RecyclerView.VERTICAL
        binding.daysRecyclerView.layoutManager = daysLinearManager
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
        getCurrentWeatherData()
        swipeRefreshLayout.setOnRefreshListener {
            reloadFragment()
        }
    }


    private fun getCurrentWeatherData() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success -> {
                        showLoading(false)

                        val weatherHoursList = createCurrentDayWeatherHoursList(state.data.hourly)
                        val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
                        binding.hoursRecyclerView.adapter = hoursAdapter
                        hoursAdapter.notifyDataSetChanged()

                        val weatherDaysList = createWeatherAllDaysList(state.data.daily)
                        val daysAdapter = DaysWeatherAdapter(requireContext(), weatherDaysList)
                        binding.daysRecyclerView.adapter = daysAdapter
                        daysAdapter.notifyDataSetChanged()
                        binding.locationTextView.text = "${state.data.timezone}"
                        binding.pressureTextView.text = "${state.data.current.pressure} hPa"
                        binding.seaLevelTextView.text = "${state.data.current.uvi} hPa"
                        binding.humidityTextView.text = "${state.data.current.humidity}%"
                        binding.windTextView.text = "${state.data.current.windSpeed} m/s"
                        binding.cloudTextView.text = "${state.data.current.clouds} %"
                        binding.visibilityTextView.text = "${state.data.current.visibility} m"
                        binding.weatherDescriptionTextView.text =
                            state.data.current.weather[0].description
                        binding.temperatureTextView.text =
                            "${convertKelvinToCelsius(state.data.current.temp)}°C"
                        binding.dateTextView.text = getCurrentDateFormatted()
                    }

                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
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
            requireActivity().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("selected_option", null)
        when (selectedOption) {
            "Map" -> {
                val sharedPreferences =
                    requireActivity().getSharedPreferences("current-location", Context.MODE_PRIVATE)
                val latitude = sharedPreferences.getFloat("latitudeFromMap", 0.0f).toDouble()
                val longitude = sharedPreferences.getFloat("longitudeFromMap", 0.0f).toDouble()
                lifecycleScope.launch(Dispatchers.Main) {
                    sharedFlow.emit(latitude to longitude)
                }

            }

            else -> {
                locationManager.startLocationUpdates(this)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadFragment() {
        val intent = activity?.intent
        activity?.finish()
        startActivity(intent!!)
        swipeRefreshLayout.isRefreshing = false
    }
}

