package com.zfl.weather.mvvm.vm

import android.app.Application
import android.os.Environment
import com.zfl.weather.request.ProgressListener
import com.zfl.weather.request.ZFLRequest
import com.zfl.weather.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.File

class DownloadViewModel(application: Application) : BaseViewModel(application) {

    var downloadTag = ""

    fun download(filePath: String, url: String, progressListener: ProgressListener) {
        downloadTag = launch {
            withContext(Dispatchers.IO) {
                ZFLRequest.get(url)
                    .asDownload(filePath, progressListener)
                    .request()
            }
        }
    }

    fun cancelDownload () {
        cancelJob(downloadTag)
    }

}