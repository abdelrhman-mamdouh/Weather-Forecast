package com.example.weatherguide.favoriteScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherguide.R
import com.example.weatherguide.databinding.FavoritesLocationItemBinding
import com.example.weatherguide.favoriteScreen.OnClickListener
import com.example.weatherguide.model.FavoriteLocation

class FavoriteLocationAdapter(
    private var favoriteLocation: List<FavoriteLocation>,
    private val listener: OnClickListener<FavoriteLocation>,
    private val context: Context
) : RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolderLocations>() {

    private lateinit var binding: FavoritesLocationItemBinding

    data class ViewHolderLocations(val binding: FavoritesLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLocations {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.favorites_location_item, parent, false)
        return ViewHolderLocations(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderLocations, position: Int) {

        val currentLocation = favoriteLocation[position]
        holder.binding.location = currentLocation

        holder.binding.locationTextView.text = currentLocation.locationName
        holder.binding.removeButton.setOnClickListener {
            listener.onClickRemove(currentLocation)
        }
        holder.binding.locationFavView.setOnClickListener {
            listener.onClickLocationFavorite(currentLocation)
        }
    }

    override fun getItemCount(): Int {
        return favoriteLocation.size
    }

    fun setList(updatedLocations: List<FavoriteLocation>) {
        favoriteLocation = updatedLocations
        notifyDataSetChanged()
    }
}