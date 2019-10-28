package com.proxy.droid.network

import android.text.TextUtils
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.proxy.droid.ProxyConstant.*
import com.proxy.droid.bean.CityListBean
import com.proxy.droid.bean.ProxyIPBean
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.ThreadUtils
import com.utils.common.TimeUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Description:代理API管理类
 * Created by Quinin on 2019-08-16.
 **/
class ProxyApiManager {
    private var mCityName: String = ""
    private var mImei: String = ""
    private var mPortCount: Int = 1 //请求端口数量

    private var mProxyDataListener: ProxyDataListener? = null

    constructor(cityName: String, imei: String, portCount: Int) {
        this.mCityName = cityName
        this.mImei = imei
        this.mPortCount = portCount
    }

    constructor(cityName: String, imei: String) {
        this.mCityName = cityName
        this.mImei = imei
    }

    companion object {
        const val URL_PROXY_IP = "http://ip.25ios.com:8089/6796324d5300e5978673d71c50780067.php"
        // const val URL_PROXY_IP = "http://ip.25ios.com:8088/api.php"
        const val DATA_TYPE = "multipart/form-data"
        const val POST_PARAM_METHOD = "method"
        const val POST_PARAM_NUMBER: String = "number"
        const val POST_PARAM_IMEI = "imei"
        const val POST_PARAM_PLATFORMID = "platformId"
        const val POST_PARAM_AREA = "area"
        const val POST_PARAM_PORT = "port"
    }

    /**
     * 通过关闭端口，然后重新请求端口
     */
    fun requestPortByClosePort(proxyDataListener: ProxyDataListener) {
        mProxyDataListener = proxyDataListener
        closePort(SPUtils.getInstance(SP_IP_PORTS).getString(KEY_CUR_PORT))
    }

    /**
     * 直接请求端口
     */
    fun requestPortDirectly(proxyDataListener: ProxyDataListener) {
        mProxyDataListener = proxyDataListener
        getPort()
    }

    /**
     * 请求端口前先关闭端口
     */
    private fun closePort(port: String?) {
        L.i("即将关闭的端口：$port")
        if (port.isNullOrEmpty()) {
            getPort()
            return
        }
        AndroidNetworking.post(URL_PROXY_IP)
                .setContentType(DATA_TYPE)
                .addBodyParameter(POST_PARAM_METHOD, "closePort")
                .addBodyParameter(POST_PARAM_PLATFORMID, "2")
                .addBodyParameter(POST_PARAM_PORT, port)
                .addBodyParameter(POST_PARAM_IMEI, mImei)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.toString()?.let {
                            L.i("关闭端口结果：$it")
                            try {
                                JSONObject(it).getJSONObject("data").getInt("code").apply {
                                    if (this@apply == 200) {
                                        SPUtils.getInstance(SP_IP_PORTS).clear(true)
                                    }
                                }
                            } catch (e: Exception) {

                            }
                            getPort()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        L.i("关闭端口失败：${anError?.response?.body()?.string()} errorMsg: ${anError?.errorDetail}")
                        // mainView.onResponClosePort(null)
                        getPort()
                    }
                })
    }

    /**
     * 获取端口
     */
    private fun getPort() {
        //判断是否有缓存
        val portsCache = SPUtils.getInstance(SP_IP_PORTS).getString(KEY_IP_DATA)
        L.i("缓存端口：$portsCache")
        if (!TextUtils.isEmpty(portsCache)) {
            Gson().fromJson(portsCache, ProxyIPBean::class.java)?.apply {
                responProxyData(this)
            }
            return
        }

        L.i("target cityName: $mCityName imei:$mImei")
        if (TextUtils.isEmpty(mCityName)) {
            requestPorts(mCityName)
            return
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) }
        L.i("currentDate: $currentDate")
        val lastGetCityDate = SPUtils.getInstance(SP_CITY_LIST).getString(KEY_CITY_GET_DATE)
        if (!TextUtils.isEmpty(lastGetCityDate)) {
            if (TimeUtils.getDays(currentDate, lastGetCityDate) >= 1) { //对比当前时间与上次获取的时间
                //重新获取
                L.i("间隔一天，重新获取城市ID")
                getCityListFromNet()
            } else {
                L.i("从缓存获取城市ID")
                val data = SPUtils.getInstance(SP_CITY_LIST).getString(KEY_CITY_DATA)
                if (TextUtils.isEmpty(data)) {
                    getCityListFromNet()
                } else {
                    getCityCode(data)
                }
            }
        } else getCityListFromNet()
    }

    /**
     * 获取城市列表
     */
    private fun getCityListFromNet() {
        AndroidNetworking.post(URL_PROXY_IP)
                .setContentType(DATA_TYPE)
                .addBodyParameter(POST_PARAM_METHOD, "getCity")
                .addBodyParameter(POST_PARAM_IMEI, mImei)
                .addBodyParameter(POST_PARAM_PLATFORMID, "2")   //Android:2 IOS:1
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.run {
                            val strResult = toString()
                            L.i("threadID = ${ThreadUtils.isMainThread()} \ngetCityListFromNet result: $strResult")
                            //保存城市列表，隔一天再重新获取
                            SPUtils.getInstance(SP_CITY_LIST).apply {
                                put(KEY_CITY_DATA, strResult)
                                put(
                                        KEY_CITY_GET_DATE,
                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) })
                            }
                            getCityCode(strResult)
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
    private fun getCityCode(cityData: String) {
        L.i("cityName: $mCityName cityData string: $cityData ")
        ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
            override fun doInBackground(): String {
                var result: String = ""

                val cityListBean = Gson().fromJson(cityData, CityListBean::class.java)
                if (cityListBean.code == 0) {   //获取数据成功
                    val provinceList = cityListBean.data.cityList   //省
                    for (province in provinceList) {
                        for (city in province.data) {
//                            L.i("cityName: ${city.name} cityCode: ${city.cityid}")
                            if (mCityName == city.name) {
                                result = city.cityid
                                L.i("target cityName: $mCityName cityID: $result")
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
                    requestPorts(result)
                } else {
                    L.i("$mCityName 该城市没有IP，重新获取地址")
                    //responProxyData(null)
                    responFailed("$mCityName 该城市没有IP")
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
                "params:\nmethod:getPort\nnumber:1\narea:$finalCityId\nimei:$mImei\n" +
                        "platformId: 2"
        )

        AndroidNetworking.post(URL_PROXY_IP)
                .setContentType(DATA_TYPE)
                .addBodyParameter(POST_PARAM_METHOD, "getPort")
                .addBodyParameter(POST_PARAM_PLATFORMID, "2")
                .addBodyParameter(POST_PARAM_AREA, finalCityId)
                .addBodyParameter(POST_PARAM_IMEI, mImei)
                .addBodyParameter(POST_PARAM_NUMBER, mPortCount.toString())
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.toString()?.let {
                            L.i("request ports result: $it")
                            val proxyIpBean = Gson().fromJson(it, ProxyIPBean::class.java)
                            if (proxyIpBean?.data?.code == 200) {
                                //保存已申请的端口
                                SPUtils.getInstance(SP_IP_PORTS).apply {
                                    put(KEY_IP_DATA, it)
                                    put(KEY_IP, proxyIpBean.data.dip[0].toString())
                                    put(KEY_CUR_PORT, proxyIpBean.data.port[0].toString())
                                }
                                responProxyData(proxyIpBean)
                            } else {  //重新请求
                                L.i("请求数据出错：code = ${proxyIpBean?.data?.code}")
                                responFailed(proxyIpBean?.msg)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        L.i("申请端口失败：${anError?.errorDetail}")
                        //responProxyData(null)
                        responFailed(anError?.errorDetail)
                    }
                })
    }

    private fun responProxyData(proxyIPBean: ProxyIPBean?) {
        mProxyDataListener?.onResponProxyData(proxyIPBean)
    }

    private fun responFailed(failedMsg: String?) {
        mProxyDataListener?.onFailed("请求代理数据异常:$failedMsg")
    }
}