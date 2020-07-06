package com.zfl.weather.request

import android.text.TextUtils
import com.zfl.weather.utils.LogUtil
import com.zfl.weather.utils_java.FileUtil
import com.zfl.weather.utils_java.NetWorkUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 实现链式便捷网络请求
 */
class ZFLRequest {

    companion object {

        val okHttpBuilder: OkHttpClient.Builder

        init {
            okHttpBuilder = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//连接超时时间10S
                .readTimeout(10, TimeUnit.SECONDS) //读取超时时间设置10S
                .writeTimeout(10, TimeUnit.SECONDS)//写入超时时间设置10S
        }

        fun get(url: String): RequestParam = RequestParam(Method.GET, url)

        fun post(url: String): RequestParam = RequestParam(Method.POST, url)

        fun postForm(url: String): RequestParam = RequestParam(Method.POSTFORM, url)

        fun request(requestParam: RequestParam): ResponseBody? {
            //初始化头部
            initHeaders(requestParam)
            //开始请求
            val iRetrofitApi = getRetrofit(requestParam).create(IRetrofitApi::class.java)
            var responseBody = when(requestParam.method) {
                Method.GET -> iRetrofitApi.get(requestParam.url, requestParam.paramsMap).execute().body()
                Method.POST -> iRetrofitApi.post(requestParam.url, requestParam.paramsMap).execute().body()
                Method.POSTFORM -> iRetrofitApi.postForm(requestParam.url, requestParam.paramsMap).execute().body()
                Method.DOWNLOAD -> iRetrofitApi.download(requestParam.url, requestParam.paramsMap).execute().body()
                else -> null
            }
            return responseBody
        }


        fun initHeaders(requestParam: RequestParam) {
            //初始化头部
            okHttpBuilder.addNetworkInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val requestBuilder = chain.request().newBuilder()
                    requestParam.headersMap?.let { map ->
                        map.forEach {
                            requestBuilder.header(it.key, it.value)
                        }
                    }
                    return chain.proceed(requestBuilder.build())
                }
            })
        }


        fun getRetrofit(requestParam: RequestParam) : Retrofit {
            when(requestParam.method) {
                Method.DOWNLOAD -> okHttpBuilder.addNetworkInterceptor(ProgressInterceptor(requestParam.progressListener))
            }
            return Retrofit.Builder()
                .baseUrl(NetWorkUtil.getHostName(requestParam.url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpBuilder.build())
                .build()
        }


    }
}