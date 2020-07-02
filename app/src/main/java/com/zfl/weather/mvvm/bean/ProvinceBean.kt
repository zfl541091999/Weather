package com.zfl.weather.mvvm.bean

class ProvinceBean {
    var expand : Boolean = false
    var province : String = ""
    val cities : MutableList<CityBean> by lazy {
        ArrayList<CityBean>()
    }
}