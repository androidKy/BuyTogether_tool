package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.ADB_XY
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.NodeUtils
import com.accessibility.service.util.ScrollUtils
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:做任务
 * Created by Quinin on 2019-07-09.
 **/
class TaskService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : com.utils.common.SingletonHolder<TaskService, MyAccessibilityService>(::TaskService)

    private var mIsScaningGoods: Boolean = false    //判断是否正在浏览,控制同一时刻只有一个收到一个节点事件
    private var mAlreadyScaned: Boolean = false   //是否已浏览，控制允许是否继续收到事件

    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0

    fun setScreenDensity(width: Int, height: Int): TaskService {
        mScreenWidth = width
        mScreenHeight = height
        return this
    }

    override fun doOnEvent() {
        try {
            val taskType = TaskDataUtil.instance.getTask_type()
            L.i("taskType = $taskType")
            when (taskType) {
                2, 23, 24, 234 -> talkWithSaler()
                3, 32, 34 -> collectGoods()
                4 -> buyGoods()
                else -> {
                    nodeService.postDelay(Runnable {
                        scanGoods()
                    }, 2)
                }
            }
        } catch (e: Exception) {
            L.e(e.message, e)
            responFailed("任务过程中出现异常")
        }
    }

    /**
     * 开始做任务
     */
    private fun scanGoods() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.apply {
                        L.i("recyclerView was found: ${nodeInfo.className}")
                        ScrollUtils(nodeService, nodeInfo)
                            .setForwardTotalTime(10)
                            .setScrollListener(ForwardListenerImpl())
                            .scrollForward()
                    }
                }
            })
            .getSingleNodeByClassName(nodeService, WidgetConstant.RECYCLERVIEW)
    }

    inner class ForwardListenerImpl : ScrollUtils.ScrollListener {

        override fun onScrollFinished(nodeInfo: AccessibilityNodeInfo) {
            L.i("向上滑动完成，开始向下拉")
            ScrollUtils(nodeService, nodeInfo)
                .setBackwardTime(10)
                .setScrollListener(BackwardListenerImpl())
                .scrollBackward()
        }
    }

    inner class BackwardListenerImpl : ScrollUtils.ScrollListener {
        override fun onScrollFinished(nodeInfo: AccessibilityNodeInfo) {
            L.i("浏览完成  ，根据任务类型是否需要进行下一步任务")
            TaskDataUtil.instance.getTask_type()?.apply {
                when (this) {
                    12, 123, 124, 1234 -> talkWithSaler()
                    13, 132, 134 -> collectGoods()
                    14, 142, 143 -> buyGoods()
                    else -> responSuccess()
                }
            }
        }
    }

    /**
     * 2:与卖家沟通
     */
    private fun talkWithSaler() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                    responFailed("$failedText node was not found.")
                }

                override fun onTaskFinished() {
                    nodeService.performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            TaskDataUtil.instance.getTask_type()?.apply {
                                when (this) {
                                    23, 123, 1234 -> collectGoods()
                                    24, 124, 324 -> buyGoods()
                                    else -> responSuccess()
                                }
                            }
                        }
                    })
                }
            })
            .setNodeParams("客服")
            .setNodeParams("com.xunmeng.pinduoduo:id/ai9", 2, false, TaskDataUtil.instance.getTalk_msg()!!)
            .setNodeParams("发送")
            .create()
            .execute()

    }

    /**
     * 3:收藏商品
     */
    private fun collectGoods() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                }

                override fun onTaskFinished() {
                    L.i("商品已收藏")
                    TaskDataUtil.instance.getTask_type()?.apply {
                        when (this) {
                            1234, 134, 234, 34 -> buyGoods()
                            132, 32, 324 -> talkWithSaler()

                            else -> responSuccess()
                        }
                    }
                }
            })
            .setNodeParams("收藏")
            .create()
            .execute()
    }

    /**
     * 4:购买商品
     */
    private fun buyGoods() {
        L.i("购买商品，并且选择商品各个参数")

        val choose_info = TaskDataUtil.instance.getChoose_info()
        L.i("商品参数size：${choose_info?.size}")
        if (choose_info == null || choose_info.isEmpty() || choose_info.size == 1) {
            L.i("商品参数为空")
            NodeController.Builder()
                .setNodeService(nodeService)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                    }

                    override fun onTaskFinished() {
                        L.i("商品选择完成，准备支付")
                        //createAddress()
                        payNow()
                    }
                })
                .setNodeParams("发起拼单")
                .create()
                .execute()
            return
        }

        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                }

                override fun onTaskFinished() {
                    L.i("商品选择完成，准备支付")
                    //createAddress()
                    payNow()
                }
            })
            .setNodeParams("发起拼单")  //todo 是拼单还是单独购买
            .setNodeParams(choose_info, true)
            .setNodeParams("确定")
            .create()
            .execute()
    }

    /**
     * 立即支付
     */
    private fun payNow() {
        val taskDataUtil = TaskDataUtil.instance
        val buyerName = taskDataUtil.getBuyer_name()
        val buyerPhone = taskDataUtil.getBuyer_phone()
        val streetName = taskDataUtil.getStreet()
        val provinceName = taskDataUtil.getProvince()
        val cityName = taskDataUtil.getCity()
        val districtName = taskDataUtil.getDistrict()

        if (buyerName.isNullOrEmpty()) {
            responFailed("收货信息为空")
            return
        }


        AdbScriptController.Builder()
            .setXY(ADB_XY.PAY_NOW.add_address, 3000L)
            .setXY(ADB_XY.PAY_NOW.name)
            .setText(buyerName)
            .setXY(ADB_XY.PAY_NOW.phone)
            .setText(buyerPhone!!)
            .setXY(ADB_XY.PAY_NOW.detailed)
            .setText(streetName!!)
            .setXY(ADB_XY.PAY_NOW.choose_local)     //todo 选省份和城市还有区域要下滑选指定的地址
            .setXY(ADB_XY.PAY_NOW.province)
            .setXY(ADB_XY.PAY_NOW.city)
            .setXY(ADB_XY.PAY_NOW.region)
            .setXY(ADB_XY.PAY_NOW.save_address)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //选择支付渠道
                    choosePayChannel()
                    //payByOther()
                }

                override fun onTaskFailed(failedText: String) {
                    //支付失败
                    L.i("$failedText was not found.")
                    //payByOther()
                }

            })
            .create()
            .execute()
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

                override fun onTaskFailed(failedText: String) {

                }
            })
            .create()
            .execute()

    }

    /**
     * 在QQ好友中找好友代付
     */
    private fun payByOther() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("支付成功，下单完成，重新开始下一轮任务")
                    mTaskFinishedListener?.onTaskFinished()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.支付失败")

                }

            })
            .setNodeParams("立即支付")
            .setNodeParams("找好友代付")
            .setNodeParams("Quinin1993")    //支付好友的昵称
            .setNodeParams("完成")
            .setNodeParams("发送")
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
                    responSuccess()
                }

                override fun onTaskFailed(failedText: String) {
                    responFailed(failedText)
                }
            })
    }


    private fun responFailed(failedMsg: String) {
        mTaskFinishedListener?.onTaskFailed(failedMsg)
    }

    private fun responSuccess() {
        mTaskFinishedListener?.onTaskFinished()
    }
}