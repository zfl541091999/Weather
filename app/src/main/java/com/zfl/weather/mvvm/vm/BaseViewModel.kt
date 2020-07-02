package com.zfl.weather.mvvm.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zfl.weather.utils.LogUtil
import kotlinx.coroutines.*

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val error by lazy { MutableLiveData<Exception>() }

    //运行在UI线程的协程
    fun launch(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(Dispatchers.Main) {
        try {
            //10秒超时
            withTimeout(10000){
                block()
            }
        } catch (e: java.lang.Exception) {
            error.value = e
        }
    }

    override fun onCleared() {
        super.onCleared()
        SupervisorJob().cancel()
    }


}