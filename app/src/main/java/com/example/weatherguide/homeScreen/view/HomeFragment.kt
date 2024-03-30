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
import com.example.weatherguide.MyLocationManager
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FragmentHomeBinding
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.homeScreen.viewModel.HomeViewModel
import com.example.weatherguide.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.createCurrentDayWeatherHoursList
import com.example.weatherguide.model.createWeatherAllDaysList
import com.example.weatherguide.network.ApiState
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import com.example.weatherguide.utills.Constants
import com.example.weatherguide.utills.Util
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
                .setMessage("You need to allow location permissions for the app to get weather data.")
                .setPositiveButton("Allow") { dialog, which ->
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

                        showLoading(false)
                        binding.swipeDown.visibility = View.GONE
                        binding.myLayout.visibility = View.VISIBLE
                        val weatherHoursList = createCurrentDayWeatherHoursList(state.data.hourly)
                        val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
                        binding.hoursRecyclerView.adapter = hoursAdapter
                        hoursAdapter.notifyDataSetChanged()

                        val weatherDaysList = createWeatherAllDaysList(requireContext(),state.data.daily)
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
                        if (state.data.current.weather[0].icon.equals("01d")) {
                            binding.weatherIconImageView.setImageResource(R.drawable.sunny)
                        } else if (state.data.current.weather[0].icon.equals("01n")) {
                            binding.weatherIconImageView.setImageResource(R.drawable.ic_night)
                        } else {
                            Glide.with(requireContext())
                                .load("https://openweathermap.org/img/wn/${state.data.current.weather[0].icon}@4x.png")
                                .apply(RequestOptions().override(450, 350))
                                .placeholder(R.drawable.sunny)
                                .into(binding.weatherIconImageView)
                        }
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

    override fun onLocationChanged(sharedFlowObject: SharedFlowObject) {
        lifecycleScope.launch(Dispatchers.Main) {
            sharedFlow.emit(sharedFlowObject)
        }
    }

    private fun onLocationSourceSelected() {
        sharedPreferences =
            requireActivity().getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        val selectedOption = sharedPreferences.getString("location", null)
        when (selectedOption) {
            requireContext().resources.getString(R.string.map) -> {

               var myObject = Util.getSharedFlowObject(requireContext())
                lifecycleScope.launch(Dispatchers.Main){
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
        val intent = activity?.intent
        activity?.finish()
        startActivity(intent!!)
        binding.swipeRefreshLayout.isRefreshing = false
    }
}

