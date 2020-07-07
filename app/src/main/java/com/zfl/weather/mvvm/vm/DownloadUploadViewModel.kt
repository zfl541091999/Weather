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

class DownloadUploadViewModel(application: Application) : BaseViewModel(application) {

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

    fun upload(filePath: String, url: String, progressListener: ProgressListener) {
        val file = File(filePath)
        launch {
            withContext(Dispatchers.IO) {
                ZFLRequest.post(url)
                    .addHeader("Authorization", "")
                    .add("file", file)
                    .asUpload(progressListener)
                    .request()
            }
        }
    }

    fun cancelDownload () {
        cancelJob(downloadTag)
    }

}