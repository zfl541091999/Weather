package com.zfl.weather.mvvm.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zfl.weather.utils.LogUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val error by lazy { MutableLiveData<Exception>() }

    val jobMap by lazy {
        HashMap<String, Job>()
    }

    //运行在UI线程的协程
    //返回类型为String的jobTag，便于单个job取消
    fun launch(block: suspend CoroutineScope.() -> Unit) : String {
        val jobTag = UUID.randomUUID().toString()
        val job = viewModelScope.launch(Dispatchers.Main) {
            try {
                block()
            } catch (e: java.lang.Exception) {
                error.value = e
            }
        }
        job.invokeOnCompletion {
            jobMap.remove(jobTag)
        }
        jobMap[jobTag] = job
        return jobTag
    }

    fun cancelJob(jobTag : String) {
        val job = jobMap[jobTag]
        if (job != null && job.isActive) {
            job.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        //停止jobMap中所有的job
        for ((key, value) in jobMap) {
            if (value != null && value.isActive) {
                value.cancel()
            }
        }
        jobMap.clear()
    }


}