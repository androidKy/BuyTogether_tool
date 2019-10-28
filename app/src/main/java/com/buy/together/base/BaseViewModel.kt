package com.buy.together.base

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.accessibility.service.data.TaskBean
import com.accessibility.service.util.Constant
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.buy.together.R
import com.buy.together.bean.CityListBean
import com.buy.together.bean.CloseProxyBean
import com.buy.together.bean.ProxyIPBean
import com.buy.together.hook.sp.DeviceParams
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.*
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

open class BaseViewModel<c : Context,bv : BaseView>(val context: Context,val baseView: BaseView) {
    private var mGetProxyFailedCount = 0
    /**
     * 释放Observable的资源
     */
    open fun clearSubscribes() {

    }

    /**
     * 显示提示
     */
    fun showTip(mContainer: FrameLayout?, tip: String) {
        mContainer?.apply {
            removeAllViews()
            val tipLayoutView =
                LayoutInflater.from(context).inflate(R.layout.layout_tip, this, true)
            val textView = tipLayoutView.findViewById(R.id.tv_tip) as TextView
            textView.text = tip
        }
    }


    /**
     * 关闭端口
     *
     */
    fun closePort(curPort: String) {
        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType(Constant.CONTENT_TYPE)
            .addBodyParameter(Constant.POST_PARAM_METHOD, "closePort")
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, "2")
            .addBodyParameter(Constant.POST_PARAM_PORT, curPort)
            .addBodyParameter(
                Constant.POST_PARAM_IMEI, SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
                    .getString(Constant.KEY_REAL_DEVICE_IMEI)
            )
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.toString()?.run {
                        L.i("关闭端口成功：$this")
                        SPUtils.getInstance(Constant.SP_IP_PORTS).clear()
                        val closeProxyBean = Gson().fromJson(this, CloseProxyBean::class.java)
                        //baseView.onResponClosePort(closeProxyBean)
                    }
                }

                override fun onError(anError: ANError?) {
                    L.i("关闭端口失败：${anError?.response?.body()?.string()} errorMsg: ${anError?.errorDetail}")
                    //baseView.onResponClosePort(null)
                }
            })
    }

    /**
     * 申请端口
     */
    fun getPorts(taskBean: TaskBean) {
        L.i("开始申请端口: $taskBean")
        //匹配相应的城市
        val cityName = taskBean.task.delivery_address.city
        L.i("target cityName: $cityName")
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) }
        L.i("currentDate: $currentDate")
        val lastGetCityDate =
            SPUtils.getInstance(Constant.SP_CITY_LIST).getString(Constant.KEY_CITY_GET_DATE)
        if (!TextUtils.isEmpty(lastGetCityDate)) {
            if (TimeUtils.getDays(currentDate, lastGetCityDate) >= 1) { //对比当前时间与上次获取的时间
                //重新获取
                L.i("间隔一天，重新获取城市ID")
                getCityListFromNet(cityName)
            } else {
                L.i("从缓存获取城市ID")
                val data =
                    SPUtils.getInstance(Constant.SP_CITY_LIST).getString(Constant.KEY_CITY_DATA)
                if (TextUtils.isEmpty(data)) {
                    getCityListFromNet(cityName)
                } else {
                    getCityCode(cityName, data)
                }
            }
        } else getCityListFromNet(cityName)
    }

    /**
     * 获取城市列表
     */
    private fun getCityListFromNet(cityName: String) {
        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType(Constant.CONTENT_TYPE)
            .addBodyParameter(Constant.POST_PARAM_METHOD, "getCity")
            .addBodyParameter(Constant.POST_PARAM_IMEI, SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS).getString(Constant.KEY_REAL_DEVICE_IMEI))
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, 2.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.run {
                        val strResult = toString()
                        //保存城市列表，隔一天再重新获取
                        SPUtils.getInstance(Constant.SP_CITY_LIST).apply {
                            put(Constant.KEY_CITY_DATA, strResult)
                            put(
                                Constant.KEY_CITY_GET_DATE,
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run {
                                    format(
                                        Date()
                                    )
                                })
                        }
                        getCityCode(cityName, strResult)
                    }
                }

                override fun onError(anError: ANError?) {
                    L.e("getCityListFromNet error: ${anError?.errorDetail} result: ${anError?.response?.body()?.string()}")
                }
            })
    }

    /**
     * 获取任务城市的相应code
     */
    private fun getCityCode(cityName: String, cityData: String) {
        L.i("cityName: $cityName")
        ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
            override fun doInBackground(): String {
                var result: String = ""

                val cityListBean = Gson().fromJson(cityData, CityListBean::class.java)
                if (cityListBean.code == 0) {   //获取数据成功
                    val provinceList = cityListBean.data.cityList   //省
                    for (province in provinceList) {
                        for (city in province.data) {
//                            L.i("cityName: ${city.name} cityCode: ${city.cityid}")
                            if (cityName == city.name) {
                                result = city.cityid
                                L.i("target cityName: $cityName serverCityName: ${city.name} cityID: $result")
                                break
                            }
                        }
                    }
                }
                return result
            }

            override fun onSuccess(result: String?) {
                //城市ID获取完成，开始打开端口

                if (!TextUtils.isEmpty(result)) {
                    //判断是否有缓存
                    val portsCache = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(
                        Constant.KEY_IP_PORTS
                    )
                    if (TextUtils.isEmpty(portsCache))
                        requestPorts(result)
                    else {
                        L.i("cache ports: $portsCache")
                        //baseView.onRequestPortsResult(portsCache)
                    }
                } else {
                    L.i("$cityName 该城市没有IP，重新获取地址")
                    ToastUtils.showToast(context, "$cityName 没有相应的IP")
                    // 如果找不到 IP，默认广州IP
                    requestPorts("440100")
                    //uploadIpError(cityName)
                }
            }

            override fun onCancel() {
                L.i("获取城市ID取消")
            }

            override fun onFail(t: Throwable?) {
                L.i("获取城市ID失败: ${t?.message}")
            }

        })
    }

    /**
     * 正式开始申请端口
     */
    private fun requestPorts(cityId: String?) {
        L.i("cityId: $cityId")
        var finalCityId = cityId
        if (TextUtils.isEmpty(finalCityId)) {
            finalCityId = "0"
        }

        L.i(
            "params:\nmethod: getPort\nnumber: 1\narea:$finalCityId\nimei:${DevicesUtil.getIMEI(
                context
            )}\n" +
                    "platformId: 2"
        )

        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType(Constant.CONTENT_TYPE)
            .addBodyParameter(Constant.POST_PARAM_METHOD, "getPort")
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, "2")
            .addBodyParameter(Constant.POST_PARAM_AREA, finalCityId)
            .addBodyParameter(
                Constant.POST_PARAM_IMEI, SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
                    .getString(Constant.KEY_REAL_DEVICE_IMEI)
            )
            .addBodyParameter(Constant.POST_PARAM_NUMBER, 1.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.toString()?.run {
                        L.i("request ports result: $this")
                        val proxyIpBean = Gson().fromJson(this, ProxyIPBean::class.java)
                        when {
                            proxyIpBean?.data?.code == 200 -> {
                                mGetProxyFailedCount = 0
                                //上报IP信息
                                uploadIpInfo(proxyIpBean)

                                //保存已申请的端口
                                SPUtils.getInstance(Constant.SP_IP_PORTS).apply {
                                    put(Constant.KEY_IP_PORTS, this@run)
                                    put(Constant.KEY_CUR_PORT, proxyIpBean.data.port[0].toString())
                                }
                                //baseView.onRequestPortsResult(this)
                            }
                            /*   proxyIpBean?.data?.code == 203 -> {

                               }*/
                            else -> {  //重新请求
                                mGetProxyFailedCount++
                                L.i("请求代理数据出错：code = ${proxyIpBean?.data?.code}" +
                                        " mGetProxyFailedCount=$mGetProxyFailedCount")
                                if (mGetProxyFailedCount <= 3) {
                                    requestPorts(cityId)
                                } else{
                                    mGetProxyFailedCount = 0
                                    //baseView.onResponPortsFailed("请求端口数据出错：code = ${proxyIpBean.data?.code}")
                                }
                            }
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    L.i("申请端口失败：${anError?.errorDetail}")
                    //baseView.onResponPortsFailed("申请端口失败：${anError?.errorDetail}")
                }
            })
    }

    /**
     * 上传IP信息
     */
    private fun uploadIpInfo(proxyIPBean: ProxyIPBean) {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<TaskBean>() {
            override fun doInBackground(): TaskBean? {
                val taskBeanStr = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getString(
                    Constant.KEY_TASK_DATA
                )
                if (!TextUtils.isEmpty(taskBeanStr)) {
                    return Gson().fromJson(taskBeanStr, TaskBean::class.java)
                }
                return null
            }

            override fun onSuccess(taskBean: TaskBean?) {
                taskBean?.task?.run {
                    ApiManager()
                        .setDataListener(object : DataListener {
                            override fun onSucceed(result: String) {

                            }

                            override fun onFailed(errorMsg: String) {

                            }
                        })
                        .uploadIpInfo(
                            task_id.toString(), delivery_address.city,
                            proxyIPBean.data.domain, DevicesUtil.getWifiMacAddr(context)
                        )
                }
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }
        })
    }

    /**
     * 保存设备参数
     */
    fun saveDeviceParams(taskBean: TaskBean) {
        val spUtils = SPUtils.getInstance(Constant.SP_DEVICE_PARAMS)
        taskBean.task?.device?.run {
            spUtils.apply {
                L.i(
                    "模拟imei: $imei 真实imei: ${SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
                        .getString(Constant.KEY_REAL_DEVICE_IMEI)}"
                )
                put(DeviceParams.IMEI_KEY, imei, true)
                put(DeviceParams.IMSI_KEY, imsi, true)
                put(DeviceParams.MAC_KEY, mac, true)
                put(DeviceParams.USER_AGENT_KEY, useragent, true)
                put(DeviceParams.BRAND_KEY, brand, true)
                put(DeviceParams.MODEL_KEY, model, true)
                put(DeviceParams.SDK_KEY, android, true)
                put(DeviceParams.SYSTEM_KEY, system, true)
            }
        }

    }

}