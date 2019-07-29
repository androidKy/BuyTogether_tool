package com.buy.together

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.buy.together.base.BaseViewModel
import com.buy.together.utils.Constant
import com.proxy.service.core.AppInfo
import com.proxy.service.core.AppProxyManager
import com.safframework.log.L
import com.utils.common.PackageManagerUtils
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener
import me.goldze.mvvmhabit.utils.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-07-29.
 **/
class MainAcViewModel(val context: Activity, val mainAcView: MainAcView) : BaseViewModel<Context, MainAcView>() {

    /**
     * 添加APP到代理IP
     */
    fun addApps2Proxy() {
        if (AppProxyManager.isLollipopOrAbove) {
            AppProxyManager(context)
        }

        val pm = context.packageManager // 获得PackageManager对象

        val pkgList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        } else {
            pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        }
        for (packageInfo in pkgList) {
            // 获取到设备上已经安装的应用的名字,即在AndriodMainfest中的app_name。
            val appName = packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
            // 获取到应用所在包的名字,即在AndriodMainfest中的package的值。
            val packageName = packageInfo.packageName
            L.i("app name: $appName packageName: $packageName")

            AppInfo().run {
                appLabel = appName
                pkgName = packageName

                AppProxyManager.Instance.mlistAppInfo.add(this)

                if (packageName == context.packageName || packageName == Constant.BUY_TOGETHER_PKG
                    || packageName == Constant.QQ_TIM_PKG || packageName == Constant.QQ_LIATE_PKG
                    || packageName == "com.android.chrome"
                ) {
                    AppProxyManager.Instance.addProxyApp(this)
                }
            }
        }
    }

    /**
     * 保存屏幕分辨率参数
     */
    fun saveScreenDensity() {
        L.i(
            "width = ${com.utils.common.DisplayUtils.getRealWidth(context)} height = ${com.utils.common.DisplayUtils.getRealHeight(
                context
            )}"
        )
        val width = com.utils.common.DisplayUtils.getRealWidth(context)
        val height = com.utils.common.DisplayUtils.getRealHeight(context)

        SPUtils.getInstance().apply {
            put(Constant.KEY_SCREEN_DENSITY, "$width,$height")
            put(Constant.KEY_SCREEN_WIDTH, width)
            put(Constant.KEY_SCREEN_HEIGHT, height)
        }
    }

    /**
     * 完成一轮任务，更新任务完成情况
     */
    fun updateTask(isSucceed: Boolean, remark: String) {
        PackageManagerUtils.getInstance().killApplication(Constant.BUY_TOGETHER_PKG)
        PackageManagerUtils.getInstance().killApplication(Constant.QQ_TIM_PKG)

        val taskId = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getInt(Constant.KEY_TASK_ID)
        ApiManager.instance
            .setDataListener(object : DataListener {
                override fun onSucceed(result: String) {
                    mainAcView.onResponUpdateTask()
                }

                override fun onFailed(errorMsg: String) {
                    mainAcView.onResponUpdateTask()
                }
            })
            .updateTaskStatus(taskId.toString(), isSucceed, "账号名", "订单号", remark)

        //todo 账号名和订单号
    }

}