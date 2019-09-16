package com.buy.together

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.buy.together.fragment.MainFragment
import com.buy.together.receiver.NetChangeObserver
import com.buy.together.receiver.NetStateReceiver
import com.buy.together.utils.NetUtils
import com.orhanobut.logger.CsvFormatStrategy
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.Logger
import com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE
import com.proxy.service.core.ProxyConfig
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.ToastUtils


class MainActivity : AppCompatActivity(), MainAcView {


    private var mMainFragment: MainFragment? = null

    private var mTaskRunning: Boolean = false

    private var mMainAcViewModel: MainAcViewModel? = null
    private var mTaskReceiver: TaskReceiver? = null

    companion object {
        const val ACTION_TASK_RESTART = "com.task.restart"      //发生未知错误，任务重新开始，重新请求代理和读取缓存的任务
        const val ACTION_APP_RESTART = "com.pdd.restart"        //拼多多APP重新启动
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val formatStrategy = CsvFormatStrategy.newBuilder()
            .tag("Pdd_Log")
            .build()
        Logger.addLogAdapter(DiskLogAdapter(formatStrategy))

        ProxyConfig.Instance.globalMode = true
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, KeepLiveService::class.java))
        } else {
            startService(Intent(this, KeepLiveService::class.java))
        }*/
        initFragment()

        mMainAcViewModel = MainAcViewModel(this, this)
        mMainAcViewModel?.run {
            addApps2Proxy()
        }

        mTaskReceiver = TaskReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_TASK_RESTART)
        intentFilter.addAction(ACTION_APP_RESTART)
        registerReceiver(mTaskReceiver, intentFilter)
        // crashInJava()
        NetStateReceiver.registerNetworkStateReceiver(this)
        NetStateReceiver.registerObserver(object : NetChangeObserver {
            override fun onNetConnected(type: NetUtils.NetType?) {
                // L.i("网络连接正常")

            }

            override fun onNetDisConnect() {
                ToastUtils.showToast(this@MainActivity, "网络发生异常")   //todo 网络异常的处理
            }
        })
    }

    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        mMainFragment = MainFragment()

        beginTransaction.add(R.id.main_container, mMainFragment!!)

        beginTransaction.commitNow()
    }

    override fun onStart() {
        super.onStart()
        L.i("开始申请权限")
        mMainAcViewModel?.requestPermission()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            L.i("VPN启动回调的Activity")
            if (resultCode == RESULT_OK) {
                val ipPorts =
                    SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_IP_PORTS)
                L.i("第一次打开VPN，需要确认允许VPN连接。ipPorts: $ipPorts")
                mMainFragment?.startMyVpnService(ipPorts)
            } else {
                //log("onActivityResult", "resultCode != RESULT_OK")
                //onLogReceived("canceled.")
                //EventBus.getDefault().postSticky(PostModel(PostCode.DisConnect_VPN))
            }
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTaskReceiver?.apply {
            unregisterReceiver(this)
        }
    }

    override fun onPermissionGranted() {
        L.i("权限授予完成")
        mMainAcViewModel?.checkAccessibilityService()
    }


    override fun onAccessibilityService() {
        if (!mTaskRunning) {
            startTask()
        }
    }

    /**
     * 开始任务
     */
    @Synchronized
    private fun startTask() {
        mMainFragment?.apply {
            if (!mTaskRunning) {
                mTaskRunning = true
                startTask()
                MyAccessibilityService.setTaskListener(TaskListenerImpl())
            }
        }
    }

    inner class TaskListenerImpl : TaskListener {
        override fun onTaskFinished() {
            L.i("任务完成，更新任务状态")
            mMainAcViewModel?.updateTask(true, "success")

        }

        override fun onTaskFailed(failedMsg: String) {
            L.i("任务失败：重新开始任务.errorMsg:$failedMsg")
            // ToastUtils.showToast(this@MainActivity, "任务失败：$failedMsg")
            mMainAcViewModel?.updateTask(false, failedMsg)
        }
    }

    /**
     * 任务完成情况已更新
     * @see MainAcViewModel.updateTask
     */
    override fun onResponUpdateTask() {
        L.i("更新任务状态完成，重新开始任务")
        //延迟2秒，等SP的异步清理完信息
        Handler(Looper.getMainLooper()).postDelayed({
            mTaskRunning = false
            startTask()
        }, 2000)
    }

    override fun onFailed(msg: String?) {
        L.i(msg)
    }


    private fun startPdd() {
        //展示弹框

        val launchIntentForPackage =
            this.packageManager?.getLaunchIntentForPackage(Constant.BUY_TOGETHER_PKG)
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage)
        } else {
            ToastUtils.showToast(this, "未安装拼多多")
        }
    }

    private fun startBrowse() {
        //展示弹框

        val launchIntentForPackage =
            this.packageManager?.getLaunchIntentForPackage(Constant.XIAOMI_BROWSER_PKG)
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage)
        } else {
            ToastUtils.showToast(this, "未安装拼多多")
        }
    }

    /**
     * 接收任务的各种异常和关闭、重启APP的处理
     */
    inner class TaskReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.apply {
                when (this) {
                    ACTION_TASK_RESTART -> {
                        mTaskRunning = false
                        mMainAcViewModel?.checkAccessibilityService()
                    }

                    ACTION_APP_RESTART -> {

                    }
                }
            }

        }
    }
}
