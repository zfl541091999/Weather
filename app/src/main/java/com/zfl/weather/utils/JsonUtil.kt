package com.zfl.weather.utils

import org.json.JSONException
import org.json.JSONObject

class JsonUtil {
    companion object {
        //这里返回Json字符串里一些关键信息的字段名，可以视具体的返回数据进行改变
        internal val MSG = "msg"
        //新的项目采用的returnMsg的字段
        internal val RETURN_MSG = "return_msg"
        //RET的值决定着这次请求是否成功，具体值由返回数据决定
        internal val RET_CODE = "retCode"
        //新的项目采用的returnCode的字段
        internal val RETURN_CODE = "return_code"
        //聚合数据返回的code
        internal val ERROR_CODE = "error_code"
        //聚合数据返回的msg
        internal val REASON = "reason"
        //具体成功值由具体返回数据决定
        internal val SUCCESS_RET_CODE = 200
        //RET的值决定着这次请求是否成功，具体值由返回数据决定
        internal val RET = "ret"
        //具体成功值由具体返回数据决定
        internal val SUCCESS_RET = 1
        //一些相关测试字段
        internal val CODE = "code"
        //测试字段成功值
        internal val SUCCESS_CODE = 1
        //聚合数据的成功code
        internal val SUCCESS_ERROR_CODE = 0

        fun checkResult(json: String): Boolean {
            var b: Boolean
            try {
                val `object` = JSONObject(json)

                if (`object`.has(RET_CODE)) {
                    b = `object`.optInt(RET_CODE) == SUCCESS_RET_CODE
                } else if (`object`.has(RET)) {
                    b = `object`.optInt(RET) == SUCCESS_RET
                } else if (`object`.has(RETURN_CODE)) {
                    b = `object`.optInt(RETURN_CODE) == SUCCESS_RET_CODE
                } else if (`object`.has(CODE)) {
                    b =
                        `object`.optInt(CODE) == SUCCESS_CODE || `object`.optInt(CODE) == SUCCESS_RET_CODE
                } else if (`object`.has(ERROR_CODE)) {
                    b = `object`.optInt(ERROR_CODE) == SUCCESS_ERROR_CODE
                } else {
                    b = false
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                b = false
            }

            return b
        }

        fun getMessage(json: String): String {
            var message: String
            try {
                val `object` = JSONObject(json)
                if (`object`.has(MSG)) {
                    message = `object`.optString(MSG)
                } else if (`object`.has(RETURN_MSG)) {
                    message = `object`.getString(RETURN_MSG)
                } else if (`object`.has(REASON)) {
                    message = `object`.getString(REASON)
                } else {
                    //这里暂时不确定是什么信息
                    message = ""
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                message = "反正就是出错了"
            }

            return message
        }

        fun getCode(json: String): Int {
            var code = -1
            try {
                val `object` = JSONObject(json)
                if (`object`.has(RET_CODE)) {
                    code = `object`.optInt(RET_CODE)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return code
        }
    }

}