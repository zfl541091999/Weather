package com.zfl.weather.mvvm.bean

class StickyCityBean {

    var isFirstInGroup = false
    var province= ""
    var city = ""

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is StickyCityBean) return false
        return city.equals(other.city)
    }

}