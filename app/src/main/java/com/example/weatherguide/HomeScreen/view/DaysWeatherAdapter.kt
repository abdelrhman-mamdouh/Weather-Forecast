package com.example.weatherguide.HomeScreen.view

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
import com.example.weatherguide.model.WeatherDaysItem
import com.example.weatherguide.model.WeatherHourItem


class DaysWeatherAdapter(private val context: Context, private val weatherList: List<WeatherDaysItem>) :
    RecyclerView.Adapter<DaysWeatherAdapter.DaysViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_day_layout, parent, false)
        return DaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.textViewDay.text = weatherItem.day
        holder.textViewTemp.text = "${weatherItem.temp}°C"
        Glide.with(context).load("https://openweathermap.org/img/wn/${weatherItem.weatherIconResource}@4x.png")
            .apply(RequestOptions().override(250, 150))
            .placeholder(R.drawable.sunny)
            .into(holder.imageViewWeatherIcon)
        holder.textViewDescription.text=weatherItem.description
    }
    override fun getItemCount(): Int {
        return weatherList.size
    }
    class DaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay: TextView = itemView.findViewById(R.id.dayTextView)
        val textViewTemp: TextView = itemView.findViewById(R.id.tempTextView)
        val textViewDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val imageViewWeatherIcon: ImageView = itemView.findViewById(R.id.weatherIconImageView)
    }
}
