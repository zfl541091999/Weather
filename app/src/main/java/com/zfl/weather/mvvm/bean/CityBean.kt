package com.zfl.weather.mvvm.bean

class CityBean {
    var expand : Boolean = false
    var city : String = ""
    val districts : MutableList<CityListResult> by lazy {
        ArrayList<CityListResult>()
    }
}