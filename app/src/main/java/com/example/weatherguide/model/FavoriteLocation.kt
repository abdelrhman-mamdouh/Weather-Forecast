package com.example.weatherguide.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "fav_location_table")
data class FavoriteLocation(
    @PrimaryKey
    val locationName: String,
    val lat: Double,
    val lon: Double
) : Serializable
