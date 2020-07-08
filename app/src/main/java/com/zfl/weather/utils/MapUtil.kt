package com.zfl.weather.utils

fun <T> T.putIfKeyNotNull(map: MutableMap<Any, T>, tag: String?):T {
    tag?.let {
        map[tag] = this
    }
    return this
}

fun <T> MutableMap<Any, T>.keyMatch(key: Any, block: (T) -> Unit) {
    forEach{
        if (it.key == key) {
            block(it.value)
        }
    }
}