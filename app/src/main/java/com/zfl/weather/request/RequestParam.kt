package com.zfl.weather.request

import android.text.TextUtils
import com.google.gson.Gson
import com.zfl.weather.utils.JsonUtil
import com.zfl.weather.utils.LogUtil
import com.zfl.weather.utils_java.FileUtil
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

class RequestParam(method: Method, url: String){

    var url: String
    //请求方法
    var method: Method
    //请求头部
    val headersMap: MutableMap<String, String> by lazy { HashMap<String, String>() }
    //请求参数
    val paramsMap: MutableMap<String, Any> by lazy { HashMap<String, Any>()}
    //下载，上传请求进度
    var progressListener : ProgressListener? = null
    //下载文件路径
    var downloadFilePath : String = ""

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

    /**
     * 调用此函数，则意味着将请求转为下载请求
     * @param filePath 文件路径，必须是全称
     * @param progressListener 下载进度
     */
    fun asDownload(filePath : String, progressListener: ProgressListener) : RequestParam {
        method = Method.DOWNLOAD
        downloadFilePath = filePath
        this.progressListener = progressListener
        return this
    }


    //直接进行请求，返回responseBody？
    fun request(): ResponseBody? {
        return ZFLRequest.request(this)?.apply {
            //如果是下载请求，还需要转换成inputStream写到文件里
            if (method == Method.DOWNLOAD) {
                if (TextUtils.isEmpty(downloadFilePath)) throw IOException("File Path is null!")
                val file = File(downloadFilePath)
                FileUtil.writeFile(file, byteStream())
            }

        }
    }

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