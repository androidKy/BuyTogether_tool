package com.accessibility.service.function

import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.safframework.log.L
import com.utils.common.CMDUtil
import com.utils.common.ThreadUtils

/**
 * Description:
 * Created by Quinin on 2019-07-13.
 **/
class ClearDataService {

    fun clearData(isClearAliPay: Boolean, taskListener: TaskListener) {
        L.i("clear data")
        ThreadUtils.executeBySingle(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                var clearDataCmd = "pm clear ${Constant.QQ_TIM_PKG};" +
                        "pm clear ${Constant.BUY_TOGETHER_PKG};"
                if (isClearAliPay)
                    clearDataCmd = clearDataCmd + "pm clear ${Constant.ALI_PAY_PKG};"

                val cmdResult = CMDUtil().execCmd(
                    clearDataCmd
                    /*   "cd /sdcard/;" +
                       "rm -fr Android;" +
                       "rm -fr Tencent;" +
                       "cd /data/data/com.tencent.mobileqq/;" +
                       "rm -fr **;" +
                       "cd /sdcard/Android/data/com.tencent.mobileqq;" +
                       "rm -fr **;" +
                       "cd /storage/emulated/0;" +
                       "rm -fr Android;rm -fr Tencent;rm -fr com.tencent.mobileqq;"
*/
                )
                // val cmdResult = CMDUtil().execCmd("pm clear com.tencent.mobileqq")

                return cmdResult.contains("Success")
            }

            override fun onSuccess(result: Boolean?) {
                if (result!!)
                    taskListener.onTaskFinished()
                else taskListener.onTaskFailed("工具应用未获得root权限")
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
                taskListener.onTaskFailed(t?.message!!)
            }

        })
    }

    fun clearData(taskListener: TaskListener) {
        //请求root权限，并且清理QQ登录的数据
        clearData(false, taskListener)
    }
}