package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:评论任务
 * Created by Quinin on 2019-08-12.
 **/
class CommentTaskService(val myAccessibilityService: MyAccessibilityService) : BaseAcService(myAccessibilityService) {

    override fun startService() {
        enterMyOrder()
    }

    /**
     * 进入我的订单
     */
    private fun enterMyOrder() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("个人中心", 0, 30)
            .setNodeParams("我的订单")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    confirmSigned()
                }

                override fun onTaskFailed(failedMsg: String) {
                    dealAccident()
                }
            })
            .create()
            .execute()
    }

    /**
     * 确认货物已签收
     */
    private fun confirmSigned() {
        val mallName = TaskDataUtil.instance.getMall_name()
        if (mallName.isNullOrEmpty()) {
            L.i("店铺名字为空")
            responFailed("店铺名字为空")
            return
        }
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("待收货")
            .setNodeParams(mallName, 0, false, 5)
            .setNodeParams("确认收货", 0, 5)
            //.setNodeParams("未签收", 1, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {

                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("没有待收货的商品")
                }
            })
            .create()
            .execute()
    }

    /**
     * 处理找不到搜索入口的突发事件，例如登录后跳转到红包界面
     */
    private fun dealAccident() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("见面福利", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    myAccessibilityService.performBackClick(0, object : AfterClickedListener {
                        override fun onClicked() {
                            L.i("跳转到见面福利界面，返回主页")
                            startService()
                        }
                    })
                }

                override fun onTaskFailed(failedMsg: String) {
                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        .setNodeParams("直接退出", 0, 5)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                L.i("跳转到见面福利界面，弹框提示，返回主页")
                                startService()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                responFailed("遇到其他突发事件，找不到搜索入口")
                            }

                        })
                        .create()
                        .execute()
                }
            })
            .create()
            .execute()
    }
}