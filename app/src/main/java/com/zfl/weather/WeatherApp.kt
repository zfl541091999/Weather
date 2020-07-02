package com.zfl.weather

import android.app.Application
import com.zfl.weather.utils_java.ScreenUtil

class WeatherApp : Application(){

    override fun onCreate() {
        super.onCreate()
        ScreenUtil.init(this)
    }
}