package com.zfl.weather.mvvm.v

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import com.zfl.weather.R
import com.zfl.weather.utils_java.ScreenUtil

class SearchingDialog(context: Context) {

    val context: Context

    val dialog : Dialog by lazy {
        Dialog(context, R.style.ScaleDialogStyle)
    }

    init {
        this.context = context
    }

    fun init(view: View) {
        dialog.setContentView(view)
        dialog.window?.setGravity(Gravity.CENTER)
        //正方形显示
        dialog.window?.attributes?.width = ScreenUtil.getScreenWidth(context) * 1 / 2
        dialog.window?.attributes?.height = ScreenUtil.getScreenWidth(context) * 1 / 2
    }

    fun isShow():Boolean = dialog.isShowing

    fun show() {dialog.show()}

    fun dismiss() {dialog.dismiss()}

    fun setDismissListener(dismissListener: DialogInterface.OnDismissListener) {
        dialog.setOnDismissListener(dismissListener)
    }

}