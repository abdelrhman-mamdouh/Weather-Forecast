package com.example.weatherguide.homeScreen.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherguide.MainActivity
import com.example.weatherguide.MainActivityListener
import com.example.weatherguide.MyLocationManager
import com.example.weatherguide.R
import com.example.weatherguide.data.local.WeatherLocalDataSourceImpl
import com.example.weatherguide.data.remote.ApiState
import com.example.weatherguide.data.remote.WeatherRemoteSourceDataImpl
import com.example.weatherguide.databinding.FragmentHomeBinding
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.WeatherResponse

import com.example.weatherguide.utills.Constants
import com.example.weatherguide.utills.Util
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
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
    private val sharedFlow = MutableSharedFlow<SharedFlowObject>()
    private lateinit var mListener: MainActivityListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MainActivityListener")
        }
    }
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

        binding.myLayout.visibility = View.GONE
        if (binding.myLayout.visibility == View.GONE) {
            binding.swipeDown.visibility = View.VISIBLE
        }
        locationManager = MyLocationManager(context = requireContext())

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
                getCurrentWeatherData()
            } else {
                locationManager.enableLocationServices()
            }
        } else {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.allow_location_permissions))
                .setPositiveButton(getString(R.string.allow)) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        Constants.REQUEST_LOCATION_CODE
                    )
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            reloadFragment()
        }
    }
    private fun getCurrentWeatherData() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weatherData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                        binding.swipeDown.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                        cacheWeatherData(state.data)
                        showWeatherData(state.data)
                    }
                    else -> {
                        val cachedWeatherData = getCachedWeatherData()
                        if (cachedWeatherData != null) {       // Show UI with cached weather data
                            showWeatherData(cachedWeatherData)
                        } else {
                            showLoading(false)
                            Snackbar.make(
                                binding.root,
                                "No network connection and no cached data available",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
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

    override fun onLocationChanged(sharedFlowObject: SharedFlowObject) {
        lifecycleScope.launch(Dispatchers.Main) {
            sharedFlow.emit(sharedFlowObject)
        }
    }

    private fun onLocationSourceSelected() {
        sharedPreferences =
            requireActivity().getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("location", "")) {
            requireContext().resources.getString(R.string.map) -> {
                var myObject = Util.getSharedFlowObject(requireContext())
                lifecycleScope.launch(Dispatchers.Main) {
                    sharedFlow.emit(myObject)
                }
            }

            else -> {
                locationManager.startLocationUpdates(this)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadFragment() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun cacheWeatherData(weatherResponse: WeatherResponse) {
        val sharedPreferences =
            requireContext().getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(weatherResponse)
        sharedPreferences.edit().putString("cachedWeatherData", json).apply()
    }

    private fun getCachedWeatherData(): WeatherResponse? {
        val sharedPreferences =
            requireContext().getSharedPreferences("WeatherCache", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("cachedWeatherData", null)
        val gson = Gson()
        return gson.fromJson(json, WeatherResponse::class.java)
    }

    private fun showWeatherData(weatherResponse: WeatherResponse) {
        showLoading(false)
        binding.swipeDown.visibility = View.GONE
        binding.myLayout.visibility = View.VISIBLE
        val weatherHoursList = Util.createCurrentDayWeatherHoursList(weatherResponse.hourly)
        val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
        binding.hoursRecyclerView.adapter = hoursAdapter
        hoursAdapter.notifyDataSetChanged()
        val weatherDaysList =
            Util.createWeatherAllDaysList(requireContext(), weatherResponse.daily)
        val daysAdapter = DaysWeatherAdapter(requireContext(), weatherDaysList)
        binding.daysRecyclerView.adapter = daysAdapter
        daysAdapter.notifyDataSetChanged()
        binding.locationTextView.text = "${weatherResponse.timezone}"
        binding.pressureTextView.text = "${weatherResponse.current.pressure} hPa"
        binding.seaLevelTextView.text = "${weatherResponse.current.uvi} hPa"
        binding.humidityTextView.text = "${weatherResponse.current.humidity}%"
        binding.cloudTextView.text = "${weatherResponse.current.clouds} %"
        binding.visibilityTextView.text = "${weatherResponse.current.visibility} m"
        if (weatherResponse.current.weather[0].icon.equals("01d")) {
            binding.weatherIconImageView.setImageResource(R.drawable.sunny)
        } else if (weatherResponse.current.weather[0].icon.equals("01n")) {
            binding.weatherIconImageView.setImageResource(R.drawable.ic_night)
        } else {
            Glide.with(requireContext())
                .load("https://openweathermap.org/img/wn/${weatherResponse.current.weather[0].icon}@4x.png")
                .apply(RequestOptions().override(450, 350))
                .placeholder(R.drawable.sunny)
                .into(binding.weatherIconImageView)
        }
        binding.weatherDescriptionTextView.text =
            weatherResponse.current.weather[0].description
        var myObject = Util.getSharedFlowObject(requireContext())
        val temperature = weatherResponse.current.temp.toInt()
        val windSpeed = weatherResponse.current.windSpeed
        binding.temperatureTextView.text = when (myObject.temp) {
            "Celsius" -> "$temperature°C"
            "Fahrenheit" -> "$temperature°F"
            else -> "$temperature°K"
        }
        binding.windTextView.text = when (myObject.temp) {
            "Celsius" -> "$windSpeed ${getString(R.string.meter_Sec)}"
            "Fahrenheit" -> "$windSpeed ${getString(R.string.mile_Hour)}"
            else -> "$windSpeed ${getString(R.string.meter_sec)}"
        }
        if (weatherResponse.current.weather[0].description.contains("rain"))
            mListener.updateBackgroundAnimation("RAIN")
        else if (weatherResponse.current.weather[0].description.contains("snow")) {
            mListener.updateBackgroundAnimation("SNOW")
        } else {
            mListener.updateBackgroundAnimation("CLEAR")
        }
        binding.dateTextView.text = Util.getCurrentDateFormatted()
    }
}

