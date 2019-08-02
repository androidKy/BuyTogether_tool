package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L
import com.utils.common.SPUtils

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
        val psw = TaskDataUtil.instance.getAlipayPsw()
        L.i("支付宝登录：账号：$account 密码：$psw")
        if (account.isNullOrEmpty() || psw.isNullOrEmpty()) {
            responTaskFailed("支付宝账号或者密码不能为空")
            return
        }

        val isSwitchAccount = SPUtils.getInstance(myAccessibilityService.applicationContext, Constant.SP_TASK_FILE_NAME)
            .getBoolean(Constant.KEY_ALIPAY_ACCOUNT_SWITCH)
        L.i("是否需要切换支付宝账号：$isSwitchAccount")
        if (isSwitchAccount)
            login(account, psw)
        else {
            payDirectly()
        }
    }

    /**
     * 直接用余额支付
     */
    private fun payDirectly() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("订单编号", 1, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    val orderNumber = myAccessibilityService.findViewByText("订单编号")?.text?.toString()
                    L.i("订单编号: $orderNumber")
                    if (!orderNumber.isNullOrEmpty())
                        SPUtils.getInstance(myAccessibilityService.applicationContext, Constant.SP_TASK_FILE_NAME)
                            .put(Constant.KEY_ORDER_NUMBER, orderNumber)


                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        .setNodeParams("立即付款", 1)
                        //.setNodeParams("立即付款", 1)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                responTaskSuccess()
                            }

                            override fun onTaskFailed(failedText: String) {
                                responTaskFailed("支付宝付款失败")
                            }
                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedText: String) {

                }

            })
            .create()
            .execute()
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
                    responTaskFailed("未获得root权限")
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
            .setNodeParams("付款方式", 0, 60)
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
            .create()
            .execute()
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