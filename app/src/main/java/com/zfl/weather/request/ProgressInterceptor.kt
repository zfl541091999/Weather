package com.zfl.weather.request

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 下载请求时添加进度指示的Interceptor
 */
class ProgressInterceptor(progressListener: ProgressListener?) : Interceptor {

    val progressListener : ProgressListener?

    init {
        this.progressListener = progressListener
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        originalResponse.body?.let {
            return originalResponse.newBuilder().body(ProgressResponseBody(it, progressListener)).build()
        }
        //body为空的情况下，返回原Response（一般不会到这里吧？？）
        return chain.proceed(chain.request())
    }
}