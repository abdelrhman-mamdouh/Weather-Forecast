package com.example.weatherguide.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherguide.R
import com.example.weatherguide.databinding.ItemDayLayoutBinding
import com.example.weatherguide.model.WeatherDaysItem


class DaysWeatherAdapter(
    private val context: Context, private val weatherList: List<WeatherDaysItem>
) : RecyclerView.Adapter<DaysWeatherAdapter.DaysViewHolder>() {
    private lateinit var binding: ItemDayLayoutBinding

    data class DaysViewHolder(val binding: ItemDayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayLayoutBinding.inflate(inflater, parent, false)
        return DaysWeatherAdapter.DaysViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.binding.dayTextView.text = weatherItem.dayName
        holder.binding.tempTextView.text = "${weatherItem.temperature}°C"
        if (weatherItem.weatherIconResource == "01d") {
            holder.binding.weatherIconImageView.setImageResource(R.drawable.sunny)
        } else if (weatherItem.weatherIconResource == "01n") {
            holder.binding.weatherIconImageView.setImageResource(R.drawable.ic_night)
        } else {
            Glide.with(context)
                .load("https://openweathermap.org/img/wn/${weatherItem.weatherIconResource}@4x.png")
                .apply(RequestOptions().override(250, 150)).placeholder(R.drawable.sunny)
                .into(holder.binding.weatherIconImageView)
        }
        holder.binding.descriptionTextView.text = weatherItem.weatherDescription
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

}
