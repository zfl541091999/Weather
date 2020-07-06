package com.zfl.weather.request

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

class ProgressResponseBody(body: ResponseBody, listener: ProgressListener?) : ResponseBody() {

    val body : ResponseBody

    val listener : ProgressListener?

    val bufferedSource : BufferedSource by lazy {
        source(body.source()).buffer()
    }

    init {
        this.body = body
        this.listener = listener
    }

    override fun source(): BufferedSource {
        return bufferedSource
    }

    override fun contentType(): MediaType? {
        return body.contentType()
    }

    override fun contentLength(): Long {
        return body.contentLength()
    }

    private fun source(source: Source) : Source {
        return object : ForwardingSource(source) {

            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                var bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                listener?.progress(totalBytesRead, body.contentLength(), bytesRead == -1L)
                return bytesRead
            }

        }
    }

}