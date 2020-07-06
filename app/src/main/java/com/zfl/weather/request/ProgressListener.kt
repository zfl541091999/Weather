package com.zfl.weather.request

/**
 * 下载，上传进度回调接口
 */
interface ProgressListener {
    /**
     * @param progress 进度
     * @param total    总大小
     * @param done     是否完成
     */
    fun progress(progress: Long, total: Long, done: Boolean)
}