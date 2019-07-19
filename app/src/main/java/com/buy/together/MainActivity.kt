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
import com.buy.together.fragment.MainFragment
import com.buy.together.service.KeepLiveService
import com.buy.together.utils.Constant
import com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE
import com.proxy.service.core.AppInfo
import com.proxy.service.core.AppProxyManager
import com.safframework.log.L
import me.goldze.mvvmhabit.utils.SPUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, KeepLiveService::class.java))
        } else {
            startService(Intent(this, KeepLiveService::class.java))
        }

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

                if (packageName == this@MainActivity.packageName || packageName == "com.xunmeng.pinduoduo" ||
                    packageName == "com.tencent.mobileqq" || packageName == "com.android.chrome"
                ) {
                    AppProxyManager.Instance.addProxyApp(this)
                }
            }
        }

       // AppProxyManager.mInstance.saveProxyAppList()

/*
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(pm))
        if (AppProxyManager.mInstance.mlistAppInfo != null) {
            AppProxyManager.mInstance.mlistAppInfo.clear()
            for (reInfo in resolveInfos) {
                val pkgName = reInfo.activityInfo.packageName // 获得应用程序的包名
                val appLabel = reInfo.loadLabel(pm) as String // 获得应用程序的Label
                val icon = reInfo.loadIcon(pm) // 获得应用程序图标

            }
        }*/
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
        initFragment()
    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val mainFragment = MainFragment()

        beginTransaction.add(R.id.main_container, mainFragment)

        beginTransaction.commitNow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            L.i("VPN启动回调的Activity")
            if (resultCode == RESULT_OK) {
                // startVpnService()
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
        }

    }
}
