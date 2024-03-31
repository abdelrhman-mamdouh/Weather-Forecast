package com.example.weatherguide.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherguide.MainCoroutineRule
import com.example.weatherguide.data.local.FavoriteLocationDAO
import com.example.weatherguide.data.local.WeatherDatabase
import com.example.weatherguide.model.FavoriteLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class FavoriteLocationDAOTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private lateinit var database: WeatherDatabase
    private lateinit var favoriteLocationDAO: FavoriteLocationDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        favoriteLocationDAO = database.weatherDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getFavoriteLocations_theSameFavoriteLocation() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyFavoriteLocations = listOf(
            FavoriteLocation("San Francisco", 37.7749, -122.4194),
            FavoriteLocation("New York", 40.7128, -74.0060),
            FavoriteLocation("London", 51.5074, -0.1278)
        )

        // When
        favoriteLocationDAO.insert(dummyFavoriteLocations[0])
        favoriteLocationDAO.insert(dummyFavoriteLocations[1])
        favoriteLocationDAO.insert(dummyFavoriteLocations[2])

        // Then
        val result = favoriteLocationDAO.getAllFavoriteLocations().first()
        assertThat(result, `is`(dummyFavoriteLocations))
    }

    @Test
    fun deleteFavoriteLocation_favoriteLocation_null() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyFavoriteLocations = FavoriteLocation("London", 51.5074, -0.1278)

        // When
        favoriteLocationDAO.insert(dummyFavoriteLocations)
        favoriteLocationDAO.delete(dummyFavoriteLocations)

        // Then
        val result = favoriteLocationDAO.getAllFavoriteLocations().first()
        assertThat(result, `is`(nullValue()))
    }


}