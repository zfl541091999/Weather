package com.zfl.weather.mvvm.v.adapter


import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.Future
import com.zfl.weather.utils_java.BGUtil
import com.zfl.weather.utils_java.ScreenUtil

class FutureWeatherAdapter(layoutResId: Int, data: MutableList<Future>?) :
    BaseQuickAdapter<Future, BaseViewHolder>(layoutResId, data) {

    lateinit var weatherIconMap : MutableMap<String, Int>

    override fun convert(holder: BaseViewHolder, item: Future) {
        holder.setText(R.id.tvDate, "日期：" + item.date)
        holder.setText(R.id.tvWeatherInfo, "天气状况：" + item.weather)
        holder.setText(R.id.tvDirect, "风向：" + item.direct)
        holder.setText(R.id.tvTemperature, "温度：" + item.temperature.replace("/", "~"))
        weatherIconMap[item.wid.day]?.let {
            holder.setImageResource(R.id.ivDay, it)
        }
        weatherIconMap[item.wid.night]?.let {
            holder.setImageResource(R.id.ivNight, it)
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val baseViewHolder = super.onCreateDefViewHolder(parent, viewType)
        baseViewHolder.getView<View>(R.id.clFutureWeatherArea).background = BGUtil.gradientDr(context,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(10f).toFloat(),
            R.color.white,
            R.color.color_056c62)
        return baseViewHolder
    }



}