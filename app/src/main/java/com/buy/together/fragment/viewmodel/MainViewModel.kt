package com.buy.together.fragment.viewmodel

import android.content.Context
import android.text.TextUtils
import com.accessibility.service.data.CommentBean
import com.accessibility.service.data.ConfirmSignedBean
import com.accessibility.service.data.TaskBean
import com.accessibility.service.data.TaskCategory
import com.accessibility.service.function.ClearDataService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.PackageManagerUtils
import com.buy.together.BuildConfig
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.fragment.view.MainView
import com.buy.together.utils.ParseDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.tinkerpatch.sdk.TinkerPatch
import com.utils.common.SPUtils
import com.utils.common.TimerUtils
import com.utils.common.ToastUtils
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
class MainViewModel(context: Context, val mainView: MainView) :
    BaseViewModel<Context, BaseView>(context, mainView) {

    private val mSubscribeList = ArrayList<Disposable>()

    /**
     * 获取任务
     */
    fun getTask() {
        L.init(MainViewModel::class.java.simpleName)
        SPUtils.getInstance(Constant.SP_DEVICE_PARAMS).clear(true)

        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).run {
            val cacheTaskData = getString(Constant.KEY_TASK_DATA, "")
            if (!TextUtils.isEmpty(cacheTaskData)) {
                //val afterUnicode = UnicodeUtils.decodeUnicode(cacheTaskData)
                L.i("从缓存获取任务：$cacheTaskData")
                //mIsFromCache = true
                parseTaskData(cacheTaskData,false)
            } else {
                // mIsFromCache = false
                L.i("从服务器获取任务")
                startGetTask()
            }
        }
    }

    private fun startGetTask() {
        val imei = SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
            .getString(Constant.KEY_REAL_DEVICE_IMEI)
        //  val imei = "866187037596234"
        L.i("真实imei：$imei")
        when (BuildConfig.taskType) {
            TaskCategory.NORMAL_TASK -> getNormalTask(imei)
            TaskCategory.COMMENT_TASK -> getCommentTask(imei)
            TaskCategory.CONFIRM_SIGNED_TASK -> getConfirmSignedTask(imei)
        }
    }

    /**
     * 获取正常任务
     */
    private fun getNormalTask(imei: String) {
        ApiManager()
            .setDataListener(object : DataListener {
                override fun onSucceed(result: String) {
                    parseTaskData(result,true)
                }

                override fun onFailed(errorMsg: String) {
                    mainView.onFailed(errorMsg)
                }
            })
            .getNormalTask(imei)
    }


    /**
     * 获取评论任务
     */
    private fun getCommentTask(imei: String) {
        ApiManager()
            .setDataListener(object : DataListener {
                override fun onSucceed(result: String) {
                    //保存服务器下发的taskId，用于判断任务是否超时
                    parseTaskData(result,true)
                }

                override fun onFailed(errorMsg: String) {
                    mainView.onFailed(errorMsg)
                }
            })
            .getCommentTask(imei)
    }

    /**
     * 获取确认收货评论
     */
    private fun getConfirmSignedTask(imei: String) {
        ApiManager()
            .setDataListener(object : DataListener {
                override fun onSucceed(result: String) {
                    //保存服务器下发的taskId，用于判断任务是否超时
                    parseTaskData(result,true)
                }

                override fun onFailed(errorMsg: String) {
                    mainView.onFailed(errorMsg)
                }
            })
            .getConfrimSignedTask(imei)
    }


    private fun saveGetTaskTime() {
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).run { format(Date()) }
        L.i("保存从服务器获取任务的时间:$currentDate")
        SPUtils.getInstance(Constant.SP_TASK_TIME_OUT).put(Constant.KEY_TASK_TIMEOUT,currentDate)
    }

    /**
     * 解析获取的任务数据
     */
    private fun parseTaskData(result: String,isFromServer:Boolean) {
        val disposable = Observable.just(result)
            .flatMap { flatIt ->
                val taskBean = when (BuildConfig.taskType) {
                    TaskCategory.NORMAL_TASK -> parseNormalTask(flatIt)
                    else -> parseCommentTask(flatIt)
                }
                taskBean.task?.task_category = BuildConfig.taskType
                val taskId = taskBean.task?.task_id
                if(isFromServer && taskId != null && taskId >0) //判断是否从服务器获取到可以做的任务
                {
                    saveGetTaskTime()
                }
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

    private fun parseConfirmTask(result:String):TaskBean{
        var taskBean = TaskBean()
        try {
            val code = JSONObject(result).getInt("code")
            if (code == 200) {
                val confirmBean = Gson().fromJson(result, ConfirmSignedBean::class.java)
                taskBean = ParseDataUtil.parseConfirmBean2TaskBean(confirmBean)
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
    }

    /**
     * 解析数据为键值对，显示到表格上
     */
    fun parseTask(taskBean: TaskBean) {
        val subscribe = Observable.just(taskBean)
            .flatMap {
                saveData(it)

                val datas = try {
                    val hashMapData = when (BuildConfig.taskType) {
                        TaskCategory.NORMAL_TASK -> ParseDataUtil.parseTaskBean2HashMap(it)
                        TaskCategory.COMMENT_TASK -> ParseDataUtil.parseCommentTask2HashMap(it)
                        else -> ParseDataUtil.parseConfirmSignedTask2HashMap(it)
                    }

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
        ClearDataService().clearData(false, object : TaskListener {
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
     * 执行一个定时器去定时获取任务
     *
     */
    fun startTaskTimer() {
        TimerUtils.instance.start(object : TimerTask() {
            override fun run() {
                getTask()
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
     * 检查是否有更新
     */
    fun checkUpdate() {
        L.i("当前补丁版本号: ${TinkerPatch.with().patchVersion}")
        TinkerPatch.with()
            .setPatchResultCallback { patchResult ->
                val result = patchResult.isSuccess
                val resultStr = if (patchResult.isSuccess) "成功" else "失败"
                L.i("热更新结果: $resultStr 当前补丁版本号: ${TinkerPatch.with().patchVersion}")
                ToastUtils.showToast(context, "热更新：$resultStr")
                //todo 上报热更新结果给后台
                if (result) {
                    PackageManagerUtils.restartSelf(Constant.PKG_NAME)
                }
            }
            .fetchPatchUpdate(true)
        mainView.onResponVersionUpdate()
    }
}