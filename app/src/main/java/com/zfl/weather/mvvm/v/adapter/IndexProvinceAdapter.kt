package com.zfl.weather.mvvm.v.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.IndexProvinceBean
import com.zfl.weather.mvvm.bean.StickyCityBean

class IndexProvinceAdapter (layoutResId: Int, data: MutableList<IndexProvinceBean>?) :

    BaseQuickAdapter<IndexProvinceBean, BaseViewHolder>(layoutResId, data){

    override fun convert(holder: BaseViewHolder, item: IndexProvinceBean) {
        holder.setText(R.id.tvProvinceAbbreviation, item.abbreviation)
    }
}