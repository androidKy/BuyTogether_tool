package com.buy.together.fragment.viewmodel

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.accessibility.service.data.CommentBean
import com.accessibility.service.data.TaskBean
import com.accessibility.service.function.ClearDataService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.CommentStatus
import com.accessibility.service.util.Constant
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.buy.together.R
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.bean.CityListBean
import com.buy.together.bean.CloseProxyBean
import com.buy.together.bean.ProxyIPBean
import com.buy.together.fragment.view.MainView
import com.buy.together.hook.sp.DeviceParams
import com.buy.together.utils.ParseDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.*
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainViewModel(val context: Context, val mainView: MainView) :
    BaseViewModel<Context, BaseView>() {

    private val mSubscribeList = ArrayList<Disposable>()
    private var mIsCommentTask = false
    //private var mIsFromCache = false

    /**
     * 获取任务
     */
    fun getTask(isCommentTask: Boolean) {
        mIsCommentTask = isCommentTask
        L.init(MainViewModel::class.java.simpleName)
        SPUtils.getInstance(Constant.SP_DEVICE_PARAMS).clear(true)

        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).run {
            val cacheTaskData = getString(Constant.KEY_TASK_DATA, "")
            if (!TextUtils.isEmpty(cacheTaskData)) {
                //val afterUnicode = UnicodeUtils.decodeUnicode(cacheTaskData)
                L.i("从缓存获取任务：$cacheTaskData")
                //mIsFromCache = true
                parseTaskData(cacheTaskData)
            } else {
                // mIsFromCache = false
                L.i("从服务器获取任务")
                startGetTask()
            }
        }

        /* val taskStatus = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getInt(Constant.KEY_TASK_STATUS, -1)
         val taskIp = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getInt(Constant.KEY_TASK_ID, 0)
         L.i("taskStatus: $taskStatus taskIP:$taskIp")
         if (taskStatus == 0) {  //未完成的任务必须上报未完成，才能请求到接下来的任务
             ApiManager()
                 .setDataListener(object : DataListener {
                     override fun onSucceed(result: String) {
                         startGetTask()
                     }

                     override fun onFailed(errorMsg: String) {
                         L.i("网络连接错误，上报任务状态失败：$errorMsg")
                         mainView.onFailed("网络连接错误")
                     }
                 })
                 .updateTaskStatus(taskIp.toString(), false, "任务中断，未知错误")
         } else startGetTask()*/
    }

    private fun startGetTask() {
        if (mIsCommentTask) {
            L.i("获取评论任务")
            ApiManager()
                .setDataListener(object : DataListener {
                    override fun onSucceed(result: String) {
                        parseTaskData(result)
                    }

                    override fun onFailed(errorMsg: String) {
                        mainView.onFailed(errorMsg)
                    }
                })
                .getCommentTask()
        } else {
            L.i("获取正常任务")
            val imei = SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
                .getString(Constant.KEY_REAL_DEVICE_IMEI)
            //  val imei = "866187037596234"
            L.i("真实imei：$imei")
            ApiManager()
                .setDataListener(object : DataListener {
                    override fun onSucceed(result: String) {
                        //保存服务器下发的taskId，用于判断任务是否超时
                        parseTaskData(result)
                    }

                    override fun onFailed(errorMsg: String) {
                        mainView.onFailed(errorMsg)
                    }
                })
                .getNormalTask(imei)
        }

    }

    private fun parseTaskData(result: String) {
        val disposable = Observable.just(result)
            .flatMap { flatIt ->
                val taskBean =
                    if (mIsCommentTask)
                        parseCommentTask(flatIt)
                    else parseNormalTask(flatIt)
                Observable.just(taskBean)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { resultIt ->
                mainView.onResponTask(resultIt)
            }
        mSubscribeList.add(disposable)
    }

    /**
     * 解析正常任务
     */
    private fun parseNormalTask(result: String): TaskBean {
        L.i("解析正常任务")
        return try {
            val code = JSONObject(result).getInt("code")
            if (code == 200)
                Gson().fromJson(result, TaskBean::class.java)
            else {
                val taskBean = TaskBean()
                taskBean.code = code
                taskBean.msg = JSONObject(result).getString("msg")

                taskBean
            }
        } catch (e: Exception) {
            L.e(e.message)
            val exceptionTask = TaskBean()
            exceptionTask.msg = e.message
            exceptionTask.code = 400
            exceptionTask
        }
    }

    /**
     * 解析评论任务
     */
    private fun parseCommentTask(result: String): TaskBean {
        L.i("解析评论任务")
        var taskBean = TaskBean()
        try {
            val code = JSONObject(result).getInt("code")
            if (code == 200) {
                val commentBean = Gson().fromJson(result, CommentBean::class.java)
                taskBean = ParseDataUtil.parseCommentBean2TaskBean(commentBean)
            } else {
                taskBean.code = code
                taskBean.msg = JSONObject(result).getString("msg")
            }
        } catch (e: Exception) {
            L.e(e.message, e)
            taskBean.msg = e.message
            taskBean.code = 400
        }

        return taskBean
    }

    /**
     * 保存数据到SP
     */
    private fun saveData(taskBean: TaskBean) {
        /* taskBean.task.account.id = 3234
         taskBean.task.account.user = "210289767"
         taskBean.task.account.pwd="gx95k1g8ra"*/

        saveAlipayAccountSwitch(taskBean)
        saveTaskData2SP(Gson().toJson(taskBean))
        saveUploadParams(taskBean)
        saveDeviceParams(taskBean)
        //保存任务状态,此时任务开始，未完成
        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).apply {
            put(Constant.KEY_TASK_TYPE, mIsCommentTask)
        }
    }

    /**
     * 解析数据为键值对，显示到表格上
     */
    fun parseTask(taskBean: TaskBean) {
        val subscribe = Observable.just(taskBean)
            .flatMap {
                saveData(it)

                val datas = try {
                    val hashMapData =
                        if (!mIsCommentTask) ParseDataUtil.parseTaskBean2HashMap(it)
                        else ParseDataUtil.parseCommentTask2HashMap(it)

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
     * 清理PDD和QQ的数据
     */
    fun clearData() {
        var isClearAlipay =
            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                .getBoolean(Constant.KEY_ALIPAY_ACCOUNT_SWITCH, true)
        isClearAlipay = false
        ClearDataService().clearData(isClearAlipay, object : TaskListener {
            override fun onTaskFinished() {
                mainView.onClearDataResult("Success")
            }

            override fun onTaskFailed(failedMsg: String) {
                L.i("清理数据失败: $failedMsg")
                mainView.onClearDataResult(failedMsg)
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
            .addBodyParameter(Constant.POST_PARAM_IMEI, DevicesUtil.getIMEI(context))
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, 2.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.run {
                        val strResult = toString()
                        L.i("threadID = ${ThreadUtils.isMainThread()} \ngetCityListFromNet result: $strResult")
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
                        mainView.onRequestPortsResult(portsCache)
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
                                //上报IP信息
                                uploadIpInfo(proxyIpBean)

                                //保存已申请的端口
                                SPUtils.getInstance(Constant.SP_IP_PORTS).apply {
                                    put(Constant.KEY_IP_PORTS, this@run)
                                    put(Constant.KEY_CUR_PORT, proxyIpBean.data.port[0].toString())
                                }
                                mainView.onRequestPortsResult(this)
                            }
                            proxyIpBean?.data?.code == 203 -> {
                                L.i("请求代理数据出错：code = 203")
                                mainView.onResponPortsFailed("请求端口数据出错：code = ${proxyIpBean.data?.code}")
                            }
                            else -> {  //重新请求
                                L.i("请求代理数据出错：code = ${proxyIpBean?.data?.code}")
                                mainView.onResponPortsFailed("请求端口数据出错：code = ${proxyIpBean?.data?.code}")
                            }
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
     * 上报城市没有该IP
     */
    private fun uploadIpError(cityName: String) {
        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).apply {
            val taskId = getInt(Constant.KEY_TASK_ID, 0)

            if (!mIsCommentTask)
            /* ApiManager()
                 .setDataListener(object : DataListener {
                     override fun onSucceed(result: String) {
                         getTask(mIsCommentTask)
                     }

                     override fun onFailed(errorMsg: String) {
                         mainView.onFailed(errorMsg)
                     }
                 })
                 .updateTaskStatus(taskId.toString(), false, "$cityName 没有相应的代理IP")*/
            else {
                ApiManager()
                    .setDataListener(object : DataListener {
                        override fun onSucceed(result: String) {
                            getTask(mIsCommentTask)
                        }

                        override fun onFailed(errorMsg: String) {
                            mainView.onFailed(errorMsg)
                        }
                    })
                    .updateCommentTaskStatus(taskId, CommentStatus.COMMENT_MISSION_FAILED, "$cityName 没有相应的代理IP")
            }
        }
    }

    /**
     * 保存支付宝账号,确定支付宝账号是否切换
     */
    private fun saveAlipayAccountSwitch(taskBean: TaskBean) {
        taskBean.task?.run {
            pay_account?.username?.apply {
                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).let {

                    val lastAlipayAccount = it.getString(Constant.KEY_ALIPAY_ACCOUNT)

                    if (this@apply == lastAlipayAccount) {
                        it.put(Constant.KEY_ALIPAY_ACCOUNT_SWITCH, true)
                    } else it.put(Constant.KEY_ALIPAY_ACCOUNT_SWITCH, true)
                }
            }
        }
    }

    /**
     * 保存任务数据到SP
     */
    private fun saveTaskData2SP(strData: String) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)

        spUtils.put(Constant.KEY_TASK_DATA, strData, true)
    }

    /**
     * 保存请求接口需要上传的参数
     */
    private fun saveUploadParams(taskBean: TaskBean) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
        taskBean.task?.apply {
            spUtils.apply {
                L.i("保存的任务ID：$task_id")
                put(Constant.KEY_TASK_ID, task_id)

                account?.let {
                    put(Constant.KEY_ACCOUNT_ID, it.id, true)
                }
            }
        }
    }

    /**
     * 保存设备参数
     */
    private fun saveDeviceParams(taskBean: TaskBean) {
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
     * 执行一个定时器去定时获取任务
     *
     */
    fun startTaskTimer(isCommentTask: Boolean) {
        TimerUtils.instance.start(object : TimerTask() {
            override fun run() {
                getTask(isCommentTask)
            }
        }, 18 * 1 * 1000L)
    }

    fun stopTaskTimer() {
        TimerUtils.instance.stop()
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
}