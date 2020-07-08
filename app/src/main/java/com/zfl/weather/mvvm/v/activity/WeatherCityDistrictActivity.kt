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
import com.zfl.weather.mvvm.bean.ProvinceBean
import com.zfl.weather.mvvm.m.CityDistrictClick
import com.zfl.weather.mvvm.v.SearchingDialog
import com.zfl.weather.mvvm.v.adapter.ProvinceAdapter
import com.zfl.weather.mvvm.vm.WeatherViewModel
import com.zfl.weather.utils.LogUtil
import com.zfl.weather.utils_java.BGUtil
import com.zfl.weather.utils_java.ScreenUtil
import kotlinx.android.synthetic.main.aty_weather_city_district.*

class WeatherCityDistrictActivity : AppCompatActivity() {

    val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(WeatherViewModel::class.java)
    }

    val provinceList : MutableList<ProvinceBean> by lazy {
        ArrayList<ProvinceBean>()
    }

    val provinceAdapter : ProvinceAdapter by lazy {
        ProvinceAdapter(R.layout.item_province_item, provinceList)
    }

    //搜索窗口
    lateinit var searchingDialog: SearchingDialog
    lateinit var lavSearch: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_weather_city_district)
        initData()
        initView()
    }

    override fun onResume() {
        super.onResume()
        getCityList()
    }

    private fun getCityList() {
        showLoading()
        rvCityDistrict.postDelayed(Runnable {
            weatherViewModel.getCityDistrictList(this)
        }, 6000)//测试lottie动画
    }

    private fun initData() {

        weatherViewModel.weatherCityDistrictData.observe(this, Observer {
            provinceList.clear()
            provinceList.addAll(it)
            provinceAdapter.notifyDataSetChanged()
            hideLoading()
        })

        weatherViewModel.error.observe(this, Observer {
            hideLoading()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            LogUtil.e(it.toString())
        })
        //使用单例模式传递被点击中的区域
        CityDistrictClick.observe(this, Observer {
            val intent = Intent()
            intent.putExtra("district", it)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })

    }

    private fun initView() {

        rvCityDistrict.layoutManager = LinearLayoutManager(this)
        rvCityDistrict.adapter = provinceAdapter

        srlWeatherCityDistrict.setColorSchemeResources(R.color.color_008e82)
        srlWeatherCityDistrict.setOnRefreshListener {
            getCityList()
        }

        ivBack.setOnClickListener {
            finish()
        }

        initSearchDialog()
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

    fun showLoading() {
        if (!searchingDialog.isShow()) {
            searchingDialog.show()
            if (!lavSearch.isAnimating) {
                lavSearch.playAnimation()
            }
        }
    }

    fun hideLoading() {
        srlWeatherCityDistrict.isRefreshing = false
        if (searchingDialog.isShow()) {
            searchingDialog.dismiss()
        }
    }


}