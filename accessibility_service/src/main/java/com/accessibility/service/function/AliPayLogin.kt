package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
class AliPayLogin(val myAccessibilityService: MyAccessibilityService) {
    private var mTaskListener: TaskListener? = null
    private var mLoginFailedCount: Int = 0

    fun login(taskListener: TaskListener) {
        mTaskListener = taskListener

        login("", "")
    }

    fun login(userName: String, userPsw: String) {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("允许")
            .setNodeParams("允许")
            .setNodeParams("其他登录方式")
            .setNodeParams("输入账号框")
            .setNodeParams("下一步")
            .setNodeParams("输入账号框")
            .setNodeParams("输入密码框")
            .setNodeParams("登录")
            .setNodeParams("付款方式")
            .setNodeParams("付款方式名称")
            .setNodeParams("立即付款")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    mTaskListener?.onTaskFinished()
                }

                override fun onTaskFailed(failedText: String) {
                    mTaskListener?.onTaskFailed("$failedText node was not found.¬")
                }

            })
            .create()
            .execute()

    }
}