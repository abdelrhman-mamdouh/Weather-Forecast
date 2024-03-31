package com.example.weatherguide.model

import com.example.weatherguide.MainCoroutineRule
import com.example.weatherguide.data.local.FakeWeatherLocalDataSource
import com.example.weatherguide.data.remote.FakeWeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class WeatherRepositoryImplTest {

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

    private val dummyFavoriteLocations = listOf(
        FavoriteLocation("San Francisco", 37.7749, -122.4194),
        FavoriteLocation("New York", 40.7128, -74.0060),
        FavoriteLocation("London", 51.5074, -0.1278)
    )

    private val dummyAlarms = listOf(
        AlarmDate(dateTime = "2024-04-01 08:00"),
        AlarmDate(dateTime = "2024-04-02 09:30"),
        AlarmDate(dateTime = "2024-04-03 07:45")
    )

    private val dummyLocationSuggestions = listOf(
        Suggestions(Properties("San Francisco, CA, USA", -122.4194, 37.7749)),
        Suggestions(Properties("New York, NY, USA", -74.0059, 40.7128))
    )
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        fakeLocalDataSource = FakeWeatherLocalDataSource(
            dummyFavoriteLocations.toMutableList(),
            dummyAlarms.toMutableList()
        )
        fakeRemoteDataSource = FakeWeatherRemoteDataSource(
            dummyWeatherResponse,
            dummyLocationSuggestions.toMutableList()
        )
        repository =
            WeatherRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource, Dispatchers.Main)
    }

    @Test
    fun getFavoriteLocations_localFavoriteLocations() = mainCoroutineRule.runBlockingTest {
        // When calling getFavoriteLocations
        val resultFlow = repository.getAllFavoriteLocations()

        // Collect
        val result = resultFlow.first()

        //Then
        assertThat(result, `is`(dummyFavoriteLocations))
    }

    @Test
    fun getAlerts_localAlerts() = mainCoroutineRule.runBlockingTest {
        //When calling getAlerts
        val resultFlow = repository.getAllAlarms()

        //Collect
        val result = resultFlow.first()

        //Then
        assertThat(result, `is`(dummyAlarms))
    }

    @Test
    fun getLocationSuggestions_remoteLocationSuggestions() = mainCoroutineRule.runBlockingTest {
        // When calling getLocationSuggestions (inputs search)
        val resultFlow = repository.getLocationSuggestions("San")

        // Collect
        val result = resultFlow.first()
        //Then
        assertThat(result, `is`(dummyLocationSuggestions))
    }

    @Test
    fun getWeatherResponse_remoteWeatherResponse() = mainCoroutineRule.runBlockingTest {
        // When calling getWeatherResponse
        var myShredObject = SharedFlowObject(30.1, 52.0, "", "", "")
        val resultFlow = repository.getWeatherData(myShredObject)

        // Collect
        val result = resultFlow.first()
        //Then
        assertThat(result, `is`(dummyWeatherResponse))
    }

}