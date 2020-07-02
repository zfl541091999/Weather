package com.zfl.weather.request

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IRetrofitApi {
    /**
     * 发起get请求(不带中文参数)
     * @param url action路径
     * @param parameters get参数
     * @return Call
     */
    @GET
    fun get(@Url url: String, @QueryMap parameters: MutableMap<String, Any>): Call<ResponseBody>

    /**
     * 发起post请求(带中文参数，表单式请求)
     * @param url action路径
     * @param parameters post参数
     * @return Observable
     */
    @FormUrlEncoded
    @POST
    fun postForm(@Url url: String, @FieldMap parameters: MutableMap<String, Any>): Call<ResponseBody>

    /**
     * 发起post请求(不带中文参数，非表单式请求)
     * @param url
     * @param parameters
     * @return
     */
    @POST
    fun post(@Url url: String, @QueryMap parameters: MutableMap<String, Any>): Call<ResponseBody>

    /**
     * 发起上传请求
     * @param url
     * @param body
     * @return
     */
    @POST
    fun upload(@Url url: String, @Body body: RequestBody): Call<ResponseBody>
}