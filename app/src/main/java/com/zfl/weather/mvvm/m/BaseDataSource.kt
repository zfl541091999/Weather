package com.zfl.weather.mvvm.m

import com.zfl.weather.mvvm.vm.BaseViewModel
import com.zfl.weather.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class BaseDataSource<VM : BaseViewModel>(vm : VM) {

    val vm: VM

    init {
        this.vm = vm
    }

    suspend fun <T> request(call: suspend () -> T) : T {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }
    }

}