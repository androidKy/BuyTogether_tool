package com.accessibility.service.function

import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
class AliPayLogin(val myAccessibilityService: MyAccessibilityService) {
    private var mTaskListener: TaskListener? = null
    private var mLoginFailedCount: Int = 0

    fun login(taskListener: TaskListener) {
        mTaskListener = taskListener

        val account = TaskDataUtil.instance.getAlipayAccount()
        if (!TextUtils.isEmpty(account))
            login(account!!, TaskDataUtil.instance.getAlipayPsw()!!)
        else responTaskFailed("支付宝账号或者密码不能为空")
    }

    fun login(userName: String, userPsw: String) {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("允许")
            .setNodeParams("允许")
            .setNodeParams("其他登录方式")
            .setNodeParams(WidgetConstant.EDITTEXT, 3, false, userName)
            .setNodeParams("下一步")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputPsw(userPsw)
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("支付宝账号输入有误")
                }
            })
            .create()
            .execute()
    }

    fun inputPsw(userPsw: String) {
        AdbScriptController.Builder()
            .setXY("540,850", 3000)
            .setText(userPsw)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    choosePayChannel()
                }

                override fun onTaskFailed(failedText: String) {

                }
            })
            .create()
            .execute()
    }

    /**
     * 选择支付宝余额的支付方式
     */
    fun choosePayChannel() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("登录")
            .setNodeParams("付款方式")
            .setNodeParams("账户余额", 0, true, true, 8, false)
            .setNodeParams("立即付款")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responTaskSuccess()
                }

                override fun onTaskFailed(failedText: String) {
                    // mTaskListener?.onTaskFailed("支付宝登录失败，请检查是否设置安全验证登录方式")
                    responTaskFailed("支付宝登录失败，请检查是否设置安全验证登录方式")
                }
            })
    }

    fun responTaskSuccess() {
        mLoginFailedCount = 0
        mTaskListener?.onTaskFinished()
    }

    fun responTaskFailed(msg: String) {
        mLoginFailedCount = 0
        myAccessibilityService.setCurPageType(PageEnum.START_PAGE)
        mTaskListener?.onTaskFailed(msg)
    }
}