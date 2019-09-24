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
import com.accessibility.service.MyAccessibilityService.Companion.ACTION_APP_RESTART
import com.accessibility.service.MyAccessibilityService.Companion.ACTION_EXCEPTION_RESTART
import com.accessibility.service.MyAccessibilityService.Companion.ACTION_TASK_FAILED
import com.accessibility.service.MyAccessibilityService.Companion.ACTION_TASK_RESTART
import com.accessibility.service.MyAccessibilityService.Companion.ACTION_TASK_SUCCEED
import com.accessibility.service.MyAccessibilityService.Companion.KEY_TASK_MSG
import com.accessibility.service.data.TaskCategory
import com.accessibility.service.util.Constant
import com.accessibility.service.util.PackageManagerUtils
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val formatStrategy = CsvFormatStrategy.newBuilder()
            .tag("Pdd_Log")
            .build()
        Logger.addLogAdapter(DiskLogAdapter(formatStrategy))

        ProxyConfig.Instance.globalMode = true

        initFragment()

        mMainAcViewModel = MainAcViewModel(this, this)
        mMainAcViewModel?.run {
            addApps2Proxy()
        }

        registerReceiver()

        initAction()
    }

    private fun initAction() {
        when(BuildConfig.taskType)
        {
            TaskCategory.NORMAL_TASK ->   actionBar?.title = "pddTask_${BuildConfig.VERSION_NAME}"
            TaskCategory.COMMENT_TASK -> actionBar?.title = "pddComment_${BuildConfig.VERSION_NAME}"
            TaskCategory.CONFIRM_SIGNED_TASK -> actionBar?.title = "pddConfirmSigned_${BuildConfig.VERSION_NAME}"
        }
    }


    private fun registerReceiver() {
        mTaskReceiver = TaskReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_TASK_RESTART)
        intentFilter.addAction(ACTION_APP_RESTART)
        intentFilter.addAction(ACTION_TASK_SUCCEED)
        intentFilter.addAction(ACTION_TASK_FAILED)
        intentFilter.addAction(ACTION_EXCEPTION_RESTART)
        registerReceiver(mTaskReceiver, intentFilter)
        // crashInJava()
        NetStateReceiver.registerNetworkStateReceiver(this)
        NetStateReceiver.registerObserver(object : NetChangeObserver {
            override fun onNetConnected(type: NetUtils.NetType?) {
                L.i("网络连接正常")
            }

            override fun onNetDisConnect() {
                ToastUtils.showToast(this@MainActivity, "网络发生异常")
                L.i("网络发生异常")
                sendBroadcast(Intent(ACTION_TASK_RESTART))
            }
        })
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
        if (!mTaskRunning)
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
        NetStateReceiver.unRegisterNetworkStateReceiver(this)

        mMainAcViewModel?.disableAccessibilityService()
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
//        startPdd()
        mMainFragment?.apply {
            if (!mTaskRunning) {
                mTaskRunning = true
                startTask()
            }
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
                    ACTION_EXCEPTION_RESTART -> {
                        restartTaskApp()
                    }

                    ACTION_TASK_RESTART -> {
                        restartTaskApp()
                        /* PackageManagerUtils.startActivity(
                             this@MainActivity.packageName,
                             "com.buy.together.MainActivity"
                         )
                         mTaskRunning = false
                         mMainAcViewModel?.checkAccessibilityService()*/
                    }

                    ACTION_APP_RESTART -> {

                    }

                    ACTION_TASK_FAILED -> {
                        val msg = intent.getStringExtra(KEY_TASK_MSG)
                        L.i("任务失败：重新开始任务.errorMsg:$msg")
                        // ToastUtils.showToast(this@MainActivity, "任务失败：$failedMsg")
                        mMainAcViewModel?.updateTask(false, msg)
                    }

                    ACTION_TASK_SUCCEED -> {
                        L.i("任务完成，更新任务状态")
                        mMainAcViewModel?.updateTask(true, "success")
                    }
                }
            }

        }
    }

    /**
     * 重新启动任务App
     */
    fun restartTaskApp() {
        PackageManagerUtils.restartApplication(Constant.PKG_NAME,MyAccessibilityService.ACTIVITY_TASK_LAUNCHER)
    }
}
