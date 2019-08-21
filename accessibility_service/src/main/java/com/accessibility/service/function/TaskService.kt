package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.*
import com.safframework.log.L
import com.utils.common.SPUtils

/**
 * Description:做任务
 * Created by Quinin on 2019-07-09.
 **/
class TaskService constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    // companion object : com.utils.common.SingletonHolder<TaskService, MyAccessibilityService>(::TaskService)

    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mTaskProgress: StringBuilder = StringBuilder()

    fun setScreenDensity(width: Int, height: Int): TaskService {
        mScreenWidth = width
        mScreenHeight = height
        return this
    }

    override fun doOnEvent() {
        try {
            mTaskProgress.clear()
            var taskType = TaskDataUtil.instance.getTask_type()
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
            L.i("浏览完成,根据任务类型是否需要进行下一步任务")
            mTaskProgress.append("1")
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
        var talkMsg = TaskDataUtil.instance.getTalk_msg()
        if (talkMsg.isNullOrEmpty()) {
            responFailed("聊天信息不能为空")
            return
        }

        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    responFailed("与客服沟通失败")
                }

                override fun onTaskFinished() {
                    mTaskProgress.append("2")
                    nodeService.performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            TaskDataUtil.instance.getTask_type()?.apply {
                                when (this) {
                                    23, 234, 123, 1234 -> collectGoods()
                                    24, 124, 324 -> buyGoods()
                                    else -> responSuccess()
                                }
                            }
                        }
                    })
                }
            })
            .setNodeParams("客服")
            .setNodeParams(WidgetConstant.EDITTEXT, 3, false, talkMsg)
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
                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.商品已收藏")
                    mTaskProgress.append("3")
                    TaskDataUtil.instance.getTask_type()?.apply {
                        when (this) {
                            1234, 134, 234, 34 -> buyGoods()
                            132, 32, 324 -> talkWithSaler()

                            else -> responSuccess()
                        }
                    }
                }

                override fun onTaskFinished() {
                    L.i("商品已收藏")
                    mTaskProgress.append("3")
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
        BuyGoods(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    mTaskProgress.append("4")
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .startService()
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
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.支付失败")
                    responFailed(failedMsg)
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
     * 保存任务进度
     */
    private fun saveTaskProgress(progress: String) {
        SPUtils.getInstance(nodeService, Constant.SP_TASK_FILE_NAME)
            .put(Constant.KEY_TASK_PROGRESS, progress)
    }


    private fun responFailed(failedMsg: String) {
        saveTaskProgress(mTaskProgress.toString())
        mTaskFinishedListener?.onTaskFailed(failedMsg)
    }

    private fun responSuccess() {
        saveTaskProgress(mTaskProgress.toString())
        mTaskFinishedListener?.onTaskFinished()
    }
}