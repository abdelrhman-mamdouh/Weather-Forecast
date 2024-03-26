package com.example.weatherguide.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm_table")
data class AlarmDate (
        @PrimaryKey
        val dateTime:String
    ): Serializable
