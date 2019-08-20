package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.ADB_XY
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:下订单服务
 * Notice:
 * 1、下订单的方式：
 *      参团购买:0
 *      拼单购买:1
 *      单独购买:2
 * 2、选择规格：竖着方向找不到，需要横着找
 * 3、需要
 * Created by Quinin on 2019-08-10.
 **/
class BuyGoods(val nodeService: MyAccessibilityService) : BaseAcService(nodeService) {

    override fun startService() {
        confirmBuyType()
    }

    /**
     * 确定购买方式
     */
    private fun confirmBuyType() {
        val buyType = TaskDataUtil.instance.getBuy_type()
        L.i("购买方式：$buyType")
        when (buyType) {
            0 -> buyByJoin()
            1 -> buyWithOther()
            2 -> buyBySelf()
            else -> responFailed("购买方式下发错误：$buyType")
        }
    }

    /**
     * 参团购买
     */
    private fun buyByJoin() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("查看更多")
            .setNodeParams("插队拼单",0,8,true)
            .setNodeParams("去拼单",0,5,true)
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("参团购买失败，换成发起拼单")
                    buyWithOther()
                }

            })
            .create()
            .execute()
    }

    /**
     * 单独购买
     */
    private fun buyBySelf() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("单独购买")
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("参团购买失败，换成发起拼单")
                    buyWithOther()
                }

            })
            .create()
            .execute()
    }

    /**
     * 发起拼单
     */
    private fun buyWithOther() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("发起拼单")
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    chooseAddress()
                }
            })
            .create()
            .execute()
    }

    /**
     * 选择商品参数
     */
    private fun chooseInfo() {
        val choose_info = TaskDataUtil.instance.getChoose_info()
        L.i("商品参数size：${choose_info?.size}")
        if (choose_info == null || choose_info.isEmpty()) {
            responFailed("商品的选择参数不则会给你缺")
            return
        }

        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    //responFailed("选择商品规格失败") todo

                }

                override fun onTaskFinished() {
                    L.i("商品选择完成，准备支付")
                    //createAddress()
                    chooseAddress()
                }
            })
            .setNodeParams(choose_info, true)
            .setNodeParams("确定")
            .create()
            .execute()
    }

    private fun chooseInfoFailed(chooseInfo: List<String>) {

    }

    /**
     * 选择收货人的地址
     */
    private fun chooseAddress() {
        FillAddressService(nodeService)
            .setTaskFinishedListener(object : TaskListener {
                override fun onTaskFinished() {
                    choosePayChannel()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .doOnEvent()
    }


    /**
     * 选择支付渠道
     */
    private fun choosePayChannel() {
        L.i("新增地址完成，选择支付方式：")
        val payXY = ADB_XY.PAY_NOW.ali_pay

        AdbScriptController.Builder()
            .setSwipeXY(ADB_XY.PAY_NOW.origin_swipe_up, ADB_XY.PAY_NOW.target_swipe_up)
            .setXY(ADB_XY.PAY_NOW.more_pay_channel)
            .setSwipeXY(ADB_XY.PAY_NOW.origin_swipe_up, ADB_XY.PAY_NOW.target_swipe_up)
            .setXY(payXY)
            .setXY(ADB_XY.PAY_NOW.pay_now_btn)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    payByAlipay()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .create()
            .execute()

    }


    /**
     * 支付宝支付
     */
    private fun payByAlipay() {
        AliPayLogin(nodeService)
            .login(object : TaskListener {
                override fun onTaskFinished() {
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
    }

}