package com.accessibility.service.function

import android.text.TextUtils
import android.util.SparseIntArray
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-09-07.
 **/
class ConfirmPayResult(val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {


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
                    responFailed("未下单，返回继续查找下单")
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
            .setNodeParams("拼单成功", 1, false, 2)
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
                        if (sharedNodes?.size!! > 1)    //支付成功
                        {
                            L.i("发起拼单已付款")
                            responSucceed()
                        } else {
                            NodeController.Builder()
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
            .setNodeParams("立即", 1, true, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputPayPsw()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("未下单，返回继续查找下单")
                   // restartTask()
                    responFailed("未下单，返回继续查找下单")
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
                .setNodeParams("立即", 1, 5)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        NodeController.Builder()
                            .setNodeService(myAccessibilityService)
                            .setNodeParams("忘记密码", 1, false, 8)
                            .setTaskListener(object : TaskListener {
                                override fun onTaskFinished() {
                                    L.i("已跳转到支付界面")
                                    adbInputPsw()
                                }

                                override fun onTaskFailed(failedMsg: String) {
                                    L.i("未跳转到输入密码界面:$failedMsg")
                                    responTaskFailed(failedMsg)
                                }
                            })
                            .create()
                            .execute()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed("支付宝付款环节失败-$failedMsg")
                    }
                })
                .create()
                .execute()
        }, 3)
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
        AdbScriptController.Builder()
            .setXY(regularPsw(payPsw!!))
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("支付密码输入错误")
                }

                override fun onTaskFinished() {
                    //支付成功 todo需要确认支付成功才上报
                    myAccessibilityService.postDelay(Runnable {
                        responTaskFailed("重新打开PDD检查是否支付成功")
                    }, 3)
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
            //val itemHeight = 140
            //val itemStartY = 1135
            val itemHeight = 150
            val itemStartY = 1280


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


    fun responTaskSuccess() {
        responSucceed()
    }

    fun responTaskFailed(msg: String) {
        responTaskFailed(msg)
    }

}