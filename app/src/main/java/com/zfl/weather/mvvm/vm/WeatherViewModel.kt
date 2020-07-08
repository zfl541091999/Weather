package com.zfl.weather.mvvm.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.zfl.weather.mvvm.bean.*
import com.zfl.weather.mvvm.m.WeatherDataSource
import com.zfl.weather.utils.LogUtil

class WeatherViewModel(application: Application) : BaseViewModel(application) {

    val weatherDataSource: WeatherDataSource

    val weatherResultData : MutableLiveData<Result>

    val weatherCityData : MutableLiveData<WeatherCityListBody>

    val weatherCityDistrictData : MutableLiveData<MutableList<ProvinceBean>>

    val weatherStickyCityData : MutableLiveData<MutableList<StickyCityBean>>

    val indexProvinceData : MutableLiveData<MutableList<IndexProvinceBean>>

    init {
        weatherDataSource = WeatherDataSource(this)
        weatherResultData = MutableLiveData()
        weatherCityData = MutableLiveData()
        weatherCityDistrictData = MutableLiveData()
        weatherStickyCityData = MutableLiveData()
        indexProvinceData = MutableLiveData()
    }

    fun getWeather(lifecycleOwner: LifecycleOwner, city: String) {
        launch {
            weatherResultData.value = weatherDataSource.getWeather(lifecycleOwner, city).result
        }
    }

    fun getCityDistrictList(lifecycleOwner: LifecycleOwner) {
        launch {
            weatherCityDistrictData.value = weatherDataSource.generateProvinceList(weatherDataSource.getCityList(lifecycleOwner))
        }
    }

    fun getStickyCityList(lifecycleOwner: LifecycleOwner) {
        launch {
            weatherDataSource.generateStickyCityAndIndexList(weatherDataSource.getCityList(lifecycleOwner))
        }
    }

}