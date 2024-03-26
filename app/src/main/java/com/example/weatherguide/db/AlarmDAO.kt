package com.example.weatherguide.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherguide.model.AlarmDate
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(alarmDate: AlarmDate)

    @Update
    suspend fun update(alarmDate: AlarmDate)

    @Delete
    suspend fun remove(alarmDate: AlarmDate)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<AlarmDate>>

}