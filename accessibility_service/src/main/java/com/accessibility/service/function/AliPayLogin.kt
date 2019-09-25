package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BasePayService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L
import com.utils.common.SPUtils
import java.util.regex.Pattern

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
class AliPayLogin(myAccessibilityService: MyAccessibilityService) :
    BasePayService(myAccessibilityService) {

    private var mLoginFailedCount: Int = 0
    private var mUserName: String? = null
    private var mUserPsw: String? = null

    override fun startService() {
        startPay()
    }

    private fun startPay() {

        val account = TaskDataUtil.instance.getAlipayAccount()
        val psw = TaskDataUtil.instance.getAliLoginPsw()
        L.i("支付宝登录：账号：$account 密码：$psw")
        if (account.isNullOrEmpty() || psw.isNullOrEmpty()) {
            responTaskFailed("支付宝账号或者密码不能为空")
            return
        }

        mUserName = account
        mUserPsw = psw

        var isSwitchAccount = SPUtils.getInstance(
            myAccessibilityService.applicationContext,
            Constant.SP_TASK_FILE_NAME
        )
            .getBoolean(Constant.KEY_ALIPAY_ACCOUNT_SWITCH)
        isSwitchAccount = false
        if (isSwitchAccount)
            login2()
        else {
            payDirectly()
        }
    }

    /**
     * 下发的支付宝账号和之前的一致，直接用余额支付
     */
    @Synchronized
    private fun payDirectly() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("订单编号", 1, false, 30)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    saveOrderMoney()
                    saveOrderNumber()

                    inputPayPsw()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付宝登录失败")
                    //dealOrderNumberFailed()
                }
            })
            .create()
            .execute()
    }

    /**
     * 保存订单金额
     */
    private fun saveOrderMoney() {
        val nodeList = myAccessibilityService.findViewsByText(".")
        L.i("找订单金额的节点size: ${nodeList.size}")
        val regex = ".*[a-zA-Z]+.*"
        for (i in 0 until nodeList.size) {
            val node = nodeList[i]
            L.i("节点text: ${node.text}")
            val isMatch = Pattern.compile(regex).matcher(node.text).matches()   //是否包含字母
            if (!isMatch) {
                SPUtils.getInstance(
                    myAccessibilityService.applicationContext,
                    Constant.SP_TASK_FILE_NAME
                )
                    .put(Constant.KEY_ORDER_MONEY, node.text.toString())
                break
            }
        }
    }

    /**
     * 保存订单编号
     */
    private fun saveOrderNumber() {
        val orderNumber = myAccessibilityService.findViewByText("订单编号")?.text?.toString()
        L.i("订单编号: $orderNumber")
        val uploadOrderNumber = orderNumber?.split("编号")?.get(1)
        if (!uploadOrderNumber.isNullOrEmpty()) {
            SPUtils.getInstance(
                myAccessibilityService.applicationContext,
                Constant.SP_TASK_FILE_NAME
            )
                .put(Constant.KEY_ORDER_NUMBER, uploadOrderNumber)
        }
    }

    /**
     * 输入支付¬密码
     */
    @Synchronized
    private fun inputPayPsw() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            //.setNodeParams("仍然支付", 0, 5, true)
            .setNodeParams("立即付款", 1, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    isPayUI()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付宝付款环节失败-$failedMsg")
                }
            })
            .create()
            .execute()
    }

    /**
     * 是否是支付密码界面
     */
    private fun isPayUI() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("忘记密码", 1, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到输入密码界面")
                    try {
                        adbInputPsw()
                    } catch (e: Exception) {
                        L.e(e.message, e)
                        responTaskFailed("输入密码发生异常")
                    }
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("找不到节点：$failedMsg")
                    responTaskFailed("没有跳转到输入密码的界面")
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
        if (payPsw.isNullOrEmpty()) {
            responTaskFailed("支付宝的支付密码为空")
            return
        }
        val payNode =
            myAccessibilityService.rootInActiveWindow?.findAccessibilityNodeInfosByText("付款")
        if (payNode != null) {
            val pswXyList = ArrayList<String>()
            if (payNode.size > 0) {
                L.i("正在输入公司账号的密码")
                pswXyList.addAll(regularPswCompany())
            } else {
                L.i("正在输入个人账号的密码")
                pswXyList.addAll(regularPsw(payPsw))
            }

            AdbScriptController.Builder()
                .setXY(pswXyList)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed("支付密码输入错误")
                    }

                    override fun onTaskFinished() {
                        //支付成功  todo 金额过大时，需要处理短信验证码
                        myAccessibilityService.apply {
                            performBackClick(6, object : AfterClickedListener {
                                override fun onClicked() {
                                    L.i("密码已输入，准备重启PDD")
                                    NodeController.Builder()
                                        .setNodeService(this@apply)
                                        .setNodeParams("立即支付", 1, false, 4)
                                        .setTaskListener(object : TaskListener {
                                            override fun onTaskFinished() {
                                                L.i("仍然停留在支付界面")
                                                performBackClick(1, object : AfterClickedListener {
                                                    override fun onClicked() {
                                                        NodeController.Builder()
                                                            .setNodeService(this@apply)
                                                            .setNodeParams("暂时放弃", 1, 4)
                                                            .setTaskListener(object : TaskListener {
                                                                override fun onTaskFinished() {
                                                                    responTaskSuccess()
                                                                }

                                                                override fun onTaskFailed(failedMsg: String) {
                                                                    responTaskSuccess()
                                                                }
                                                            })
                                                            .create()
                                                            .execute()
                                                    }
                                                })
                                            }

                                            override fun onTaskFailed(failedMsg: String) {
                                                L.i("支付成功，已离开支付界面")
                                                responTaskSuccess()
                                            }
                                        })
                                        .create()
                                        .execute()

                                }
                            })
                        }
                    }
                })
                .create()
                .execute()
        } else {
            L.i("rootInActiveWindow is null")
            responTaskFailed("rootInActiveWindow is null")
        }
    }


    /**
     * 下发的支付宝账号和已登录的不一致
     */
    fun login2() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("允许")
            .setNodeParams("允许")
            //  .setNodeParams("其他登录方式")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    pressOtherLogin()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付宝账号输入有误")
                }
            })
            .create()
            .execute()
    }

    fun pressOtherLogin() {
        AdbScriptController.Builder()
            .setXY("855,1675")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    myAccessibilityService.postDelay(Runnable {
                        inputAccount()
                    }, 3)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付宝账号输入有误")
                }
            })
            .create()
            .execute()
    }

    fun inputAccount() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams(WidgetConstant.EDITTEXT, 3, false, mUserName!!)
            .setNodeParams("下一步")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputPsw()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付宝账号输入有误")
                }
            })
            .create()
            .execute()
    }

    /**
     * 输入支付宝密码
     */
    fun inputPsw() {
        AdbScriptController.Builder()
            .setXY("540,850", 3000)
            .setText(mUserPsw!!)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    choosePayChannel()
                }

                override fun onTaskFailed(failedMsg: String) {
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

                override fun onTaskFailed(failedMsg: String) {
                    // mTaskListener?.onTaskFailed("支付宝登录失败，请检查是否设置安全验证登录方式")
                    responTaskFailed("支付宝登录失败，请检查是否设置安全验证登录方式")
                }
            })
            .create()
            .execute()
    }

    fun responTaskSuccess() {
        mLoginFailedCount = 0
        responSucceed()
    }

    fun responTaskFailed(msg: String) {
        mLoginFailedCount = 0
        responFailed(msg)
    }
}