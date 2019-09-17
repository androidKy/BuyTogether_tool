package com.buy.together

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.util.Constant
import com.accessibility.service.util.UpdateSPManager
import com.buy.together.base.BaseViewModel
import com.proxy.service.core.AppInfo
import com.proxy.service.core.AppProxyManager
import com.safframework.log.L
import com.utils.common.*
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener
import org.json.JSONObject

/**
 * Description:
 * Created by Quinin on 2019-07-29.
 **/
class MainAcViewModel(val context: Activity, val mainAcView: MainAcView) :
    BaseViewModel<Context, MainAcView>() {

    fun requestPermission() {
        val permissionArray = arrayListOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
            // Manifest.permission.WRITE_SECURE_SETTINGS
        )
        PermissionUtils
            .permission(permissionArray)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    saveData()
                    mainAcView.onPermissionGranted()
                }

                override fun onDenied(
                    permissionsDeniedForever: MutableList<String>?,
                    permissionsDenied: MutableList<String>?
                ) {
                    ToastUtils.showToast(context, "请授予该应用相应的权限")
                    // PackageManagerUtils.getInstance().killApplication(context.packageName)
                }
            })
            .request()
    }

    /**
     * 添加APP到代理IP
     */
    fun addApps2Proxy() {
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
            // L.i("app name: $appName packageName: $packageName")

            AppInfo().run {
                appLabel = appName
                pkgName = packageName

                //AppProxyManager.getInstance().mlistAppInfo.add(this)

                if (packageName == context.packageName || packageName == Constant.BUY_TOGETHER_PKG
                    || packageName == Constant.QQ_TIM_PKG || packageName == Constant.QQ_LIATE_PKG
                    || packageName == "com.android.browser"
                ) {
                    AppProxyManager.getInstance().setAppInfo(this)
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


        SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS).apply {
            val realImei = getString(Constant.KEY_REAL_DEVICE_IMEI)
            if (realImei.isNullOrEmpty())
                put(Constant.KEY_REAL_DEVICE_IMEI, DevicesUtil.getIMEI(context))
        }

    }

    /**
     * 检查无障碍服务是否开启
     */
    fun checkAccessibilityService(){
        L.i("检测无障碍服务是否开启")
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(
                context,
                MyAccessibilityService::class.java.canonicalName!!
            )
        ) {
            //自动开启无障碍服务
            ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
                override fun onSuccess(result: Boolean?) {
                    if (result!!) {
                        Settings.Secure.putString(
                            context.contentResolver,
                            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                            context.packageName + "/com.accessibility.service.MyAccessibilityService"
                        )
                        Settings.Secure.putInt(
                            context.contentResolver,
                            Settings.Secure.ACCESSIBILITY_ENABLED, 1
                        )
                        checkAccessibilityService()
                    }
                }

                override fun onCancel() {

                }

                override fun onFail(t: Throwable?) {

                }

                override fun doInBackground(): Boolean {
                    val result = CMDUtil().execCmd(
                        "pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS;"
                    )
                    /* "settings put secure enabled_accessibility_services ${Constant.BUY_TOGETHER_PKG}/com.accessibility.service.MyAccessibilityService;" +
                     "settings put secure accessibility_enabled 1;"
         )*/
                    L.i("用adb命令开启无障碍:$result")
                    if (result.contains("Success")) {
                        return true
                    }

                    return true

                }
            })

        } else {
            mainAcView.onAccessibilityService()
        }
    }

    /**
     * 完成一轮任务，更新任务完成情况
     */

    fun updateTask(isSucceed: Boolean, remark: String) {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                CmdListUtil.getInstance().apply {
                    val cmdStr = "am force-stop ${Constant.ALI_PAY_PKG};" +
                            "am force-stop ${Constant.BUY_TOGETHER_PKG};"+
                            "am force-stop ${Constant.XIAOMI_BROWSER_PKG};"
                    execCmd(cmdStr)
                    return true
                }
            }

            override fun onSuccess(result: Boolean?) {
                val isCommentTask = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                    .getBoolean(Constant.KEY_TASK_TYPE)
                if (isCommentTask)
//                    updateCommentTask(taskStatus,remark)
                    updateCommentTask(isSucceed, remark)
                else updateNormalTask(isSucceed, remark)
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }

        })
    }

    /**
     * 更新评论任务状态
     */
    private fun updateCommentTask(isSucceed: Boolean, remark: String) {
        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).apply {
            val taskId = getInt(Constant.KEY_TASK_ID)
            val successCode = getInt(Constant.KEY_COMMENT_SUCCESS_CODE)
            var finalRemark = remark
            L.i("上报评论任务状态：taskId:$taskId successCode:$successCode remark:$finalRemark")
           when(successCode){
               0-> finalRemark = "未签收"
               1-> finalRemark = "评论成功"
               2-> finalRemark = "评论失败"
           }

            ApiManager()
                .setDataListener(object : DataListener {
                    override fun onSucceed(result: String) {
                        try {
                            val jsonObj = JSONObject(result)
                            val code = jsonObj.getInt("code")
                            if (code == 200) {
                                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).clear()
                            }
                        } catch (e: Exception) {
                            L.e(e.message, e)
                        }
                        sendTaskStatusReceiver()
                        mainAcView.onResponUpdateTask()
                    }

                    override fun onFailed(errorMsg: String) {
                        updateCommentTask(isSucceed, remark)
                    }

                })
                .updateCommentTaskStatus(taskId, successCode, finalRemark)
        }
    }


    /**
     * 更新正常任务状态
     */
    private fun updateNormalTask(isSucceed: Boolean, remark: String) {
        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).apply {
            val taskId = getInt(Constant.KEY_TASK_ID)
            var orderMoney = getString(Constant.KEY_ORDER_MONEY)
            var orderNumber = getString(Constant.KEY_ORDER_NUMBER)
            val pddAccount = getString(Constant.KEY_PDD_ACCOUNT)
            val progress = getString(Constant.KEY_TASK_PROGRESS)
            L.i(
                "上报任务状态：taskId=$taskId orderNumber=$orderNumber remark=$remark\n" +
                        "orderMoney=$orderMoney paddAccount=$pddAccount progress=$progress"
            )
            var finalRemark = remark
            if (!isSucceed) {
                orderNumber = ""
                orderMoney = ""
                sendTaskStatusReceiver()
                mainAcView.onResponUpdateTask()
                return
            } else {
                finalRemark = "任务成功:$progress"
            }

            ApiManager()
                .setDataListener(object : DataListener {
                    override fun onSucceed(result: String) {
                        try {
                            val jsonObj = JSONObject(result)
                            val code = jsonObj.getInt("code")
                            if (code == 200) {
                                UpdateSPManager(context).updateTaskStatus(1)
                                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).clear()
                            } else {
                                L.i("任务更新失败：code:$code")
                            }
                        } catch (e: Exception) {
                            L.e(e.message, e)
                        }
                        sendTaskStatusReceiver()
                        mainAcView.onResponUpdateTask()
                    }

                    override fun onFailed(errorMsg: String) {
                        updateNormalTask(isSucceed, remark)
                        /* UpdateSPManager(context).updateTaskStatus(0)
                         mainAcView.onResponUpdateTask()*/
                    }
                })
                .updateTaskStatus(
                    taskId.toString(),
                    isSucceed,
                    pddAccount,
                    progress,
                    orderNumber,
                    orderMoney,
                    finalRemark
                )
        }
    }

    /**
     * 发送广播到无障碍服务更新任务状态
     */
    private fun sendTaskStatusReceiver() {
        context.sendBroadcast(Intent(MyAccessibilityService.ACTION_TASK_STATUS))
    }
}