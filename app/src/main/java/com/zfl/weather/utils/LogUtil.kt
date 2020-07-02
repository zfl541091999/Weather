package com.zfl.weather.utils

import android.util.Log

class LogUtil {

    companion object {
        var isPrint = true//是否要打印日志

        fun v(msg: String) {
            print(Log.VERBOSE, msg)
        }

        fun e(msg: String) {
            print(Log.ERROR, msg)
        }

        fun d(msg: String) {
            print(Log.DEBUG, msg)
        }

        fun w(msg: String) {
            print(Log.WARN, msg)
        }

        fun i(msg: String) {
            print(Log.INFO, msg)
        }

        fun a(msg: String) {
            print(Log.ASSERT, msg)
        }

        private fun print(level: Int, msg: String) {
            if (isPrint) {
                val tag = getClassName()
                val line = getLineIndicator()
                Log.println(level, tag, line + msg)
            }
        }

        private fun getClassName(): String {
            val element = Exception().stackTrace[3]

            return element.fileName
        }

        //获取行所在的方法指示
        private fun getLineIndicator(): String {
            //3代表方法的调用深度：0-getLineIndicator，1-performPrint，2-print，3-调用该工具类的方法位置
            val element = Exception().stackTrace[3]
            return "(" +
                    element.fileName + ":" +
                    element.lineNumber + ")." +
                    element.methodName + ":"
        }
    }


}