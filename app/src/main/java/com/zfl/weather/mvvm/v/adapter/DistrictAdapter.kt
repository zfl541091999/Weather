package com.zfl.weather.mvvm.v.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.CityListResult
import kotlinx.android.synthetic.main.item_district_item.view.*

class DistrictAdapter (layoutResId: Int, data: MutableList<CityListResult>?) :

    BaseQuickAdapter<CityListResult, BaseViewHolder>(layoutResId, data)  {


    override fun convert(holder: BaseViewHolder, item: CityListResult) {
        holder.setText(R.id.tvDistrictName, item.district)
    }
}