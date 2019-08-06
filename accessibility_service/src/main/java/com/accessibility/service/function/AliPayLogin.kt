package com.accessibility.service.function

import android.text.TextUtils
import android.util.SparseIntArray
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
        val psw = TaskDataUtil.instance.getAliLoginPsw()
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
     * 下发的支付宝账号和之前的一致，直接用余额支付
     */
    private fun payDirectly() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("订单编号", 1, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    val orderNumber = myAccessibilityService.findViewByText("订单编号")?.text?.toString()
                    L.i("订单编号: $orderNumber")
                    val uploadOrderNumber = orderNumber?.split("编号")?.get(1)
                    if (!uploadOrderNumber.isNullOrEmpty()) {
                        SPUtils.getInstance(myAccessibilityService.applicationContext, Constant.SP_TASK_FILE_NAME)
                            .put(Constant.KEY_ORDER_NUMBER, uploadOrderNumber)
                    }

                    inputPayPsw()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("支付宝登录失败")
                }

            })
            .create()
            .execute()
    }

    /**
     * 输入支付¬密码
     */
    private fun inputPayPsw() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("立即付款", 1)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    adbInputPsw()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("支付宝付款环节失败")
                }
            })
            .create()
            .execute()
    }

    /**
     * 通过ADB输入密码
     */
    private fun adbInputPsw() {
        //818910
        val payPsw = TaskDataUtil.instance.getAliPay_psw()
        L.i("支付宝的支付密码：$payPsw")
        if (TextUtils.isEmpty(payPsw)) {
            responTaskFailed("支付宝的支付密码为空")
            return
        }
        val delayTime = 400L
        AdbScriptController.Builder()
            .setXY(regularPsw(payPsw!!), delayTime)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("支付密码输入错误")
                }

                override fun onTaskFinished() {
                    //支付成功
                    responTaskSuccess()     //todo 支付成功后，根据什么条件来判断支付成功
                    //paySuccess()
                }
            })
            .create()
            .execute()
    }

    /**
     * 根据密码找到对应的xy坐标
     */
    private fun regularPsw(payPsw: String): ArrayList<String> {
        val payPswXYList: ArrayList<String> = ArrayList<String>()
        try {
            val itemWidth =
                SPUtils.getInstance(myAccessibilityService, Constant.SP_DEVICE_PARAMS).getInt(
                    Constant.KEY_SCREEN_WIDTH,
                    1080
                ) / 3
            val itemHeight = 140
            val itemStartY = 1135


            val xNumberKey = SparseIntArray()
            xNumberKey.put(1, itemWidth / 2)
            xNumberKey.put(2, itemWidth + itemWidth / 2)
            xNumberKey.put(3, itemWidth * 2 + itemWidth / 2)
            xNumberKey.put(4, xNumberKey[1])
            xNumberKey.put(5, xNumberKey[2])
            xNumberKey.put(6, xNumberKey[3])
            xNumberKey.put(7, xNumberKey[1])
            xNumberKey.put(8, xNumberKey[2])
            xNumberKey.put(9, xNumberKey[3])
            xNumberKey.put(0, xNumberKey[2])

            val yNumberKey = SparseIntArray()
            yNumberKey.put(1, itemStartY + itemHeight / 2)
            yNumberKey.put(2, itemStartY + itemHeight / 2)
            yNumberKey.put(3, itemStartY + itemHeight / 2)
            yNumberKey.put(4, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(5, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(6, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(7, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(8, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(9, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(0, itemStartY + itemHeight * 3 + itemHeight / 2)

            val payPswCharArray = payPsw.toCharArray()

            for (i in 0 until payPswCharArray.size) {
                val pswInt = payPswCharArray[i].toString().toInt()
                val pswXY = "${xNumberKey[pswInt]},${yNumberKey[pswInt]}"
                L.i("$pswInt 坐标：$pswXY")
                payPswXYList.add(pswXY)
            }
        } catch (e: Exception) {
            L.e(e.message, e)
        }

        return payPswXYList
    }

    /**
     * 是否支付成功
     */
    private fun paySuccess() {

    }

    /**
     * 下发的支付宝账号和已登录的不一致
     */
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

    /**
     * 输入支付宝密码
     */
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
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //responTaskSuccess()
                    //登录成功
                    SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                        .put(Constant.KEY_ALIPAY_ACCOUNT, TaskDataUtil.instance.getAlipayAccount())

                    inputPayPsw()
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