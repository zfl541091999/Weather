package com.zfl.weather.mvvm.m

import com.zfl.weather.mvvm.bean.*
import com.zfl.weather.mvvm.vm.WeatherViewModel
import com.zfl.weather.request.ZFLRequest
import com.zfl.weather.utils.LogUtil
import kotlinx.coroutines.*
import okhttp3.ResponseBody

class WeatherDataSource (weatherViewModel: WeatherViewModel) :
    BaseDataSource<WeatherViewModel>(weatherViewModel) {

    val weatherDataUrl = "http://apis.juhe.cn/simpleWeather/query"

    val weatherCityUrl = "http://apis.juhe.cn/simpleWeather/cityList"

    val weatherTypeUrl = "http://apis.juhe.cn/simpleWeather/wids"

    val key = "你自己聚合数据的AppKey"
    //各个省份对应的缩写
    val indexProvinceMap : MutableMap<String, String> by lazy {
        HashMap<String, String>().also {
            //加入各省份对应简称
            it["北京"] = "京"
            it["上海"] = "沪"
            it["天津"] = "津"
            it["重庆"] = "渝"
            it["黑龙江"] = "黑"
            it["吉林"] = "吉"
            it["辽宁"] = "辽"
            it["内蒙古"] = "内"//内蒙古自治区简称为内蒙古，这里缩写为内
            it["河北"] = "冀"
            it["山西"] = "晋"
            it["陕西"] = "陕"
            it["山东"] = "鲁"
            it["新疆"] = "新"
            it["西藏"] = "藏"
            it["青海"] = "青"
            it["甘肃"] = "甘"
            it["宁夏"] = "宁"
            it["河南"] = "豫"
            it["江苏"] = "苏"
            it["湖北"] = "鄂"
            it["浙江"] = "浙"
            it["安徽"] = "皖"
            it["福建"] = "闽"
            it["江西"] = "赣"
            it["湖南"] = "湘"
            it["贵州"] = "贵"
            it["四川"] = "川"
            it["广东"] = "粤"
            it["云南"] = "云"
            it["广西"] = "桂"
            it["海南"] = "琼"
            it["香港"] = "港"
            it["澳门"] = "澳"
            it["台湾"] = "台"
        }
    }

    suspend fun getWeather(city: String):WeatherDataBody {
        return request {
            ZFLRequest.get(weatherDataUrl)
                .add("key", key)
                .add("city", city)
                .requestAs(WeatherDataBody::class.java)
        }
    }

    suspend fun getCityList():WeatherCityListBody {
        return request {
            ZFLRequest.get(weatherCityUrl)
                .add("key", key)
                .requestAs(WeatherCityListBody::class.java)
        }
    }

    suspend fun generateProvinceList(weatherCityListBody: WeatherCityListBody):MutableList<ProvinceBean> {
        withContext(Dispatchers.IO) {
            val provinceList = ArrayList<ProvinceBean>()
            for (result in weatherCityListBody.result) {
                var existsProvinceBean = findExistsProvince(provinceList, result.province)
                if (existsProvinceBean != null) {
                    var existsCityBean = findExistsCity(existsProvinceBean.cities, result.city)
                    if (existsCityBean != null) {
                        existsCityBean.districts.add(result)
                    } else {
                        val newCityBean = CityBean()
                        newCityBean.city = result.city
                        newCityBean.districts.add(result)
                        existsProvinceBean.cities.add(newCityBean)
                    }
                } else {
                    val newProvinceBean = ProvinceBean()
                    newProvinceBean.province = result.province
                    val newProvinceCityBean = CityBean()
                    newProvinceCityBean.city = result.city
                    newProvinceCityBean.districts.add(result)
                    newProvinceBean.cities.add(newProvinceCityBean)
                    provinceList.add(newProvinceBean)
                }
            }
            return@withContext provinceList
        }.let {
            return it
        }
    }

    private fun findExistsProvince(list: MutableList<ProvinceBean>, province: String) : ProvinceBean? {
        for (provinceBean in list) {
            if (provinceBean.province.equals(province)) {
                return provinceBean
            }
        }
        return null
    }

    private fun findExistsCity(list: MutableList<CityBean>, city: String) :CityBean? {
        for (cityBean in list) {
            if (cityBean.city.equals(city)) {
                return cityBean
            }
        }
        return null
    }

    private fun findExistsCity(list: MutableList<StickyCityBean>, city: String) :StickyCityBean? {
        for (cityBean in list) {
            if (cityBean.city.equals(city)) {
                return cityBean
            }
        }
        return null
    }


    suspend fun generateStickyCityAndIndexList(weatherCityListBody: WeatherCityListBody) {
        withContext(Dispatchers.IO) {
            val cityList = ArrayList<StickyCityBean>()
            val indexList = ArrayList<IndexProvinceBean>()
            for (result in weatherCityListBody.result) {
                var existsCityBean = findExistsCity(cityList, result.city)
                if (existsCityBean != null) continue
                var newCityBean = StickyCityBean()
                newCityBean.province = result.province
                newCityBean.city = result.city
                if (cityList.size == 0) {
                    newCityBean.isFirstInGroup = true
                } else {
                    val preCityBean = cityList[cityList.size - 1]
                    newCityBean.isFirstInGroup = !preCityBean.province.equals(newCityBean.province)
                }
                cityList.add(newCityBean)
                if (newCityBean.isFirstInGroup) {
                    val indexProvinceBean = IndexProvinceBean()
                    indexProvinceBean.position = cityList.indexOf(newCityBean)
                    indexProvinceBean.province = newCityBean.province
                    indexProvinceMap[newCityBean.province]?.let {
                        indexProvinceBean.abbreviation = it
                    }
                    indexList.add(indexProvinceBean)
                }
            }
            vm.weatherStickyCityData.postValue(cityList)
            vm.indexProvinceData.postValue(indexList)
        }
    }



}