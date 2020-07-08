package com.zfl.weather.request

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.zfl.weather.utils.*
import com.zfl.weather.utils_java.NetWorkUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 实现链式便捷网络请求
 */
class ZFLRequest {

    companion object {

        val callMap : MutableMap<Any, Calling> by lazy {
            HashMap<Any, Calling>()
        }

        val okHttpBuilder: OkHttpClient.Builder

        init {
            okHttpBuilder = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//连接超时时间10S
                .readTimeout(10, TimeUnit.SECONDS) //读取超时时间设置10S
                .writeTimeout(10, TimeUnit.SECONDS)//写入超时时间设置10S
        }

        fun get(lifecycleOwner: LifecycleOwner, url: String): RequestParam = RequestParam(lifecycleOwner, Method.GET, url)

        fun post(lifecycleOwner: LifecycleOwner, url: String): RequestParam = RequestParam(lifecycleOwner, Method.POST, url)

        fun postForm(lifecycleOwner: LifecycleOwner, url: String): RequestParam = RequestParam(lifecycleOwner, Method.POSTFORM, url)

        fun request(requestParam: RequestParam): ResponseBody? {
            //初始化头部
            initHeaders(requestParam)
            //开始请求
            val iRetrofitApi = getRetrofit(requestParam).create(IRetrofitApi::class.java)
            var call = when(requestParam.method) {
                Method.GET -> iRetrofitApi.get(requestParam.url, requestParam.paramsMap)
                Method.POST -> iRetrofitApi.post(requestParam.url, requestParam.paramsMap)
                Method.POSTFORM -> iRetrofitApi.postForm(requestParam.url, requestParam.paramsMap)
                Method.DOWNLOAD -> iRetrofitApi.download(requestParam.url, requestParam.paramsMap)
                Method.UPLOAD -> iRetrofitApi.upload(requestParam.url, requestParam.paramsMap, requestParam.uploadBody)
                else -> null
            }
            call?.let {
                it.runOnUiThread {
                    val calling = Calling(requestParam.tag, this)
                    requestParam.lifecycleOwner.lifecycle.addObserver(calling)
                    calling.putIfKeyNotNull(callMap, requestParam.tag)
                }
                return it.execute().body()
            }
            return null
        }


        fun initHeaders(requestParam: RequestParam) {
            //初始化头部
            okHttpBuilder.addNetworkInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val requestBuilder = chain.request().newBuilder()
                    requestParam.headersMap.forEach {
                        requestBuilder.header(it.key, it.value)
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

        fun cancel(tag: Any?) {
            tag?.let {
                callMap.remove(it)?.call?.cancel()
            }
        }

    }

    class Calling(tag: Any?, call: Call<ResponseBody>) : LifecycleObserver {

        val tag: Any?

        val call: Call<ResponseBody>

        init {
            this.tag = tag
            this.call = call
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun stop() {
            //首先调用cancel，如果tag不为空，则会取消掉这次calling
            cancel(tag)
            //如果此次calling没有设置tag，那在这里进行取消
            if (!call.isCanceled) {
                call.cancel()
            }
        }

    }

}