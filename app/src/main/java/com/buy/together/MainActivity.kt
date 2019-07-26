package com.buy.together

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.TaskListener
import com.buy.together.fragment.MainFragment
import com.buy.together.utils.Constant
import com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE
import com.proxy.service.core.AppInfo
import com.proxy.service.core.AppProxyManager
import com.safframework.log.L
import com.utils.common.PackageManagerUtils
import com.utils.common.ToastUtils
import me.goldze.mvvmhabit.utils.SPUtils

class MainActivity : AppCompatActivity() {

    private var mMainFragment: MainFragment? = null
    private var mTaskRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, KeepLiveService::class.java))
        } else {
            startService(Intent(this, KeepLiveService::class.java))
        }*/
        initFragment()

        queryAppInfo()
    }


    private fun queryAppInfo() {
        if (AppProxyManager.isLollipopOrAbove) {
            AppProxyManager(this)
        }

        val pm = this.packageManager // 获得PackageManager对象

        val pkgList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        } else {
            pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        }
        for (packageInfo in pkgList) {
            // 获取到设备上已经安装的应用的名字,即在AndriodMainfest中的app_name。
            val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            // 获取到应用所在包的名字,即在AndriodMainfest中的package的值。
            val packageName = packageInfo.packageName
            L.i("app name: $appName packageName: $packageName")

            AppInfo().run {
                appLabel = appName
                pkgName = packageName

                AppProxyManager.Instance.mlistAppInfo.add(this)

                if (packageName == com.utils.common.Constants.PKG_NAME || packageName == Constant.BUY_TOGETHER_PKG || packageName == Constant.QQ_TIM_PKG ||
                    packageName == Constant.QQ_LIATE_PKG || packageName == "com.android.chrome"
                ) {
                    AppProxyManager.Instance.addProxyApp(this)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        saveScreenDensity(this)
    }

    override fun onResume() {
        super.onResume()
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(
                this,
                MyAccessibilityService::class.java.canonicalName!!
            )
        ) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }
        startTask()
    }

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
            L.i("任务完成，重新开始任务")
            PackageManagerUtils.getInstance().killApplication(Constant.BUY_TOGETHER_PKG)
            PackageManagerUtils.getInstance().killApplication(Constant.QQ_TIM_PKG)

            mTaskRunning = false
            startTask()
        }

        override fun onTaskFailed(failedText: String) {
            L.i("任务失败：重新开始任务.errorMsg:$failedText")
            mTaskRunning = false
            ToastUtils.showToast(this@MainActivity, "任务失败：$failedText")
        }
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

    /**
     * 保存屏幕数据，用于Adb命令根据像素点击
     */
    fun saveScreenDensity(activity: Activity) {
        L.i(
            "width = ${com.utils.common.DisplayUtils.getRealWidth(activity)} height = ${com.utils.common.DisplayUtils.getRealHeight(
                activity
            )}"
        )
        val width = com.utils.common.DisplayUtils.getRealWidth(activity)
        val height = com.utils.common.DisplayUtils.getRealHeight(activity)

        SPUtils.getInstance().apply {
            put(Constant.KEY_SCREEN_DENSITY, "$width,$height")
            put(Constant.KEY_SCREEN_WIDTH, width)
            put(Constant.KEY_SCREEN_HEIGHT, height)
        }

    }
}
