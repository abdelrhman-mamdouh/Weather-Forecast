package com.example.weatherguide.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherguide.R
import com.example.weatherguide.model.WeatherHourItem

class HoursWeatherAdapter(private val context: Context, private val weatherList: List<WeatherHourItem>) :
    RecyclerView.Adapter<HoursWeatherAdapter.HoursViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_hour_layout, parent, false)
        return HoursViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.textViewHour.text = weatherItem.hour
        holder.textViewTemp.text = "${weatherItem.temp}°C"
        Glide.with(context).load("https://openweathermap.org/img/wn/${weatherItem.weatherIconResource}@4x.png")
            .apply(RequestOptions().override(250, 150))
            .placeholder(R.drawable.sunny)
            .into(holder.imageViewWeatherIcon)
    }
    override fun getItemCount(): Int {
        return weatherList.size
    }
    class HoursViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewHour: TextView = itemView.findViewById(R.id.hourTextView)
        val textViewTemp: TextView = itemView.findViewById(R.id.tempTextView)
        val imageViewWeatherIcon: ImageView = itemView.findViewById(R.id.weatherIconImageView)
    }
}
