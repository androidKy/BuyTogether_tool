package com.buy.together.fragment.viewmodel

import android.content.Context
import android.text.TextUtils
import com.accessibility.service.function.ClearDataService
import com.accessibility.service.listener.TaskListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.bean.CityListBean
import com.buy.together.bean.CloseProxyBean
import com.buy.together.bean.ProxyIPBean
import com.buy.together.bean.TaskBean
import com.buy.together.fragment.view.MainView
import com.buy.together.hook.sp.DeviceParams
import com.buy.together.utils.Constant
import com.buy.together.utils.ParseDataUtil
import com.buy.together.utils.TestData
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.DevicesUtil
import com.utils.common.ThreadUtils
import com.utils.common.TimeUtils
import com.utils.common.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.goldze.mvvmhabit.utils.SPUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainViewModel(val context: Context, val mainView: MainView) : BaseViewModel<Context, BaseView>() {

    private val mSubscribeList = ArrayList<Disposable>()

    /**
     * 获取任务
     */
    fun getTask() {
        L.init(MainViewModel::class.java.simpleName)

        /* AndroidNetworking.get(Constants.URL_GET_TASK)
             .build()
             .getAsOkHttpResponse(object : OkHttpResponseListener {
                 override fun onResponse(response: Response?) {
                     response?.body()?.string()?.let {
                         L.i("返回任务：$it")
                         parseStringForTask(it)
                     }
                 }

                 override fun onError(anError: ANError?) {
                     anError?.apply {
                         L.e(message, this)
                         mainView.onFailed(message)
                     }
                 }
             })*/
        parseStringForTask(TestData.taskBean_str)
    }

    private fun parseStringForTask(result: String) {
        val disposable = Observable.just(result)
            .flatMap { flatIt ->
                val taskBean = try {
                    Gson().fromJson(flatIt, TaskBean::class.java)
                } catch (e: Exception) {
                    L.e(e.message)
                    val exceptionTask = TaskBean()
                    exceptionTask.msg = e.message
                    exceptionTask.code = 400
                    exceptionTask
                }
                saveTaskData2SP(flatIt)
                saveDeviceParams(taskBean)
                Observable.just(taskBean)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { resultIt ->
                mainView.onResponTask(resultIt)
            }
        mSubscribeList.add(disposable)
    }

    /**
     * 解析数据为键值对，显示到表格上
     */
    fun parseTask(taskBean: TaskBean) {
        val subscribe = Observable.just(taskBean)
            .flatMap {
                val datas = try {
                    val hashMapData = ParseDataUtil.parseTaskBean2HashMap(it)

                    ParseDataUtil.parseHashMap2ArrayList(hashMapData).reversed()
                } catch (e: Exception) {
                    L.e(e.message)
                    ArrayList<ArrayList<String>>()
                }

                val arrayList = ArrayList<ArrayList<String>>()
                arrayList.addAll(datas)
                Observable.just(arrayList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mainView.onParseDatas(it)
            }

        mSubscribeList.add(subscribe)
    }

    /**
     * 清理数据
     */
    fun clearData() {
        ClearDataService().clearData(object : TaskListener {
            override fun onTaskFinished() {
                mainView.onClearDataResult("Success")
            }

            override fun onTaskFailed(failedText: String) {
                L.i("清理数据失败: $failedText")
                mainView.onClearDataResult("failed")
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
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) }
        L.i("currentDate: $currentDate")
        val lastGetCityDate = SPUtils.getInstance(Constant.SP_CITY_LIST).getString(Constant.KEY_CITY_GET_DATE)
        if (!TextUtils.isEmpty(lastGetCityDate)) {
            if (TimeUtils.getDays(currentDate, lastGetCityDate) >= 1) { //对比当前时间与上次获取的时间
                //重新获取
                L.i("间隔一天，重新获取城市ID")
                getCityListFromNet(cityName)
            } else {
                L.i("从缓存获取城市ID")
                val data = SPUtils.getInstance(Constant.SP_CITY_LIST).getString(Constant.KEY_CITY_DATA)
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
            .addBodyParameter(Constant.POST_PARAM_IMEI, DevicesUtil.getIMEI(context))
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, 2.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.run {
                        val strResult = toString()
                        L.i("threadID = ${ThreadUtils.isMainThread()} \ngetCityListFromNet result: $strResult")
                        //保存城市列表，隔一天再重新获取
                        SPUtils.getInstance(Constant.SP_CITY_LIST).put(Constant.KEY_CITY_DATA, strResult)
                        SPUtils.getInstance(Constant.SP_CITY_LIST).put(
                            Constant.KEY_CITY_GET_DATE,
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) })
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
        L.i("cityData string: $cityData cityName: $cityName")
        ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
            override fun doInBackground(): String {
                var result: String = ""

                val cityListBean = Gson().fromJson(cityData, CityListBean::class.java)
                if (cityListBean.code == 0) {   //获取数据成功
                    val provinceList = cityListBean.data.cityList   //省
                    for (province in provinceList) {
                        for (city in province.data) {
                            L.i("cityName: ${city.name} cityCode: ${city.cityid}")
                            if (cityName == city.name) {
                                result = city.cityid
                                L.i("target cityName: $cityName cityID: $result")
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
                    val portsCache = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_IP_PORTS)
                    if (TextUtils.isEmpty(portsCache))
                        requestPorts(result)
                    else {
                        L.i("cache ports: $portsCache")
                        mainView.onRequestPortsResult(portsCache)
                    }
                } else {
                    L.i("city id == null")
                    //todo 该城市没有IP，重新获取地址
                    ToastUtils.showToast(context, "city id == null")
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
            "params:\nmethod: getPort\n number: 1\n area:$finalCityId\n imei:${DevicesUtil.getIMEI(context)}\n" +
                    "platformId: 2"
        )

        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType(Constant.CONTENT_TYPE)
            .addBodyParameter(Constant.POST_PARAM_METHOD, "getPort")
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, "2")
            .addBodyParameter(Constant.POST_PARAM_AREA, finalCityId)
            .addBodyParameter(Constant.POST_PARAM_IMEI, DevicesUtil.getIMEI(context))
            .addBodyParameter(Constant.POST_PARAM_NUMBER, 1.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.toString()?.run {
                        L.i("request ports result: $this")
                        val proxyIpBean = Gson().fromJson(this, ProxyIPBean::class.java)
                        if (proxyIpBean?.data?.code == 200) {
                            //保存已申请的端口
                            SPUtils.getInstance(Constant.SP_IP_PORTS).put(Constant.KEY_IP_PORTS, this)
                            SPUtils.getInstance(Constant.SP_IP_PORTS)
                                .put(Constant.KEY_CUR_PORT, proxyIpBean.data.port[0].toString())
                            mainView.onRequestPortsResult(this)
                        } else {  //重新请求
                            L.i("请求数据出错：code = ${proxyIpBean?.data?.code}")
                            mainView.onResponPortsFailed("请求数据出错：code = ${proxyIpBean?.data?.code}")
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    L.i("申请端口失败：${anError?.errorDetail}")
                    mainView.onResponPortsFailed("申请端口失败：${anError?.errorDetail}")
                }
            })
    }

    /**
     * 关闭端口
     */
    fun closePort(curPort: String) {
        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType(Constant.CONTENT_TYPE)
            .addBodyParameter(Constant.POST_PARAM_METHOD, "closePort")
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, "2")
            .addBodyParameter(Constant.POST_PARAM_PORT, curPort)
            .addBodyParameter(Constant.POST_PARAM_IMEI, DevicesUtil.getIMEI(context))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.toString()?.run {
                        L.i("关闭端口成功：$this")
                        SPUtils.getInstance(Constant.SP_IP_PORTS).clear()
                        val closeProxyBean = Gson().fromJson(this, CloseProxyBean::class.java)
                        mainView.onResponClosePort(closeProxyBean)
                    }
                }

                override fun onError(anError: ANError?) {
                    L.i("关闭端口失败：${anError?.response?.body()?.string()} errorMsg: ${anError?.errorDetail}")
                    mainView.onResponClosePort(null)
                }

            })
    }

    /**
     * 检查VPN是否连接成功
     */
    fun checkVpnConnected() {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                var getIpUrl = "http://2019.ip138.com"
                try {
                    getIpUrl = Gson().fromJson(
                        SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_IP_PORTS),
                        ProxyIPBean::class.java
                    ).data.getIpUrl
                } catch (e: Exception) {
                    L.e(e.message, e)
                }
                return true
            }

            override fun onSuccess(result: Boolean?) {
                L.i("vpn connect result: $result")
                mainView.onResponVpnResult(result!!)
            }

            override fun onCancel() {
                mainView.onResponVpnResult(false)
            }

            override fun onFail(t: Throwable?) {
                mainView.onResponVpnResult(false)
            }

        })
    }


    /**
     * 保存任务数据到SP
     */
    private fun saveTaskData2SP(strData: String) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)

        spUtils.put(Constant.KEY_TASK_DATA, strData)
    }

    /**
     * 保存设备参数
     */
    private fun saveDeviceParams(taskBean: TaskBean) {
        val spUtils = SPUtils.getInstance(Constant.SP_DEVICE_PARAMS)
        taskBean.task?.device?.run {
            spUtils.apply {
                put(DeviceParams.IMEI_KEY, imei)
                put(DeviceParams.IMSI_KEY, imsi)
                put(DeviceParams.MAC_KEY, mac)
                put(DeviceParams.USER_AGENT_KEY, useragent)
                put(DeviceParams.BRAND_KEY, brand)
                put(DeviceParams.MODEL_KEY, model)
                put(DeviceParams.SDK_KEY, android)
                put(DeviceParams.SYSTEM_KEY, system)
            }
        }

    }

    /**
     * 释放订阅
     */
    override fun clearSubscribes() {
        if (mSubscribeList.size > 0) {
            for (sub in mSubscribeList) {
                sub.dispose()
            }
        }
    }
}