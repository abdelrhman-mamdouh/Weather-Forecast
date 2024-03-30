package com.example.weatherguide.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherguide.R
import com.example.weatherguide.databinding.ItemDayLayoutBinding
import com.example.weatherguide.databinding.ItemHourLayoutBinding
import com.example.weatherguide.model.WeatherHourItem


class HoursWeatherAdapter(
    private val context: Context,
    private val weatherList: List<WeatherHourItem>
) :
    RecyclerView.Adapter<HoursWeatherAdapter.HoursViewHolder>() {

    data class HoursViewHolder(val binding: ItemHourLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourLayoutBinding.inflate(inflater, parent, false)
        return HoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        val binding = holder.binding

        binding.hourTextView.text = weatherItem.time
        binding.tempTextView.text = "${weatherItem.temperature}°C"
        if (weatherItem.weatherIconResource == "01d") {
            holder.binding.weatherIconImageView.setImageResource(R.drawable.sunny)
        } else if (weatherItem.weatherIconResource == "01n") {
            holder.binding.weatherIconImageView.setImageResource(R.drawable.ic_night)
        } else {
            Glide.with(context)
                .load("https://openweathermap.org/img/wn/${weatherItem.weatherIconResource}@4x.png")
                .apply(RequestOptions().override(250, 150))
                .placeholder(R.drawable.sunny)
                .into(binding.weatherIconImageView)
        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}