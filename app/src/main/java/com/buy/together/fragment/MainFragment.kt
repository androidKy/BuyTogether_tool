package com.buy.together.fragment

import android.content.Intent
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.TextView
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.data.TaskBean
import com.accessibility.service.data.TaskCategory
import com.accessibility.service.util.Constant
import com.accessibility.service.util.PackageManagerUtils
import com.buy.together.BuildConfig
import com.buy.together.R
import com.buy.together.base.BaseFragment
import com.buy.together.fragment.view.MainView
import com.buy.together.fragment.viewmodel.MainViewModel
import com.proxy.droid.ProxyManager
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.TimeUtils
import com.utils.common.TimerUtils
import com.utils.common.ToastUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainFragment : BaseFragment(), MainView {

    private var mTableDatas = ArrayList<ArrayList<String>>()
    private var mTaskBean: TaskBean? = null
    private var mContainer: FrameLayout? = null
    private var mViewModel: MainViewModel? = null
    private var mVpnFailedConnectCount: Int = 0 //VPN连接失败次数
    private var mIsResumed: Boolean = false  //Fragment是否被创建
    private var mIsCommentTask: Boolean = false  //是否是评论任务
    private var mTVtaskType: TextView? = null

    init {
        mIsCommentTask = BuildConfig.taskType == TaskCategory.COMMENT_TASK
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        L.init(MainFragment::class.java.simpleName)
        mContainer = mRootView?.findViewById<FrameLayout>(R.id.fl_container)
        mTVtaskType = mRootView?.findViewById(R.id.tv_taskType)

        initDisplayOpinion()

        context?.apply {
            mViewModel = MainViewModel(this, this@MainFragment)
        }

        when (BuildConfig.taskType) {
            TaskCategory.NORMAL_TASK -> mTVtaskType?.text = "正常任务"
            TaskCategory.COMMENT_TASK -> mTVtaskType?.text = "评论任务"
            TaskCategory.CONFIRM_SIGNED_TASK -> mTVtaskType?.text = "确认收货任务"
        }
    }

    override fun onStart() {
        super.onStart()
        mIsResumed = false
    }

    override fun onResume() {
        super.onResume()
        mIsResumed = true
    }

    /**
     * 开始任务
     */
    @Synchronized
    fun startTask() {
        if (mIsResumed) {
            mVpnFailedConnectCount = 0
            activity?.apply {
                //mIsCommentTask = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_TASK_TYPE)
                //开始任务之前去检测是否有更新
                mViewModel?.checkUpdate()
            }
        } else {
            L.i("onResume()还没执行")
            mContainer?.postDelayed({
                startTask()
            }, 1000)
        }
    }

    override fun onResponVersionUpdate() {
        //LocalVpnManager.getInstance().stopVpnService(activity)
        mViewModel?.getTask()
    }

    override fun onResponTask(taskBean: TaskBean) {
        when {
            taskBean.code == 200 -> {
                mContainer?.removeAllViews()
                mViewModel?.stopTaskTimer()
                mTaskBean = taskBean
                mViewModel?.parseTask(taskBean)
            }
            taskBean.code == 201 -> //没有待领取的任务，启动一个定时器去定时获取
            {
                //  mIsCommentTask = !mIsCommentTask
                mViewModel?.stopTaskTimer()
                mViewModel?.startTaskTimer()
                mViewModel?.showTip(mContainer, "没有待领取的任务")
            }
            else -> {
                L.i("获取数据失败：${taskBean.msg}")
                context?.run {
                    mViewModel?.showTip(mContainer, "获取数据失败：${taskBean.msg}")
                    ToastUtils.showToast(this, "获取数据失败：${taskBean.msg}")

                    mViewModel?.stopTaskTimer()
                    mViewModel?.startTaskTimer()
                }
            }
        }
    }


    override fun onParseDatas(taskData: ArrayList<ArrayList<String>>) {
        mTableDatas.clear()
        initHeader(mTableDatas)
        mTableDatas.addAll(taskData)

        initTableView(mContainer, mTableDatas)

        mViewModel?.clearData()
    }

    override fun onFailed(msg: String?) {
        L.i("获取任务失败：$msg")
        mViewModel?.stopTaskTimer()
        mViewModel?.startTaskTimer()
        context?.run {
            if (!msg.isNullOrEmpty()) {
                ToastUtils.showToast(this, msg)
                mViewModel?.showTip(mContainer, msg)
            }
        }
    }

    /**
     * 拼多多和QQ的缓存数据清理完毕
     */
    override fun onClearDataResult(result: String) {
        if (result == "Success") {
            startProxy()
        } else {
            context?.apply {
                mViewModel?.showTip(mContainer, "应用未获得root权限")
                ToastUtils.showToast(this, "应用未获得root权限")
            }
        }
    }

    private fun startProxy() {
        val imei = SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
            .getString(Constant.KEY_REAL_DEVICE_IMEI)
        if (TextUtils.isEmpty(imei)) {
            mViewModel?.showTip(mContainer, "获取真实IMEI失败")
            return
        }
        mTaskBean?.let {
            it.task?.delivery_address?.city?.apply {
                ProxyManager.Builder()
                    .setContext(context)
                    .setCityName(this)
                    .setImei(imei)
                    .setProxyStatusListener { status, msg ->
                        L.i("代理状态：$status Msg:$msg")
                        if (status)
                            startPdd()
                        else mViewModel?.showTip(mContainer, msg)
                    }
                    .build()
                    .startProxy()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.clearSubscribes()
        TimerUtils.instance.stop()
    }

    /**
     * 启动拼多多开始任务
     */
    private fun startPdd() {
        //展示弹框
        mContainer?.postDelayed({
            this@MainFragment.context?.sendBroadcast(Intent(MyAccessibilityService.ACTION_TASK_STATUS))

            val launchIntentForPackage =
                context?.packageManager?.getLaunchIntentForPackage(Constant.BUY_TOGETHER_PKG)
            if (launchIntentForPackage != null) {
                startActivity(launchIntentForPackage)
                //一个任务超时时间，超过一定时间后，根据是否还在做同一个任务，强制刷单APP重启
                TimerUtils.instance.start(object : TimerTask() {
                    override fun run() {
                        val startTaskTime = SPUtils.getInstance(Constant.SP_TASK_TIME_OUT)
                            .getString(Constant.KEY_TASK_TIMEOUT)
                        val currentDate =
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.CHINA
                            ).run { format(Date()) }
                        L.i("当前任务时间: $currentDate 从服务器获取时间：$startTaskTime")
                        if (TimeUtils.getMinutes(currentDate, startTaskTime) > 8) {
                            //startTask()
                            L.i("任务已超时，即将重启自身")
                            PackageManagerUtils.restartSelf(Constant.PKG_NAME)
                        } else {
                            L.i("任务未超时")
                        }
                    }
                }, 60 * 1000 * 10)
            } else {
                context?.run {
                    ToastUtils.showToast(this, "未安装拼多多")
                }
            }
        }, 5000)
    }

}