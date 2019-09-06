package com.buy.together.fragment

import android.text.TextUtils
import android.widget.FrameLayout
import com.accessibility.service.data.TaskBean
import com.accessibility.service.util.Constant
import com.buy.together.R
import com.buy.together.base.BaseFragment
import com.buy.together.bean.CloseProxyBean
import com.buy.together.bean.ProxyIPBean
import com.buy.together.fragment.view.MainView
import com.buy.together.fragment.viewmodel.MainViewModel
import com.google.gson.Gson
import com.proxy.service.LocalVpnManager
import com.proxy.service.LocalVpnService
import com.rmondjone.locktableview.DisplayUtil
import com.rmondjone.locktableview.LockTableView
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.ThreadUtils
import com.utils.common.ToastUtils
import com.vilyever.socketclient.SocketClient


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainFragment : BaseFragment(), MainView, LocalVpnService.onStatusChangedListener {


    private var mTableDatas = ArrayList<ArrayList<String>>()
    private var mTaskBean: TaskBean? = null
    private var mContainer: FrameLayout? = null
    private var mViewModel: MainViewModel? = null
    private var mVpnFailedConnectCount: Int = 0 //VPN连接失败次数
    private var mIsResumed: Boolean = false  //Fragment是否被创建
    private var mIsCommentTask: Boolean = false  //是否是评论任务

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        L.init(MainFragment::class.java.simpleName)
        mContainer = mRootView?.findViewById<FrameLayout>(R.id.mainFragment_container)

        initDisplayOpinion()

        initHeader()

        mViewModel = MainViewModel(context!!, this)


        //initTableView(mTableDatas)
    }

    private fun initHeader() {
        val headerList = ArrayList<String>()

        headerList.add("KEY")
        headerList.add("VALUE")

        mTableDatas.add(headerList)
    }

    private fun initDisplayOpinion() {

        val dm = resources.displayMetrics
        DisplayUtil.density = dm.density
        DisplayUtil.densityDPI = dm.densityDpi
        DisplayUtil.screenWidthPx = dm.widthPixels
        DisplayUtil.screenhightPx = dm.heightPixels
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(context, dm.widthPixels.toFloat()).toFloat()
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(context, dm.heightPixels.toFloat()).toFloat()
    }

    private fun initTableView(tableDatas: ArrayList<ArrayList<String>>) {
        context?.run {
            mContainer?.apply {
                removeAllViews()
                LockTableView(this@run, this@apply, tableDatas).let {
                    it.setLockFristColumn(true) //是否锁定第一列
                        .setLockFristRow(true) //是否锁定第一行
                        .setMaxColumnWidth(300) //列最大宽度
                        .setMinColumnWidth(60) //列最小宽度
                        //.setColumnWidth(1, 30) //设置指定列文本宽度
                        //.setColumnWidth(2, 0)
                        .setMinRowHeight(20)//行最小高度
                        .setMaxRowHeight(50)//行最大高度
                        .setTextViewSize(16) //单元格字体大小
                        .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                        .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                        .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                        .setCellPadding(5)//设置单元格内边距(dp)
                        .show()
                }
            }
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
    fun startTask() {
        if (mIsResumed) {
            mVpnFailedConnectCount = 0
            activity?.apply {
                mIsCommentTask = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_TASK_TYPE)
                LocalVpnManager.getInstance().stopVpnService(activity)
                mViewModel?.getTask(mIsCommentTask)
            }
        } else {
            L.i("onResume()还没执行")
            mContainer?.postDelayed({
                startTask()
            }, 500)
        }
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
                mIsCommentTask = !mIsCommentTask
                mViewModel?.stopTaskTimer()
                mViewModel?.startTaskTimer(mIsCommentTask)
                mViewModel?.showTip(mContainer, "没有待领取的任务")
            }
            else -> {
                L.i("获取数据失败：${taskBean.msg}")
                context?.run {
                    mViewModel?.showTip(mContainer, "获取数据失败：${taskBean.msg}")
                    ToastUtils.showToast(this, "获取数据失败：${taskBean.msg}")
                }
            }
        }
    }


    override fun onParseDatas(taskData: ArrayList<ArrayList<String>>) {
        mTableDatas.addAll(taskData)

        initTableView(mTableDatas)

        mViewModel?.clearData()
    }

    override fun onFailed(msg: String?) {
        L.i("获取任务失败：$msg")
        mViewModel?.stopTaskTimer()
        mViewModel?.startTaskTimer(mIsCommentTask)
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
//            startPdd()  //不用代理
            getPort() //用代理
            /*  val curPort = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_CUR_PORT)
              if (!TextUtils.isEmpty(curPort) && LocalVpnService.IsRunning) {  //如果端口不为空
                  //stopMyVpnService()
                  L.i("关闭端口：$curPort")
                  mViewModel?.closePort(curPort)
              } else {

              }*/
        } else {
            context?.apply {
                mViewModel?.showTip(mContainer, "应用未获得root权限")
                ToastUtils.showToast(this, "应用未获得root权限")
            }
        }
    }

    /**
     * 申请端口（注意：必须先关闭端口，再申请端口）
     */
    private fun getPort() {
        closePort()
    }

    /**
     * 关闭端口
     */
    private fun closePort() {
        val curPort = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_CUR_PORT)
        if (!TextUtils.isEmpty(curPort)) {  //如果端口不为空
            //stopMyVpnService()
            L.i("开始关闭端口：$curPort")
            mViewModel?.closePort(curPort)
        } else {
            mTaskBean?.run {
                mViewModel?.getPorts(this)
            }
        }
    }

    /**
     * 端口打开成功，开始连接VPN
     */
    override fun onRequestPortsResult(result: String) {
        startMyVpnService(result)
    }

    override fun onResponPortsFailed(errorMsg: String) {
        ToastUtils.showToast(context!!, errorMsg)
        L.i("请求代理数据出错：$errorMsg")
        startTask()
    }

    /**
     * 申请关闭端口结果，重新连接VPN
     */
    override fun onResponClosePort(closeProxyBean: CloseProxyBean?) {
        L.i("关闭端口结果：$closeProxyBean")
        mTaskBean?.run {
            mViewModel?.getPorts(this)
        }
    }

    /**
     * 开启VPN
     */
    fun startMyVpnService(result: String) {
        L.i("请求打开端口结果：$result")
        LocalVpnService.addOnStatusChangedListener(this)
        activity?.run {
            val proxyIPBean = Gson().fromJson(result, ProxyIPBean::class.java)
            proxyIPBean?.data?.apply {
                LocalVpnManager.getInstance().initData(this@run, authuser, authpass, domain, port?.get(0)?.toString())
                LocalVpnManager.getInstance().startVpnService(this@run)
            }
        }
    }

    /**
     * VPN状态变化
     */
    override fun onStatusChanged(status: String, isRunning: Boolean?) {
        L.i("LocalVpnService status changed: $status isRunning: $isRunning MainThread: ${ThreadUtils.isMainThread()}")
        if (isRunning!!)   //代理连接成功
        {
            //protectSocket()

            LocalVpnService.IsRunning = true
            startPdd()
            // mViewModel?.checkVpnConnected()
        } else {
            //VPN 连接失败
        }
    }

    override fun onResponVpnResult(result: Boolean) {
        if (result) {
            LocalVpnService.IsRunning = true
            startPdd()
        } else {
            L.i("VPN连接失败，尝试重新连接次数：$mVpnFailedConnectCount")
            mVpnFailedConnectCount++
            if (mVpnFailedConnectCount < 5) {
                val ipPorts = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_IP_PORTS)
                startMyVpnService(ipPorts)
            } else {
                context?.run {
                    ToastUtils.showToast(this, "VPN连接失败,重新请求端口")
                    startTask()
                }
            }
        }
    }

    /**
     * VPN连接成功后，把Socket保护起来
     */
    private fun protectSocket() {
        mContainer?.postDelayed({
            val result = LocalVpnService.mInstance.protect(SocketClient().runningSocket)
            L.i("protect socket result: $result")
        }, 3000)
    }

    override fun onLogReceived(logString: String) {
        L.i("LocalVpnService onLogReceived: $logString")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.clearSubscribes()
        activity?.apply {
            LocalVpnManager.getInstance().stopVpnService(this)
        }
    }


    /* private fun unBindService() {
         activity?.run {
             mServiceConnect?.let {
                 unbindService(it)
             }
         }
     }*/

    /**
     * 启动拼多多开始任务
     */
    private fun startPdd() {
        //展示弹框
        mContainer?.postDelayed({

            val launchIntentForPackage =
                context?.packageManager?.getLaunchIntentForPackage(Constant.BUY_TOGETHER_PKG)
            if (launchIntentForPackage != null) {
                startActivity(launchIntentForPackage)
            } else {
                context?.run {
                    ToastUtils.showToast(this, "未安装拼多多")
                }
            }
        }, 5000)
    }

}