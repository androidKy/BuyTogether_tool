package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.CommentStatus
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:评论任务
 * Created by Quinin on 2019-08-12.
 **/
class CommentTaskService(val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {


    var mCommentStatusListener: CommentStatusListener? = null

    override fun startService() {
        enterMyOrder()
    }

    interface CommentStatusListener {
        fun responCommentStatus(status: Int)
    }

    fun setCommentStatusListener(listener: CommentStatusListener): CommentTaskService {
        mCommentStatusListener = listener
        return this
    }


    /**
     * 进入我的订单
     */
    private fun enterMyOrder() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("拒绝", 0, 4, true)
            .setNodeParams("个人中心", 0, 4)
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
            mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_FAILED)
            responFailed("店铺名字为空")
            return
        }
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
//            .setNodeParams("待收货")
            .setNodeParams(mallName, 1, false, 5)
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
                    isFindComment()
                }
            })
            .create()
            .execute()
    }

    private fun isFindComment() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("立即评价", 0, true, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    startComment()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("找不到立即评价，尝试去找追加评价")

                    isAdditioncalComment()
                }

            })
            .create()
            .execute()
    }

    private fun isAdditioncalComment() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("已评价",0,false,3,true)
            .setNodeParams("追加评价", 0, false, 4)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_SUCCESS)
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("找不到追加评价")
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
                    mCommentStatusListener?.responCommentStatus(CommentStatus.NOT_SIGNED)

                    responFailed("包裹未签收")
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
//                                noComment()
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
     *  不进行评论，单纯确认收货
     */
    private fun noComment() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("提交评价", 0, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到提交评价，确认已收货")
                    mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_SUCCESS)
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
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
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("提交评价", 0, false, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到评价界面")
                    // A型机坐标
                    val xScore = "680"
                    // B型机坐标
                    //        val xScore = "750"
                    AdbScriptController.Builder()
                        .setXY("$xScore,465")
                        .setXY("$xScore,565")
                        .setXY("$xScore,665")
                        .setXY("540,850")      //评价输入框的XY
                        .setText(commentContent)
                        .setXY("540,1500")      //提交评价
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {

                                L.i("成功提交评价")
                                isCommentSucceed()


                            }

                            override fun onTaskFailed(failedMsg: String) {
                                mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_FAILED)
                                responFailed("评论失败：$failedMsg")
                            }

                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("未跳转到评价界面")
                    confirmSigned()
                }

            })
            .create()
            .execute()
    }

    /**
     *  无限寻找 个人中心，人工操作评论之后，方便上传数据到后台。
     */
    private fun deadLoop() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("个人中心",0,false,5)
            .setTaskListener(object :TaskListener{
                override fun onTaskFailed(failedMsg: String) {
                    mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_SUCCESS)
                    responSucceed()
                }

                override fun onTaskFinished() {
                    deadLoop()
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
                        startComment()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        //responFailed("评论失败：$failedMsg can not be found.")
                        L.i("评论成功")
                        mCommentStatusListener?.responCommentStatus(CommentStatus.COMMENT_MISSION_SUCCESS)
                        responSucceed()
                    }
                })
                .create()
                .execute()
        }, 5)

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

    }
}