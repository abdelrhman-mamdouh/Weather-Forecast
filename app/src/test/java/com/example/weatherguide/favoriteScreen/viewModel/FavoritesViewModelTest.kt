package com.example.weatherguide.favoriteScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherguide.MainCoroutineRule
import com.example.weatherguide.data.remote.ApiState
import com.example.weatherguide.model.FakeWeatherRepositoryImpl
import com.example.weatherguide.model.FavoriteLocation
import com.example.weatherguide.model.SharedFlowObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavoritesViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var repository: FakeWeatherRepositoryImpl
    private lateinit var sharedFlow: MutableSharedFlow<SharedFlowObject>

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        repository = FakeWeatherRepositoryImpl()
        sharedFlow = MutableSharedFlow()
        viewModel = FavoritesViewModel(repository)
    }

    @Test
    fun getFavoriteLocations_successResult() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyFavoriteLocations = listOf(
            FavoriteLocation(locationName = "San Francisco", lat = 37.7749, lon = -122.4194),
            FavoriteLocation(locationName = "New York", lat = 40.7128, lon = -74.0060),
            FavoriteLocation(locationName = "London", lat = 51.5074, lon = -0.1278)
        )
        repository.insert(dummyFavoriteLocations[0])
        repository.insert(dummyFavoriteLocations[1])
        repository.insert(dummyFavoriteLocations[2])
        // Then
        assertEquals(viewModel.favoriteLocations.value, ApiState.Success(dummyFavoriteLocations))
    }

    @Test
    fun removeFavoriteLocations_favoriteLocation_notExit() = mainCoroutineRule.runBlockingTest {
        // Given
        val dummyFavoriteLocations = listOf(
            FavoriteLocation(locationName = "San Francisco", lat = 37.7749, lon = -122.4194),
            FavoriteLocation(locationName = "New York", lat = 40.7128, lon = -74.0060),
            FavoriteLocation(locationName = "London", lat = 51.5074, lon = -0.1278)
        )

        dummyFavoriteLocations.forEach { repository.insert(it) }
        // When
        viewModel.removeLocation(dummyFavoriteLocations[0])

        // Then
        val result = repository.getAllFavoriteLocations().first()
        assertThat(result.contains(dummyFavoriteLocations[0]), `is`(false))
    }

}
