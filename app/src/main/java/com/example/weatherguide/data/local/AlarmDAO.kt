package com.example.weatherguide.data.local

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

    @Delete
    suspend fun remove(alarmDate: AlarmDate)

    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun removeById(alarmId: Long)
    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<AlarmDate>>

}