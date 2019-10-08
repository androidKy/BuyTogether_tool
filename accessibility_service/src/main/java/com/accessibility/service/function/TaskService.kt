package com.accessibility.service.function

import android.os.Handler
import android.os.Looper
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
    private var mScanGoodTime: Int = 0
    private var mExceptionHappened: Boolean = false
    private var mTaskType: String = "1"
    private var mOriginTaskType: String = ""

    companion object {
        const val MSG_SCAN_FINISHED = 1000
        const val MSG_TALK_FINISHED = 2000
        const val MSG_COLLECT_FINISHED = 3000
    }

    fun setScreenDensity(width: Int, height: Int): TaskService {
        mScreenWidth = width
        mScreenHeight = height
        return this
    }

    override fun doOnEvent() {
        try {
            mTaskProgress.clear()
            // var taskType =
            mOriginTaskType = TaskDataUtil.instance.getTask_type().toString()
            L.i("开始任务之前：taskType = $mOriginTaskType")
            mTaskType = mOriginTaskType.replace("4", "")
            finishOneTask("0")
            /* when (taskType) {
                 2, 23, 24, 234 -> talkWithSaler()
 //                2,23,24,234,1234 -> talkWithSaler()
                 3, 32, 34 -> collectGoods()
                 4 -> buyGoods()
                 else -> {
                     nodeService.postDelay(Runnable {
                         scanGoods()
                     }, 2)
                 }
             }*/
        } catch (e: Exception) {
            L.e(e.message, e)
            responFailed("任务过程中出现异常")
        }
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_SCAN_FINISHED -> {
                saveFinishProgress("1")
                finishOneTask("1")
            }

            MSG_TALK_FINISHED -> {
                saveFinishProgress("2")
                finishOneTask("2")
            }

            MSG_COLLECT_FINISHED -> {
                saveFinishProgress("3")
                finishOneTask("3")
            }

        }
        false
    }

    /**
     * 保存当前完成的任务类型
     */
    private fun saveFinishProgress(progress: String) {
        mTaskProgress.append(progress)
        saveTaskProgress(mTaskProgress.toString())
    }

    private fun finishOneTask(progress: String) {
        mTaskType = mTaskType.replace(progress, "")

        L.i("完成任务类型：$progress mTaskType: $mTaskType")

        if (mTaskType.isEmpty()) {
            if (mOriginTaskType.contains("4")) {
                buyGoods()
            } else responSuccess()
        } else {
            //随机打乱顺序
            val arrayType = mTaskType.toCharArray()
            val startType = arrayType.random().toString()
            L.i("开始任务类型：$startType")
            when (startType) {
                "1" -> scanGoods()
                "2" -> talkWithSaler()
                "3" -> collectGoods()
            }
        }
    }

    /**
     * 开始做任务
     */
    private fun scanGoods() {
        mScanGoodTime = (30..120).random()
        try {
            val goodName = TaskDataUtil.instance.getGoods_name()
            NodeController.Builder()
                .setNodeService(nodeService)
                .setNodeParams(goodName!!, 1, 16)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("已跳转到商品详情，开始滑动")
                        startScroll()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("跳转不到商品详情2")
                        responFailed("跳转不到商品详情")
                    }
                })
                .create()
                .execute()

        } catch (e: Exception) {
            if (!mExceptionHappened) {
                L.i("无障碍服务崩溃：${e.message}")
                mExceptionHappened = true
                //nodeService.sendBroadcast(Intent(MyAccessibilityService.ACTION_EXCEPTION_RESTART))
                responFailed("无障碍服务崩溃: ${e.message}")
            }
        }
    }

    private fun startScroll() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) {
                        L.i("scanGoods()... nodeInfo为空")
                        scanGoods()
                        return
                    }

                    nodeInfo?.apply {
                        L.i("recyclerView was found: ${nodeInfo.className}")
                        ScrollUtils(nodeService, nodeInfo)
                            .setForwardTotalTime(mScanGoodTime)
                            .setScrollListener(ForwardListenerImpl())
                            .scrollForward()
                    }
                }
            })
            .getSingleNodeByClassName(nodeService, WidgetConstant.RECYCLERVIEW)
    }

    inner class ForwardListenerImpl : ScrollUtils.ScrollListener {

        override fun onScrollFinished(nodeInfo: AccessibilityNodeInfo) {
            L.i("浏览完成，进行下一步任务")
            NodeController.Builder()
                .setNodeService(nodeService)
                .setNodeParams("顶部", 0, 4)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        mHandler.sendEmptyMessage(MSG_SCAN_FINISHED)
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("回到顶部失败")

                    }
                })
                .create()
                .execute()
        }
    }

    /**
     * 2:与卖家沟通
     */
    private fun talkWithSaler() {
        val talkMsg = TaskDataUtil.instance.getTalk_msg()
        if (talkMsg.isNullOrEmpty()) {
            responFailed("聊天信息不能为空")
            return
        }

        val isTalked =
            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_ALREADY_TALKED)
        if (isTalked) {
            L.i("已聊过天")
            mHandler.sendEmptyMessage(MSG_TALK_FINISHED)
        } else {
            L.i("没有聊过天")
            startTalk(talkMsg)
        }
    }

    /**
     * 正式开始聊天
     */
    private fun startTalk(talkMsg: String) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedMsg: String) {
                    responFailed("与客服沟通失败")
                }

                override fun onTaskFinished() {
                    SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                        .put(Constant.KEY_ALREADY_TALKED, true)
                    //continueTask()
                    nodeService.performBackClick(5,object:AfterClickedListener{
                        override fun onClicked() {
                            mHandler.sendEmptyMessage(MSG_TALK_FINISHED)
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
                    mHandler.sendEmptyMessage(MSG_COLLECT_FINISHED)
                }

                override fun onTaskFinished() {
                    L.i("商品已收藏")
                    mHandler.sendEmptyMessage(MSG_COLLECT_FINISHED)
                }
            })
            .setNodeParams("收藏",0,6)
            .create()
            .execute()
    }

    /**
     * 4:购买商品
     */
    private fun buyGoods() {
        L.i("购买商品，并且选择商品各个参数")
       /* mTaskProgress.append("4")
        saveTaskProgress(mTaskProgress.toString())*/
        saveFinishProgress("4")
        BuyGoods(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .startService()
    }

    /**
     * 保存任务进度
     */
    private fun saveTaskProgress(progress: String) {
        //任务进度
        SPUtils.getInstance(nodeService, Constant.SP_TASK_FILE_NAME)
            .put(Constant.KEY_TASK_PROGRESS, progress, true)
    }


    private fun responFailed(failedMsg: String) {
        mTaskFinishedListener?.onTaskFailed(failedMsg)
    }

    private fun responSuccess() {
        mTaskFinishedListener?.onTaskFinished()
    }
}