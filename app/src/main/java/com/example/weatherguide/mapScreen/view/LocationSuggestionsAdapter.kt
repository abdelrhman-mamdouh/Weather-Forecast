package com.example.weatherguide.mapScreen.view

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.mapScreen.OnItemLocationClickListener
import com.example.weatherguide.model.Suggestions


class LocationSuggestionsAdapter(var listener: OnItemLocationClickListener) : ListAdapter<Suggestions, LocationSuggestionsAdapter.LocationSuggestionsViewHolder>(
    LocationSuggestionsDiffCallback()
){
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationSuggestionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_location_suggestion, parent, false)
        return LocationSuggestionsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocationSuggestionsViewHolder, position: Int) {
        val locationSuggestions = getItem(position)
        holder.bind(locationSuggestions)
    }

    inner class LocationSuggestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView = itemView.findViewById(R.id.suggestionTextView)
        private val itemLayout: CardView = itemView.findViewById(R.id.cardView)

        fun bind(locationSuggestions: Suggestions) {
            locationNameTextView.text = locationSuggestions.properties.formatted
            itemLayout.setOnClickListener {
                val latitude = locationSuggestions.properties.lat
                val longitude = locationSuggestions.properties.lon
                listener.onClick(latitude,longitude,locationSuggestions.properties.formatted)
            }
        }
    }

    class LocationSuggestionsDiffCallback : DiffUtil.ItemCallback<Suggestions>() {
        override fun areItemsTheSame(oldItem: Suggestions, newItem: Suggestions): Boolean {
            return oldItem.properties.formatted == newItem.properties.formatted
        }

        override fun areContentsTheSame(oldItem: Suggestions, newItem: Suggestions): Boolean {
            return oldItem == newItem
        }
    }

}