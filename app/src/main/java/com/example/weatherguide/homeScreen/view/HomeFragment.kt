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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.Constants
import com.example.weatherguide.LocationListener
import com.example.weatherguide.MyLocationManager
import com.example.weatherguide.R
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.createWeatherAllDaysList
import com.example.weatherguide.model.createWeatherHoursList
import com.example.weatherguide.model.getCurrentDayWeatherData
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), LocationListener {
    private lateinit var viewModel: HomeViewModel
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

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )
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
        viewModel =
            ViewModelProvider(requireActivity(), homeViewModelFactory)[HomeViewModel::class.java]
        if (locationManager.checkPermissions()) {
            if (locationManager.isLocationEnabled()) {
                // locationManager.startLocationUpdates(this)
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
    }

    private suspend fun updateUI(latitude: Double, longitude: Double) {
        viewModel.getWeatherData(latitude, longitude)
        getAddressLocation(latitude, longitude)
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            val weatherHoursList = createWeatherHoursList(weatherResponse)
            val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
            hoursRecyclerView.adapter = hoursAdapter
            hoursAdapter.notifyDataSetChanged()
            val weatherDaysList = createWeatherAllDaysList(weatherResponse)
            val daysAdapter = DaysWeatherAdapter(requireContext(), weatherDaysList)
            daysRecyclerView.adapter = daysAdapter
            daysAdapter.notifyDataSetChanged()
            val currentWeatherData = getCurrentDayWeatherData(weatherResponse.list)
            if (currentWeatherData != null) {
                pressureTextView.text = "${currentWeatherData.main.pressure} hPa"
                seaLevelTextView.text = "${currentWeatherData.main.sea_level} hPa"
                humidityTextView.text = "${currentWeatherData.main.humidity}%"
                windTextView.text = "${currentWeatherData.wind.speed} m/s"
                cloudTextView.text = "${currentWeatherData.clouds.all} %"
                visibilityTextView.text = "${currentWeatherData.visibility} m"
                weatherDescriptionTextView.text = currentWeatherData.weather[0].description
                temperatureTextView.text =
                    "${convertKelvinToCelsius(currentWeatherData.main.temp)}°C"
                dateTextView.text = getCurrentDateFormatted()
            }
        }
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

    private suspend fun getAddressLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext()).getFromLocation(latitude!!, longitude!!, 1)
        try {
            if (geocoder != null) {
                if (geocoder.isNotEmpty()) {
                    val address = geocoder[0]
                    Log.i("TAG", "getAddressLocation:${latitude}+--${longitude} ")
                    val addressText = "${address?.locality}, ${address?.countryName}"
                    withContext(Dispatchers.Main) {
                        locationTextView.text = addressText
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        lifecycleScope.launch {
            updateUI(latitude, longitude)
        }
    }

     fun onLocationSourceSelected() {
        sharedPreferences =
            requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("selected_option", null)
        when (selectedOption) {
            "Map" -> {
                val sharedPreferences = requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
                val longitude = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
                Log.i("TAG", "onLocationSourceSelected: ${latitude}+${longitude}")
                lifecycleScope.launch {
                    updateUI(latitude, longitude)
                }
            }
            else->{
                locationManager.startLocationUpdates(this)
            }
        }
    }
}
