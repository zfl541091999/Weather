package com.zfl.weather.mvvm.vm

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.zfl.weather.request.ProgressListener
import com.zfl.weather.request.ZFLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class DownloadUploadViewModel(application: Application) : BaseViewModel(application) {

    var downloadTag = ""

    fun download(lifecycleOwner: LifecycleOwner, filePath: String, url: String, progressListener: ProgressListener) {
        downloadTag = UUID.randomUUID().toString()
        launch {
            withContext(Dispatchers.IO) {
                ZFLRequest.get(lifecycleOwner, url)
                    .setTag(downloadTag)
                    .asDownload(filePath, progressListener)
                    .request()
            }
        }
    }

    fun upload(lifecycleOwner: LifecycleOwner, filePath: String, url: String, progressListener: ProgressListener) {
        val file = File(filePath)
        launch {
            withContext(Dispatchers.IO) {
                ZFLRequest.post(lifecycleOwner, url)
                    .addHeader("Authorization", "")
                    .add("file", file)
                    .asUpload(progressListener)
                    .request()
            }
        }
    }

    fun cancelDownload () {
        ZFLRequest.cancel(downloadTag)
    }

}