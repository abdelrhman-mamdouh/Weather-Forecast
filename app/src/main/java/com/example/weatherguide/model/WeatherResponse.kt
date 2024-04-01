package com.example.weatherguide.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<Alert>?
)

data class Alert(
    @SerializedName("sender_name")
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)

data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>
)


data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>,
    val pop: Double
)


data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val summary: String,
    val temp: Temperature,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<WeatherDescription>,
    val clouds: Int,
    val pop: Double,
    val rain: Double?,
    val uvi: Double
)


data class Temperature(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)


data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)


data class WeatherHourItem(
    val time: String,
    val temperature: Int,
    val weatherIconResource: String
)

data class WeatherDaysItem(
    var dayName: String,
    val weatherIconResource: String,
    val weatherDescription: String,
    val temperature: Int,
)

data class LocationSuggestions(
    @SerializedName("features")
    val features: List<Suggestions>
)

data class Suggestions(
    @SerializedName("properties")
    val properties: Properties
)

data class Properties(
    @SerializedName("formatted")
    val formatted: String,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("lat")
    val lat: Double,
)


