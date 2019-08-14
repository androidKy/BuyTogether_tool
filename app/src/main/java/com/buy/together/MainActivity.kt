package com.buy.together

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.buy.together.fragment.MainFragment
import com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE
import com.safframework.log.L
import com.utils.common.SPUtils

class MainActivity : AppCompatActivity(), MainAcView {
    private var mMainFragment: MainFragment? = null
    private var mTaskRunning: Boolean = false
    private var mMainAcViewModel: MainAcViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // crashInJava()
    }

    fun crashInJava() {
        var nullStr: String? = "hello"
        val convertValue = nullStr?.toInt()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMainAcViewModel?.requestPermission()
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(
                this,
                MyAccessibilityService::class.java.canonicalName!!
            )
        ) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }
        // PackageManagerUtils.getInstance().restartApplication(this)
        startTask()
    }

    /**
     * 开始任务
     */
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
        //PackageManagerUtils.getInstance().restartApplication(this)
        //延迟1秒，等SP的异步清理完信息
        Handler(Looper.getMainLooper()).postDelayed({
            mTaskRunning = false
            startTask()
        }, 1000)
    }

    /**
     *
     */
    override fun onFailed(msg: String?) {

    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        mMainFragment = MainFragment()

        beginTransaction.add(R.id.main_container, mMainFragment!!)

        beginTransaction.commitNow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            L.i("VPN启动回调的Activity")
            if (resultCode == RESULT_OK) {
                val ipPorts = SPUtils.getInstance(Constant.SP_IP_PORTS).getString(Constant.KEY_IP_PORTS)
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
    }
}
