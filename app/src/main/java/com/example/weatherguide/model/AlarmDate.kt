package com.example.weatherguide.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm_table")
data class AlarmDate (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateTime:String
): Serializable
