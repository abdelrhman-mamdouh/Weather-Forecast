package com.example.weatherguide.homeScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherguide.MainCoroutineRule
import com.example.weatherguide.data.remote.ApiState
import com.example.weatherguide.model.CurrentWeather
import com.example.weatherguide.model.DailyWeather
import com.example.weatherguide.model.FakeWeatherRepositoryImpl
import com.example.weatherguide.model.FeelsLike
import com.example.weatherguide.model.HourlyWeather
import com.example.weatherguide.model.SharedFlowObject
import com.example.weatherguide.model.Temperature
import com.example.weatherguide.model.WeatherDescription
import com.example.weatherguide.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private val dummyWeatherResponse = WeatherResponse(
        37.7749, -122.4194,
        "America/Los_Angeles",
        -25200,
        CurrentWeather(
            1617088740,
            1617065100,
            1617110420,
            15.75,
            13.91,
            1013,
            82,
            12.93,
            5.79,
            90,
            10000,
            5.14,
            360,
            7.72,
            listOf(
                WeatherDescription(
                    804,
                    "Clouds",
                    "overcast clouds",
                    "04d"
                )
            )
        ),
        listOf(
            HourlyWeather(
                1617090000,
                15.71,
                13.81,
                1013,
                82,
                12.89,
                5.82,
                90,
                10000,
                5.03,
                360,
                7.65,
                listOf(
                    WeatherDescription(
                        804,
                        "Clouds",
                        "overcast clouds",
                        "04d"
                    )
                ),
                0.12
            )
        ),
        daily = listOf(
            DailyWeather(
                1617084000,
                1617065100,
                1617110420,
                1617138660,
                1617082860,
                0.61,
                "Overcast clouds",
                Temperature(
                    16.22,
                    14.6,
                    16.22,
                    15.72,
                    16.22,
                    14.6
                ),
                FeelsLike(
                    14.3,
                    13.95,
                    14.3,
                    13.95
                ),
                1013,
                82,
                13.06,
                5.15,
                360,
                7.64,
                listOf(
                    WeatherDescription(
                        804,
                        "Clouds",
                        "overcast clouds",
                        "04d"
                    )
                ),
                90,
                0.7,
                6.17,
                6.17
            )
        ),
        null
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: HomeViewModel
    lateinit var repository: FakeWeatherRepositoryImpl
    lateinit var sharedFlow: MutableSharedFlow<SharedFlowObject>

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        repository = FakeWeatherRepositoryImpl()
        sharedFlow = MutableSharedFlow()
        viewModel = HomeViewModel(repository, sharedFlow)
    }
    @Test
    fun getWeatherData_sharedFlowObject_successResult() = mainCoroutineRule.runBlockingTest {
        // Given
        val sharedFlowObject = SharedFlowObject(30.0, 30.0, "", "", "")
        // When
        launch {
            sharedFlow.emit(sharedFlowObject)
        }
        // Then
        val result = viewModel.weatherData.first()
        assertEquals(ApiState.Success(dummyWeatherResponse), result)

    }
}