package com.zfl.weather.mvvm.v.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.CityBean
import com.zfl.weather.mvvm.bean.StickyCityBean

class StickyCityAdapter (layoutResId: Int, data: MutableList<StickyCityBean>?) :

    BaseQuickAdapter<StickyCityBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: StickyCityBean) {
        holder.setText(R.id.tvCityName, item.city)
    }


}