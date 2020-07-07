package com.zfl.weather.mvvm.v.activity

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.ftd.livepermissions.LivePermissions
import com.ftd.livepermissions.PermissionResult
import com.zfl.weather.R
import com.zfl.weather.mvvm.bean.Future
import com.zfl.weather.mvvm.bean.Result
import com.zfl.weather.mvvm.v.SearchingDialog
import com.zfl.weather.mvvm.v.adapter.FutureWeatherAdapter
import com.zfl.weather.mvvm.vm.DownloadUploadViewModel
import com.zfl.weather.mvvm.vm.WeatherViewModel
import com.zfl.weather.request.ProgressListener
import com.zfl.weather.utils.LogUtil
import com.zfl.weather.utils_java.BGUtil
import com.zfl.weather.utils_java.ScreenUtil
import kotlinx.android.synthetic.main.aty_test_weather.*
import java.io.File

class WeatherActivity : AppCompatActivity(){
    //download test
    val downloadUploadViewModel : DownloadUploadViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(DownloadUploadViewModel::class.java)
    }

    val downloadUploadDialog : ProgressDialog by lazy {
        ProgressDialog(this).also {
            it.setTitle("下载")
            it.setMessage("正在下载，请稍后...")
            it.setProgressNumberFormat("%1d KB/%2d KB")
            it.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            it.setCancelable(false)
            it.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", { dialog, which ->
                //TODO 后续添加取消下载功能
                downloadUploadViewModel.cancelDownload()
            })
        }
    }

    val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(WeatherViewModel::class.java)
    }

    val weatherIconMap: MutableMap<String, Int> by lazy {
        HashMap<String, Int>().also {
            it["00"] = R.drawable.ic_weather_qing
            it["01"] = R.drawable.ic_weather_duoyun
            it["02"] = R.drawable.ic_weather_yin
            it["03"] = R.drawable.ic_weather_zhenyu
            it["04"] = R.drawable.ic_weather_leizhenyu
            it["05"] = R.drawable.ic_weather_leizhenyu_bingbao
            it["06"] = R.drawable.ic_weather_yujiaxue
            it["07"] = R.drawable.ic_weather_yu
            it["08"] = R.drawable.ic_weather_yu
            it["09"] = R.drawable.ic_weather_yu
            it["10"] = R.drawable.ic_weather_baoyu
            it["11"] = R.drawable.ic_weather_baoyu
            it["12"] = R.drawable.ic_weather_baoyu
            it["13"] = R.drawable.ic_weather_zhenxue
            it["14"] = R.drawable.ic_weather_xue
            it["15"] = R.drawable.ic_weather_xue
            it["16"] = R.drawable.ic_weather_xue
            it["17"] = R.drawable.ic_weather_baoxue
            it["18"] = R.drawable.ic_weather_foggy
            it["19"] = R.drawable.ic_weather_dongyu
            it["20"] = R.drawable.ic_weather_shachenbao
            it["21"] = R.drawable.ic_weather_yu
            it["22"] = R.drawable.ic_weather_yu
            it["23"] = R.drawable.ic_weather_baoyu
            it["24"] = R.drawable.ic_weather_baoyu
            it["25"] = R.drawable.ic_weather_baoyu
            it["26"] = R.drawable.ic_weather_xue
            it["27"] = R.drawable.ic_weather_xue
            it["28"] = R.drawable.ic_weather_baoxue
            it["29"] = R.drawable.ic_weather_fuchen
            it["30"] = R.drawable.ic_weather_fuchen
            it["31"] = R.drawable.ic_weather_qiangshachenbao
            it["53"] = R.drawable.ic_weather_mai
        }
    }
    //未来五天天气状况
    val futureList: MutableList<Future> by lazy {
        ArrayList<Future>()
    }

    val futureWeatherAdapter: FutureWeatherAdapter by lazy {
        FutureWeatherAdapter(R.layout.item_future_weather, futureList)
    }

    //recyclerView高度
    var rvFutureHeight: Int = 0
    //是否已经展开，默认展开
    var expand: Boolean = true
    //在没获取到recyclerView高度前，不允许操作
    var expandOrCloseOperateAvailable: Boolean = false

    //搜索窗口
    lateinit var searchingDialog: SearchingDialog
    lateinit var lavSearch: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_test_weather)
        initData()
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11111 && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                var queryStr : String? = null
                if ("district" in it.keySet()) {
                    queryStr = it.getString("district")
                } else if ("city" in it.keySet()) {
                    queryStr = it.getString("city")
                }
                queryStr?.let {
                    etCity.setText(queryStr)
                    queryWeather(queryStr)
                }
            }
        }
    }

    private fun initData() {

        weatherViewModel.weatherResultData.observe(this, Observer {
            hideLoading()
            updateUI(it)
        })

        weatherViewModel.error.observe(this, Observer {
            hideLoading()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            LogUtil.e(it.toString())
        })

        //test
        downloadUploadViewModel.error.observe(this, Observer {
            closeDownloadDialog()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            LogUtil.e(it.toString())
        })

    }

    private fun closeDownloadDialog() {
        if (downloadUploadDialog.isShowing) {
            downloadUploadDialog.dismiss()
            downloadUploadDialog.progress = 0
            downloadUploadDialog.max = 0
        }
    }

    private fun initView() {

        clWeatherArea.background = BGUtil.gradientDr(
            this,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(10f).toFloat(),
            R.color.white,
            R.color.color_056c62
        )

        llExpand.background = BGUtil.gradientDr(
            this,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(10f).toFloat(),
            R.color.white,
            R.color.color_056c62
        )

        llQueryArea.background = BGUtil.gradientDr(
            this,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(15f).toFloat(),
            R.color.white,
            R.color.color_056c62
        )

        tvSelectCity.background = BGUtil.gradientDr(
            this,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(15f).toFloat(),
            R.color.white,
            R.color.color_056c62
        )

        tvSelectDistrict.background = BGUtil.gradientDr(
            this,
            ScreenUtil.dp2px(1f),
            ScreenUtil.dip2px(15f).toFloat(),
            R.color.white,
            R.color.color_056c62
        )

        srlWeather.setColorSchemeResources(R.color.color_008e82)
        srlWeather.setOnRefreshListener {
            queryWeather(etCity.text.toString().trim())
        }

        val inputFilters = Array<InputFilter>(1){InputFilter.LengthFilter(10)}
        etCity.filters = inputFilters
        //带布尔值返回的listener的写法，坑死人
        etCity.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                queryWeather(v.text.toString().trim())
                true
            }
            false
        }

        rvFuture.layoutManager = LinearLayoutManager(this)
        futureWeatherAdapter.weatherIconMap = weatherIconMap
        rvFuture.adapter = futureWeatherAdapter

        btQuery.setOnClickListener {
//            queryWeather(etCity.text.toString().trim())
            //upload test
            //download test
            LivePermissions(this)
                .request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).observe(this, Observer {
                    when(it) {
                        is PermissionResult.Grant -> {
                            downloadUploadDialog.setTitle("上传")
                            downloadUploadDialog.setMessage("正在上传，请稍后...")
                            downloadUploadDialog.show()
                            //upload test
                            //filepath 你想要上传的文件路径（全称）
                            var filePath = Environment.getExternalStorageDirectory().path +
                                    File.separator + "tieba" + File.separator +
                                    "F1EEC1C3116B6CDB20D269ACB291647A.jpg"
                            //url 接收上传文件的api的url
                            var url = ""
                            downloadUploadViewModel.upload(filePath, url, object : ProgressListener {
                                override fun progress(progress: Long, total: Long, done: Boolean) {
                                    downloadUploadDialog.max = (total/1024).toInt()
                                    downloadUploadDialog.progress = (progress/1024).toInt()
                                    if (done) {
                                        closeDownloadDialog()
                                    }
                                    LogUtil.e("progress:" + progress + " total:" + total + " done:" + done)
                                }
                            })
                        }
                        is PermissionResult.Rationale -> {
                            //被拒绝的权限
                            it.permissions.forEach {

                            }
                        }
                        is PermissionResult.Deny -> {
                            //被拒绝的权限，并且勾选了不再询问
                            it.permissions.forEach {

                            }
                        }

                    }
                })
        }

        llExpand.setOnClickListener {
            if (expandOrCloseOperateAvailable) {
                expand = !expand
                tvExpand.text = if (expand) "收起" else "展开"
                ivExpand.setImageResource(if (expand) R.drawable.ic_arrow_drop_up_white_24dp else R.drawable.ic_arrow_drop_down_white_24dp)
                expandOrCloseRVFuture(expand)
            }
        }

        tvSelectCity.setOnClickListener {
            val intent = Intent(this, WeatherCityActivity::class.java)
            startActivityForResult(intent, 11111)
        }

        tvSelectDistrict.setOnClickListener {
            val intent = Intent(this, WeatherCityDistrictActivity::class.java)
            startActivityForResult(intent, 11111)
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

    fun queryWeather(city: String) {
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "City is null", Toast.LENGTH_SHORT).show()
            srlWeather.isRefreshing = false
            return
        }
        showLoading()
        tvSelectCity.postDelayed(Runnable {
            weatherViewModel.getWeather(city)
        }, 6000)//测试lottie动画
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
        srlWeather.isRefreshing = false
        if (searchingDialog.isShow()) {
            searchingDialog.dismiss()
        }
    }

    fun updateUI(result: Result) {
        weatherIconMap[result.realtime.wid]?.let {
            ivWeather.setImageResource(it)
        }
        tvCity.text = "城市：" + result.city
        tvTemperature.text = "温度：" + result.realtime.temperature + "℃"
        tvWeatherInfo.text = "天气状况：" + result.realtime.info
        tvHumidity.text = "湿度：" + result.realtime.humidity
        tvDirect.text = "风向：" + result.realtime.direct
        tvWindPower.text = "风力：" + result.realtime.power
        tvAQI.text = "空气质量指数：" + result.realtime.aqi
        futureList.clear()
        futureList.addAll(result.future)
        rvFuture.adapter?.notifyDataSetChanged()
        if (expand) {
            tvCity.postDelayed(Runnable {
                rvFutureHeight = rvFuture.measuredHeight
                expandOrCloseOperateAvailable = true
            }, 1000)
        }
    }

    fun expandOrCloseRVFuture(expand: Boolean) {

        val valueAnimator = ValueAnimator.ofInt(
            if (expand) 0 else rvFutureHeight,
            if (expand) rvFutureHeight else 0
        )
        valueAnimator.addUpdateListener {
            rvFuture.layoutParams.height = it.animatedValue as Int
            rvFuture.requestLayout()
        }
        valueAnimator.duration = 1000
        valueAnimator.start()
    }

}