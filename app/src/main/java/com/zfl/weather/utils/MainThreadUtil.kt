package com.zfl.weather.utils

import android.os.Handler
import android.os.Looper

/**
 * 主线程切换工具
 */
private val handler : Handler by lazy {
    Handler(looper)
}

private val looper : Looper by lazy {
    Looper.getMainLooper().also {
        Looper.prepare()
    }
}

fun <T> T.runOnUiThread(block : T.() -> Unit) {
    handler.post {
        block()
    }
}