package com.example.weatherguide.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherguide.MainCoroutineRule
import com.example.weatherguide.data.local.AlarmDAO
import com.example.weatherguide.data.local.WeatherDatabase
import com.example.weatherguide.model.AlarmDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class AlarmDAOTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private lateinit var database: WeatherDatabase
    private lateinit var alarmDAO: AlarmDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        alarmDAO = database.alarmDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllAlarms_theSameAlarmList() = mainCoroutineRule.runBlockingTest {

        // Given
        val dummyAlarms = listOf(
            AlarmDate(1, dateTime = "2024-04-01 08:00"),
            AlarmDate(2, dateTime = "2024-04-02 09:30"),
            AlarmDate(3, dateTime = "2024-04-03 07:45")
        )
        // When
        alarmDAO.add(dummyAlarms[0])
        alarmDAO.add(dummyAlarms[1])
        alarmDAO.add(dummyAlarms[2])

        // Then
        val result = alarmDAO.getAllAlarms().first()
        assertThat(result, `is`(dummyAlarms))
    }

    @Test
    fun removeAlarm_alarmDate_null() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyAlarm = AlarmDate(1, dateTime = "2024-04-01 08:00")

        // When
        alarmDAO.add(dummyAlarm)
        alarmDAO.remove(dummyAlarm)

        // Then
        val result = alarmDAO.getAllAlarms().first()
        assertThat(result.isEmpty(), `is`(true))
    }

    @Test
    fun removeAlarmById() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyAlarms = listOf(
            AlarmDate(1, dateTime = "2024-04-01 08:00"),
            AlarmDate(2, dateTime = "2024-04-02 09:30"),
            AlarmDate(3, dateTime = "2024-04-03 07:45")
        )

        alarmDAO.add(dummyAlarms[0])
        alarmDAO.add(dummyAlarms[1])
        alarmDAO.add(dummyAlarms[2])

        // When
        alarmDAO.removeById(dummyAlarms[0].id)

        // Then
        val retrievedAlarms = alarmDAO.getAllAlarms().first()
        assertThat(retrievedAlarms.contains(dummyAlarms[0]), `is`(false))
    }
}