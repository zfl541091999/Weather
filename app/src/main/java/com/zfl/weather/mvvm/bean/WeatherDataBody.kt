package com.zfl.weather.mvvm.bean

import com.google.gson.annotations.SerializedName

data class WeatherDataBody(
    @SerializedName("error_code") val error_code: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("result") val result: Result
)

data class Result(
    @SerializedName("city") val city: String,
    @SerializedName("future") val future: MutableList<Future>,
    @SerializedName("realtime") val realtime: Realtime
)

data class Future(
    @SerializedName("date") val date: String,
    @SerializedName("direct") val direct: String,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("weather") val weather: String,
    @SerializedName("wid") val wid: Wid
)

data class Realtime(
    @SerializedName("aqi") val aqi: String,
    @SerializedName("direct") val direct: String,
    @SerializedName("humidity") val humidity: String,
    @SerializedName("info") val info: String,
    @SerializedName("power") val power: String,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("wid") val wid: String
)

data class Wid(
    @SerializedName("day") val day: String,
    @SerializedName("night") val night: String
)