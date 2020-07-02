package com.zfl.weather.mvvm.v.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.CityBean
import com.zfl.weather.mvvm.bean.Future
import com.zfl.weather.mvvm.bean.ProvinceBean

class ProvinceAdapter(layoutResId: Int, data: MutableList<ProvinceBean>?) :

    BaseQuickAdapter<ProvinceBean, ProvinceAdapter.ProvinceHolder>(layoutResId, data) {

    override fun convert(holder: ProvinceHolder, item: ProvinceBean) {
        holder.setText(R.id.tvProvinceName, item.province)
        holder.cityList.clear()
        holder.cityList.addAll(item.cities)
        holder.cityAdapter.notifyDataSetChanged()
        holder.getView<RelativeLayout>(R.id.rlProvince).setOnClickListener {
            item.expand = !item.expand
            notifyDataSetChanged()
        }
        holder.getView<RecyclerView>(R.id.rvCity).visibility =
            if (item.expand) View.VISIBLE else View.GONE
        holder.setImageResource(R.id.ivExpand,
            if (item.expand) R.drawable.ic_expand_more_black_24dp
            else R.drawable.ic_chevron_left_black_24dp)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): ProvinceHolder {
        val provinceHolder = super.onCreateDefViewHolder(parent, viewType)
        val rvCity = provinceHolder.getView<RecyclerView>(R.id.rvCity)
        rvCity.layoutManager = LinearLayoutManager(context)
        rvCity.adapter = provinceHolder.cityAdapter
        return provinceHolder
    }

    inner class ProvinceHolder(view: View) : BaseViewHolder(view) {

        val cityAdapter : CityAdapter by lazy {
            CityAdapter(R.layout.item_city_item, cityList)
        }

        val cityList : MutableList<CityBean> by lazy {
            ArrayList<CityBean>()
        }


    }
}