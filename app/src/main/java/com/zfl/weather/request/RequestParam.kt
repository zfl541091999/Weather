package com.zfl.weather.request

import com.google.gson.Gson
import com.zfl.weather.utils.JsonUtil
import okhttp3.ResponseBody

class RequestParam(method: Method, url: String){

    var url: String
    //请求方法
    var method: Method
    //请求头部
    val headersMap: MutableMap<String, String> by lazy { HashMap<String, String>() }
    //请求参数
    val paramsMap: MutableMap<String, Any> by lazy { HashMap<String, Any>()}

    init {
        this.method = method
        this.url = url
    }

    fun addHeader(key: String, value: String): RequestParam {
        headersMap.put(key, value)
        return this
    }

    fun add(key: String, value: Any): RequestParam{
        paramsMap.put(key, value)
        return this
    }


    //直接进行请求，返回responseBody？
    fun request(): ResponseBody? = ZFLRequest.request(this)

    //输入转化类型class，返回
    inline fun <reified T> requestAs(clazz: Class<T>): T {
        val gson = Gson()
        request()?.let {
            val jsonStr = it.string()
            if (JsonUtil.checkResult(jsonStr)) {
                return gson.fromJson(jsonStr, clazz)
            } else {
                throw Exception(JsonUtil.getMessage(jsonStr))
            }
        }
        throw Exception("null")
    }

}