package com.example.weatherguide.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteLocation: FavoriteLocation)

    @Delete
    suspend fun delete(favoriteLocation: FavoriteLocation)

    @Query("SELECT * FROM fav_location_table")
     fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
}