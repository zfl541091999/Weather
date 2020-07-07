package com.zfl.weather.request

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*

/**
 * 上传文件时使用的带进度的requestBody
 */
class ProgressRequestBody(requestBody: RequestBody, progressListener: ProgressListener?) : RequestBody() {

    private val progressListener : ProgressListener?

    private val requestBody : RequestBody

    init {
        this.progressListener = progressListener
        this.requestBody = requestBody
    }


    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        val bufferedSink = sink(sink).buffer()
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }


    private fun sink(sink: Sink) : Sink {

        return object : ForwardingSink(sink) {

            var bytesWritten = 0L

            var contentLength = 0L

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                progressListener?.progress(bytesWritten, contentLength, contentLength == bytesWritten)
            }

        }
    }
}