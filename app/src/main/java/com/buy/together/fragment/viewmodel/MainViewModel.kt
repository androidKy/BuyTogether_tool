package com.buy.together.fragment.viewmodel

import android.content.Context
import com.accessibility.service.function.ClearDataService
import com.accessibility.service.listener.TaskListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.bean.TaskBean
import com.buy.together.fragment.view.MainView
import com.buy.together.utils.Constant
import com.buy.together.utils.ParseDataUtil
import com.buy.together.utils.TestData
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.DevicesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.goldze.mvvmhabit.utils.SPUtils
import org.json.JSONObject

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

        val disposable = Observable.just(TestData.taskBean_str)
            .flatMap {
                val taskBean = try {
                    Gson().fromJson(it, TaskBean::class.java)
                } catch (e: Exception) {
                    L.e(e.message)
                    val exceptionTask = TaskBean()
                    exceptionTask.msg = e.message
                    exceptionTask.code = 400
                    exceptionTask
                }
                saveTaskData2SP(it)
                Observable.just(taskBean)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mainView.onResponTask(it)
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
        //匹配相应的城市
        val city = ""

        getCity()
    }

    fun getCity() {
        AndroidNetworking.post(Constant.URL_PROXY_IP)
            .setContentType("multipart/form-data")
            .addBodyParameter(Constant.POST_PARAM_METHOD, "getCity")
            .addBodyParameter(Constant.POST_PARAM_IMEI, DevicesUtil.getIMEI(context))
            .addBodyParameter(Constant.POST_PARAM_PLATFORMID, 2.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    L.i("getCity result: $response")
                }

                override fun onError(anError: ANError?) {
                    L.e("getCity error: ${anError?.errorDetail}")
                }

            })
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
     * 保存任务数据到SP
     */
    private fun saveTaskData2SP(strData: String) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)

        spUtils.put(Constant.KEY_TASK_DATA, strData)
    }
}