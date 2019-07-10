package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.util.*
import com.safframework.log.L

/**
 * Description:做任务
 * Created by Quinin on 2019-07-09.
 **/
class TaskService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<TaskService, MyAccessibilityService>(::TaskService)

    private var mIsScaningGoods: Boolean = false    //判断是否正在浏览,控制同一时刻只有一个收到一个节点事件
    private var mScaned: Boolean = false   //是否已浏览，控制允许是否继续收到事件


    override fun doOnEvent() {
        val taskType = TaskDataUtil.instance.getTask_type()
        L.i("taskType = $taskType")
        when (taskType) {
            2 -> talkWithSaler()
            3 -> collectGoods()
            4 -> buyGoods()
            else -> scanGoods()
        }
    }

    /**
     * 开始做任务
     */
    fun scanGoods() {
        if (mIsScaningGoods)
            return
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.apply {
                        L.i("recyclerView was found: ${nodeInfo.className}")
                        if (!mScaned) {
                            mIsScaningGoods = true
                            ScrollUtils(nodeService, nodeInfo)
                                .setForwardTotalTime(10)
                                .setScrollListener(ForwardListenerImpl())
                                .scrollForward()
                        }
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
        override fun onScrollFinished(nodeInfo: AccessibilityNodeInfo) { L.i("浏览完成  ，v  根据任务类型是否需要进行下一步任务")
            mIsScaningGoods = false
            mScaned = true
            TaskDataUtil.instance.getTask_type()?.apply {
                when (this) {
                    12 -> talkWithSaler()
                    13 -> collectGoods()
                    14 -> buyGoods()
                }
            }
        }
    }

    /**
     * 2:与卖家沟通
     */
    fun talkWithSaler() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.apply {
                        nodeService.performViewClick(this, 1)
                    }
                }
            })
            .getNodeByFullText(nodeService, "客服")
    }

    /**
     * 3:收藏商品
     */
    fun collectGoods() {

    }

    /**
     * 4:购买商品
     */
    fun buyGoods() {

    }
}