package com.accessibility.service.function

import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BasePayService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.PackageManagerUtils

/**
 * Description:
 * Created by Quinin on 2019-09-07.
 **/
class ConfirmPayResult(myAccessibilityService: MyAccessibilityService) :
    BasePayService(myAccessibilityService) {


    override fun startService() {
        confirmGood()
    }

    private fun confirmGood() {
        val mallName = TaskDataUtil.instance.getMall_name()
        if (!TextUtils.isEmpty(mallName)) {
            dealPayResult()

        } else responFailed("店铺名字不能为空")

    }

    /**
     * 处理订单是否支付失败
     */
    private fun dealPayResult() {
        val mallName = TaskDataUtil.instance.getMall_name()
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("查看全部", 1, 3)
            .setNodeParams(mallName!!, 1, false, 3)  //店铺名
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //支付失败
                    L.i("检测到商店:$mallName")
                    confirmPayAgain()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("未下单，返回继续查找下单")
                    //restartTask()
                    responTaskFailed("$mallName 商品检测不到，返回继续查找下单")
                }
            })
            .create()
            .execute()
    }

    /**
     * 判断是否付款
     */
    private fun confirmPayAgain() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("成功", 1, false, 2)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("参团拼单已付款")
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    try {
                        val sharedNodes =
                            myAccessibilityService.rootInActiveWindow?.findAccessibilityNodeInfosByText(
                                "待分享"
                            )
                        when {
                            sharedNodes?.size!! > 1    //支付成功
                            -> {
                                L.i("发起拼单已付款")
                                responSucceed()
                            }
                            myAccessibilityService.rootInActiveWindow?.findAccessibilityNodeInfosByText(
                                "待发货"
                            )?.size!! > 1 -> {
                                L.i("参团成功")
                                responSucceed()
                            }
                            else -> NodeController.Builder()
                                .setNodeService(myAccessibilityService)
                                .setNodeParams("支付", 1, false, 2)
                                .setTaskListener(object : TaskListener {
                                    override fun onTaskFinished() {
                                        L.i("没有支付成功，继续去支付")
                                        dealPayFailed()
                                    }

                                    override fun onTaskFailed(failedMsg: String) {
                                        L.i("有订单号却找不到订单去支付")
                                        responTaskFailed(failedMsg)
                                    }
                                })
                                .create()
                                .execute()
                        }
                    } catch (e: Exception) {
                        L.e(e.message, e)
                        responTaskFailed("待分享异常")
                    }
                }

            })
            .create()
            .execute()
    }


    /**
     * 支付失败
     */
    private fun dealPayFailed() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("去支付", 0, true, 3)
            //.setNodeParams("付款", 1, true, 3)
            .setNodeParams("更多支付方式", 1, true, 3)
            .setNodeParams("支付宝", 1, true, 3)
            .setNodeParams("立即支付", 1, true, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputPayPsw()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("未下单，返回继续查找下单")
                    //restartTask()
                    responTaskFailed("找不到待支付的订单")
                }
            })
            .create()
            .execute()
    }

    /*private fun restartTask() {
        myAccessibilityService.setCurPageType(PageEnum.START_PAGE)
        PackageManagerUtils.restartApplication(
            Constant.BUY_TOGETHER_PKG,
            "${MyAccessibilityService.PKG_PINDUODUO}.ui.activity.MainFrameActivity"
        )
    }*/

    /**
     * 输入支付¬密码
     */
    private fun inputPayPsw() {
        myAccessibilityService.postDelay(Runnable {
            NodeController.Builder()
                .setNodeService(myAccessibilityService)
                //.setNodeParams("仍然支付", 0, 5, true)
                .setNodeParams("立即付款", 1, 6)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        myAccessibilityService.apply {
                            postDelay(Runnable {
                                try {
                                    val nodeHuaBei =
                                        this.rootInActiveWindow?.findAccessibilityNodeInfosByText("花呗")
                                    if (nodeHuaBei != null && nodeHuaBei.size >= 1) {
                                        NodeController.Builder()
                                            .setNodeService(this)
                                            .setNodeParams("花呗", 1, 5)
                                            .setNodeParams("账户余额", 1, 5)
                                            .setTaskListener(object : TaskListener {
                                                override fun onTaskFinished() {
                                                    isInputPswPage()
                                                }

                                                override fun onTaskFailed(failedMsg: String) {
                                                    isInputPswPage()
                                                }
                                            })
                                            .create()
                                            .execute()
                                    } else {
                                        isInputPswPage()
                                    }
                                } catch (e: Exception) {
                                    isInputPswPage()
                                }
                            }, 2)
                        }
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed("支付宝付款环节失败-$failedMsg")
                    }
                })
                .create()
                .execute()
        }, 2)
    }

    /**
     * 是否是输入密码的界面
     */
    private fun isInputPswPage() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("忘记密码", 1, false, 8)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到支付界面")
                    try {
                        adbInputPsw()
                    } catch (e: Exception) {
                        L.e(e.message, e)
                        responTaskFailed("输入密码发生异常")
                    }
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("未跳转到输入密码界面:$failedMsg")
                    responTaskFailed(failedMsg)
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
                        //支付成功
                        myAccessibilityService.performBackClick(5, object : AfterClickedListener {
                            override fun onClicked() {
                                L.i("密码已输入，准备重启PDD")
                                //responTaskSuccess() 不能够直接返回支付成功的结果，需要重新打开Pdd去检查
                                reconfirmPayResult()
                            }
                        })
                    }
                })
                .create()
                .execute()
        }else{
            L.i("rootInActiveWindow is null")
            responTaskFailed("rootInActiveWindow is null")
        }
    }

    /**
     * 输入密码后，重新确认是否支付成功
     */
    private fun reconfirmPayResult() {
        myAccessibilityService.apply {
            startPddTask()
            postDelay(Runnable {
                setCurPageType(PageEnum.START_PAGE)

                PackageManagerUtils.killApplication(Constant.ALI_PAY_PKG)
                PackageManagerUtils.restartApplication(
                    MyAccessibilityService.PKG_PINDUODUO,
                    "${MyAccessibilityService.PKG_PINDUODUO}.ui.activity.MainFrameActivity"
                )
            }, 5)
        }
    }


    fun responTaskSuccess() {
        responSucceed()
    }

    fun responTaskFailed(msg: String) {
        responFailed(msg)
    }

}