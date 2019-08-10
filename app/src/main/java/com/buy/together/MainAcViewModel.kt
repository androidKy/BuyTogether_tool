package com.buy.together

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.accessibility.service.util.Constant
import com.accessibility.service.util.UpdateSPManager
import com.buy.together.base.BaseViewModel
import com.proxy.service.core.AppInfo
import com.proxy.service.core.AppProxyManager
import com.safframework.log.L
import com.utils.common.*
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener
import me.goldze.mvvmhabit.utils.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-07-29.
 **/
class MainAcViewModel(val context: Activity, val mainAcView: MainAcView) : BaseViewModel<Context, MainAcView>() {

    fun requestPermission() {
        val permissionArray = arrayListOf(
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET
        )
        if (!PermissionUtils.isGranted(permissionArray))
            PermissionUtils
                .permission(permissionArray)
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(permissionsGranted: MutableList<String>?) {
                        saveData()
                    }

                    override fun onDenied(
                        permissionsDeniedForever: MutableList<String>?,
                        permissionsDenied: MutableList<String>?
                    ) {
                        ToastUtils.showToast(context, "请授予该应用相应的权限")
                        PackageManagerUtils.getInstance().killApplication(context.packageName)
                    }

                })
                .request()
    }

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
                    || packageName == "com.android.chrome" || packageName == "com.android.browser"
                ) {
                    AppProxyManager.Instance.addProxyApp(this)
                }
            }
        }
    }

    /**
     * 保存屏幕分辨率参数
     */
    fun saveData() {
        L.i(
            "width = ${com.utils.common.DisplayUtils.getRealWidth(context)} height = ${com.utils.common.DisplayUtils.getRealHeight(
                context
            )}"
        )
        val width = com.utils.common.DisplayUtils.getRealWidth(context)
        val height = com.utils.common.DisplayUtils.getRealHeight(context)

        SPUtils.getInstance(Constant.SP_DEVICE_PARAMS).apply {
            put(Constant.KEY_SCREEN_DENSITY, "$width,$height")
            put(Constant.KEY_SCREEN_WIDTH, width)
            put(Constant.KEY_SCREEN_HEIGHT, height)
        }

        SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
            .put(Constant.KEY_REAL_DEVICE_IMEI, DevicesUtil.getIMEI(context))
    }

    /**
     * 完成一轮任务，更新任务完成情况
     */
    fun updateTask(isSucceed: Boolean, remark: String) {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                CmdListUtil.getInstance().apply {
                    val cmdStr = "am force-stop ${Constant.ALI_PAY_PKG};" +
                            "am force-stop ${Constant.BUY_TOGETHER_PKG};" +
                            "am force-stop ${Constant.QQ_TIM_PKG};"
                    execCmd(cmdStr)
                    return true
                }
            }

            override fun onSuccess(result: Boolean?) {
                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).apply {
                    val taskId = getInt(Constant.KEY_TASK_ID)
                    var orderMoney = getString(Constant.KEY_ORDER_MONEY)
                    var orderNumber = getString(Constant.KEY_ORDER_NUMBER)
                    val pddAccount = getString(Constant.KEY_PDD_ACCOUNT)
                    val progress = getString(Constant.KEY_TASK_PROGRESS)
                    L.i(
                        "上报任务状态：taskId=$taskId orderNumber=$orderNumber \n" +
                                " orderMoney=$orderMoney paddAccount=$pddAccount"
                    )
                    if (!isSucceed) {
                        orderNumber = ""
                        orderMoney = ""
                    }

                    ApiManager()
                        .setDataListener(object : DataListener {
                            override fun onSucceed(result: String) {
                                UpdateSPManager(context).updateTaskStatus(1)
                                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).run {
                                    remove(Constant.KEY_TASK_DATA)
                                    remove(Constant.KEY_ORDER_NUMBER)
                                    remove(Constant.KEY_ORDER_MONEY)
                                    remove(Constant.KEY_PDD_ACCOUNT)
                                    remove(Constant.KEY_TASK_PROGRESS)
                                }

                                mainAcView.onResponUpdateTask()
                            }

                            override fun onFailed(errorMsg: String) {
                                UpdateSPManager(context).updateTaskStatus(0)
                                mainAcView.onResponUpdateTask()
                            }
                        })
                        .updateTaskStatus(
                            taskId.toString(),
                            isSucceed,
                            pddAccount,
                            progress,
                            orderNumber,
                            orderMoney,
                            remark
                        )
                }
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }

        })

    }

}