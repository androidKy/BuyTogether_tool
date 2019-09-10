package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
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
class CommentTaskService(val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {

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
            .setNodeParams(
                "确认收货",
                0,
                isClicked = true,
                isScrolled = true,
                timeout = 5,
                findNextFlag = false
            )
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    checkIsSigned()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("${mallName}-商品找不到")
                }
            })
            .create()
            .execute()
    }

    private fun checkIsSigned() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("未签收", 1, false, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("包裹未签收")
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("包裹已签收")
                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        .setNodeParams("确认收货", 1, 3)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                L.i("开始评论")
                                startComment()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                L.i("评论失败：$failedMsg")
                            }

                        })
                        .create()
                        .execute()
                }
            })
            .create()
            .execute()
    }


    /**
     * 开始评论
     */
    private fun startComment() {
        var commentContent = TaskDataUtil.instance.getCommentContent()
        if (commentContent.isNullOrEmpty()) {
            commentContent = ""
        }
//        val xScore = "680"
        val xScore = "750"
        AdbScriptController.Builder()
            .setXY("$xScore,465")
            .setXY("$xScore,565")
            .setXY("$xScore,665")
            .setXY("540,850")      //评价输入框的XY
            .setText(commentContent)
            // .setXY("540,1500")      //提交评价
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        .setNodeParams("提交评价")
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                isCommentSucceed()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                //responFailed("评论失败：$failedMsg")
                            }
                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("评论失败：$failedMsg")
                }

            })
            .create()
            .execute()
    }

    /**
     * 是否评论成功
     */
    private fun isCommentSucceed() {
        myAccessibilityService.postDelay(Runnable {
            NodeController.Builder()
                .setNodeService(myAccessibilityService)
                .setNodeParams("提交评价", 0, 6)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        //responSucceed()
                        L.i("评论失败")
                        startComment()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        //responFailed("评论失败：$failedMsg can not be found.")
                        L.i("评论成功")
                        responSucceed()
                    }
                })
                .create()
                .execute()
        }, 2)

    }

    /**
     * 处理找不到搜索入口的突发事件，例如登录后跳转到红包界面
     */
    private fun dealAccident() {
        myAccessibilityService.performBackClick(0, object : AfterClickedListener {
            override fun onClicked() {
                L.i("返回主页")
                startService()
            }
        })
        /* NodeController.Builder()
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
             .execute()*/
    }
}