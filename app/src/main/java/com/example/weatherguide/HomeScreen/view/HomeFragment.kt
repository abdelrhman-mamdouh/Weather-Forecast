package com.example.weatherguide.HomeScreen.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.HomeScreen.viewModel.HomeViewModel
import com.example.weatherguide.HomeScreen.viewModel.HomeViewModelFactory
import com.example.weatherguide.R
import com.example.weatherguide.db.WeatherLocalDataSourceImpl
import com.example.weatherguide.model.WeatherItem
import com.example.weatherguide.model.WeatherRepositoryImpl
import com.example.weatherguide.model.createWeatherAllDaysList
import com.example.weatherguide.model.createWeatherHoursList
import com.example.weatherguide.network.WeatherRemoteSourceDataImpl
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var productsFactory: HomeViewModelFactory
    private lateinit var weatherIconImageView: ImageView
    private lateinit var  hoursRecyclerView: RecyclerView
    private lateinit var daysRecyclerView: RecyclerView
    private lateinit var hoursLinearManger:LinearLayoutManager
    private lateinit var daysLinearManager:LinearLayoutManager

    private lateinit var pressureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var cloudTextView: TextView
    private lateinit var seaLevelTextView: TextView
    private lateinit var visibilityTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initialization(view)

        weatherIconImageView = view.findViewById<ImageView>(R.id.weatherIconImageView)
        productsFactory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteSourceDataImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )
        viewModel = ViewModelProvider(requireActivity(), productsFactory)[HomeViewModel::class.java]
        viewModel.getWeatherData()

        hoursRecyclerView = view.findViewById(R.id.hoursRecyclerView)
        hoursRecyclerView.setHasFixedSize(true)
        hoursLinearManger  = LinearLayoutManager(requireContext())
        hoursLinearManger.orientation = RecyclerView.HORIZONTAL
        hoursRecyclerView.layoutManager = hoursLinearManger


        daysRecyclerView = view.findViewById(R.id.daysRecyclerView)
        daysRecyclerView.setHasFixedSize(true)
        daysLinearManager = LinearLayoutManager(requireContext())
        daysLinearManager.orientation = RecyclerView.VERTICAL
        daysRecyclerView.layoutManager = daysLinearManager

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            val weatherHoursList =
                createWeatherHoursList(weatherResponse)
            val hoursAdapter = HoursWeatherAdapter(requireContext(), weatherHoursList)
            hoursRecyclerView.adapter = hoursAdapter
            hoursAdapter.notifyDataSetChanged()

            val weatherDaysList =
                createWeatherAllDaysList(weatherResponse)
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
            }

        }
    }

    private fun initialization(view: View) {
        pressureTextView = view.findViewById(R.id.pressureTextView)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        windTextView = view.findViewById(R.id.windTextView)
        cloudTextView = view.findViewById(R.id.cloudTextView)
        seaLevelTextView = view.findViewById(R.id.seaLevelTextView)
        visibilityTextView = view.findViewById(R.id.visibilityTextView)
    }
    private fun getCurrentHourRange(): IntRange {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val startHour = (currentHour / 3) * 3
        return startHour..(startHour + 2)
    }
    private fun getCurrentDayWeatherData(weatherDataList: List<WeatherItem>): WeatherItem? {
        val currentHourRange = getCurrentHourRange()
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateString = dateFormat.format(currentDate).substring(0, 10)

        return weatherDataList.filter { weatherData ->
            val dateString = weatherData.dt_txt.substring(0, 10)
            dateString == currentDateString
        }.filter { weatherData ->
            val date = dateFormat.parse(weatherData.dt_txt)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                hour in currentHourRange
            } else {
                false
            }
        }.firstOrNull()
    }
}