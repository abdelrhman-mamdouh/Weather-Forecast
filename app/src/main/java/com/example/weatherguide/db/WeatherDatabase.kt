package com.example.productsmvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherguide.db.FavoriteLocationDAO
import com.example.weatherguide.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDAO(): FavoriteLocationDAO
    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_location_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}