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
import com.zfl.weather.mvvm.bean.CityListResult
import com.zfl.weather.mvvm.m.CityDistrictClick

class CityAdapter(layoutResId: Int, data: MutableList<CityBean>?) :

    BaseQuickAdapter<CityBean, CityAdapter.CityHolder>(layoutResId, data)  {

    override fun convert(holder: CityHolder, item: CityBean) {
        holder.setText(R.id.tvCityName, item.city)
        holder.districts.clear()
        holder.districts.addAll(item.districts)
        holder.districtAdapter.notifyDataSetChanged()
        holder.getView<RelativeLayout>(R.id.rlCity).setOnClickListener {
            item.expand = !item.expand
            notifyDataSetChanged()
        }
        holder.getView<RecyclerView>(R.id.rvDistrict).visibility =
            if (item.expand) View.VISIBLE else View.GONE
        holder.setImageResource(R.id.ivExpand,
            if (item.expand) R.drawable.ic_expand_more_black_24dp
            else R.drawable.ic_chevron_left_black_24dp)


    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): CityHolder{
        val cityHolder = super.onCreateDefViewHolder(parent, viewType)
        val rvDistrict = cityHolder.getView<RecyclerView>(R.id.rvDistrict)
        rvDistrict.layoutManager = LinearLayoutManager(context)
        rvDistrict.adapter = cityHolder.districtAdapter
        cityHolder.districtAdapter.setOnItemClickListener { adapter, view, position ->
            CityDistrictClick.value = cityHolder.districts[position].district
        }
        return cityHolder
    }

    inner class CityHolder (view: View) : BaseViewHolder(view) {

        val districtAdapter : DistrictAdapter by lazy {
            DistrictAdapter(R.layout.item_district_item, districts)
        }

        val districts : MutableList<CityListResult> by lazy {
            ArrayList<CityListResult>()
        }
    }
}