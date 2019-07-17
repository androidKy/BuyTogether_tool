package com.accessibility.service.function

import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.ThreadUtils
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-13.
 **/
class ClearDataService {

    fun clearData(taskListener: TaskListener) {
        //请求root权限，并且清理QQ登录的数据
        L.i("clear data")
        ThreadUtils.executeBySingle(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                /* val cmdResult = CMDUtil().execCmd(
                     "cd /data/data/com.tencent.mobileqq/;" +
                             "rm -fr cache,code_cache,shared_prefs,databases;"
                      "cd /data/data/com.xunmeng.pinduoduo/;" +
                      "rm -fr **;"
                 )*/
                val cmdResult = "Success"

                return cmdResult.contains("Success")
            }

            override fun onSuccess(result: Boolean?) {
                taskListener.onTaskFinished()
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
                taskListener.onTaskFailed(t?.message!!)
            }

        })
    }
}