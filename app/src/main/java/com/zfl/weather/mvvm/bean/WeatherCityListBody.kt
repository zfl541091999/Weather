package com.zfl.weather.mvvm.bean

data class WeatherCityListBody(
    val error_code: Int,
    val reason: String,
    val result: MutableList<CityListResult>
)

data class CityListResult(
    val city: String,
    val district: String,
    val id: String,
    val province: String
)