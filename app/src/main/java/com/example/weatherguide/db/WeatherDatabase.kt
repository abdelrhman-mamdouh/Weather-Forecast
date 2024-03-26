package com.example.productsmvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherguide.db.AlarmDAO
import com.example.weatherguide.db.FavoriteLocationDAO
import com.example.weatherguide.model.AlarmDate
import com.example.weatherguide.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class, AlarmDate::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDAO(): FavoriteLocationDAO
    abstract fun alarmDAO(): AlarmDAO

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WeatherDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather_database"
            )
                .build()
        }

    }

}