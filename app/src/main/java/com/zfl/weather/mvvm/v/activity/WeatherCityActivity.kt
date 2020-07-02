package com.zfl.weather.mvvm.v.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.IndexProvinceBean
import com.zfl.weather.mvvm.bean.StickyCityBean
import com.zfl.weather.mvvm.v.SearchingDialog
import com.zfl.weather.mvvm.v.adapter.IndexProvinceAdapter
import com.zfl.weather.mvvm.v.adapter.StickyCityAdapter
import com.zfl.weather.mvvm.v.widget.TopSmoothScrollManager
import com.zfl.weather.mvvm.v.widget.ZFLStickyItemDecoration
import com.zfl.weather.mvvm.v.widget.ZFLStickyItemDecoration.IStickyCallBack
import com.zfl.weather.mvvm.vm.WeatherViewModel
import com.zfl.weather.utils.LogUtil
import com.zfl.weather.utils_java.BGUtil
import com.zfl.weather.utils_java.ScreenUtil
import kotlinx.android.synthetic.main.aty_weather_city.*

class WeatherCityActivity: AppCompatActivity()  {

    val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(WeatherViewModel::class.java)
    }

    val cityList : MutableList<StickyCityBean> by lazy {
        ArrayList<StickyCityBean>()
    }

    val cityAdapter : StickyCityAdapter by lazy {
        StickyCityAdapter(R.layout.item_sticky_city, cityList)
    }

    val indexList : MutableList<IndexProvinceBean> by lazy {
        ArrayList<IndexProvinceBean>()
    }

    val indexAdapter : IndexProvinceAdapter by lazy {
        IndexProvinceAdapter(R.layout.item_index_province, indexList)
    }

    //搜索窗口
    lateinit var searchingDialog: SearchingDialog
    lateinit var lavSearch: LottieAnimationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_weather_city)
        initData()
        initView()
    }

    override fun onResume() {
        super.onResume()
        getCityList()
    }

    private fun initData() {
        weatherViewModel.weatherStickyCityData.observe(this, Observer {
            cityList.clear()
            cityList.addAll(it)
            cityAdapter.notifyDataSetChanged()
            hideLoading()
        })

        weatherViewModel.indexProvinceData.observe(this, Observer {
            indexList.clear()
            indexList.addAll(it)
            indexAdapter.notifyDataSetChanged()
        })

        weatherViewModel.error.observe(this, Observer {
            hideLoading()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            LogUtil.e(it.toString())
        })
    }

    private fun initView() {

        srlWeatherCity.setColorSchemeResources(R.color.color_008e82)
        srlWeatherCity.setOnRefreshListener {
            getCityList()
        }

        rvCity.layoutManager = TopSmoothScrollManager(this)
        rvCity.adapter = cityAdapter
        cityAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent()
            intent.putExtra("city", cityList[position].city)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        rvCity.addItemDecoration(ZFLStickyItemDecoration(cityAdapter,
            object : IStickyCallBack<View, StickyProvinceData> {
                override fun createStickyView(): View {
                    return getStickyView()
                }

                override fun obtainInsideDataByPosition(position: Int): StickyProvinceData? {
                    if (cityList[position].isFirstInGroup) return generateStickyData(position)
                    return null
                }

                override fun bindView(view: View?, data: StickyProvinceData?) {
                    view?.findViewById<TextView>(R.id.tvStickyProvince)?.text = data?.province
                }

                override fun obtainHoverData(headPosition: Int): StickyProvinceData? {
                    for (index in headPosition downTo 0) {
                        if (cityList[index].isFirstInGroup) return generateStickyData(index)
                    }
                    return null
                }

                fun generateStickyData(position: Int):StickyProvinceData {
                    val data = StickyProvinceData()
                    data.province = cityList[position].province
                    return data
                }

            }
        ))

        rvProvinceIndex.layoutManager = LinearLayoutManager(this)
        rvProvinceIndex.adapter = indexAdapter
        indexAdapter.setOnItemClickListener { adapter, view, position ->
            rvCity.smoothScrollToPosition(indexList[position].position)
        }
        rvProvinceIndex.background = BGUtil.gradientDr(this,
            0,
            ScreenUtil.dip2px(15f).toFloat(),
            0,
            R.color.color_30000000)

        initSearchDialog()
    }

    private fun getStickyView() : View {
        val view = View.inflate(this, R.layout.widget_city_sticky, null)
        view.background = BGUtil.gradientDr(this,
            ScreenUtil.dip2px(1f),
            0f,
            R.color.gray,
            R.color.color_008e82)
        return view
    }

    private fun initSearchDialog() {
        searchingDialog = SearchingDialog(this)
        val view = View.inflate(this, R.layout.dialog_searching, null)
        view.background = BGUtil.gradientDr(
            this,
            0,
            ScreenUtil.dip2px(15f).toFloat(),
            0,
            R.color.white
        )
        view.findViewById<TextView>(R.id.tvSearch).text = "查询天气中..."
        lavSearch = view.findViewById(R.id.lavSearch)
        lavSearch.setAnimation("search-nearby.json")
        lavSearch.repeatCount = -1
        lavSearch.playAnimation()
        searchingDialog.init(view)
        searchingDialog.setDismissListener(DialogInterface.OnDismissListener {
            lavSearch.pauseAnimation()
        })
    }

    private fun getCityList() {
        showLoading()
        rvCity.postDelayed(Runnable {
            weatherViewModel.getStickyCityList()
        }, 6000)
    }

    fun showLoading() {
        if (!searchingDialog.isShow()) {
            searchingDialog.show()
            if (!lavSearch.isAnimating) {
                lavSearch.playAnimation()
            }
        }
    }

    fun hideLoading() {
        srlWeatherCity.isRefreshing = false
        if (searchingDialog.isShow()) {
            searchingDialog.dismiss()
        }
    }

    inner class StickyProvinceData : ZFLStickyItemDecoration.BaseStickyData() {
        var province = ""
    }

}
