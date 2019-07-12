package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.*
import com.safframework.log.L

/**
 * Description:做任务
 * Created by Quinin on 2019-07-09.
 **/
class TaskService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<TaskService, MyAccessibilityService>(::TaskService)

    private var mIsScaningGoods: Boolean = false    //判断是否正在浏览,控制同一时刻只有一个收到一个节点事件
    private var mAlreadyScaned: Boolean = false   //是否已浏览，控制允许是否继续收到事件


    override fun doOnEvent() {
        val taskType = TaskDataUtil.instance.getTask_type()
        L.i("taskType = $taskType")
        when (taskType) {
            2, 23, 24, 234 -> talkWithSaler()
            3, 32, 34 -> collectGoods()
            4 -> buyGoods()
            else -> scanGoods()
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
                    else -> L.i("任务完成")
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
                }

                override fun onTaskFinished() {
                    L.i("聊天后返回")
                    nodeService.performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            TaskDataUtil.instance.getTask_type()?.apply {
                                when (this) {
                                    23, 123, 1234 -> collectGoods()
                                    24, 124, 324 -> buyGoods()
                                    else -> L.i("任务完成")
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

                            else -> L.i("任务完成")
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
        if (choose_info == null) {
            L.i("商品参数为空")
            return
        }

        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {

                }

                override fun onTaskFinished() {
                    L.i("商品选择完成，准备支付")
                    //createAddress()
                    nodeService.postDelay(Runnable {
                        L.i("check nodes")
                        iteratorRootView(nodeService.rootInActiveWindow)
                    }, 3)
                }
            })
            .setNodeParams("发起拼单")  //todo 是拼单还是单独购买
            .setNodeParams(choose_info, true)
            .setNodeParams("确定")
            .create()
            .execute()
    }

    private fun iteratorRootView(nodeView: AccessibilityNodeInfo) {
        // val rootView =
        L.i("rootView childSize = ${nodeView.childCount}")

        for (i in 0 until nodeView.childCount) {
            val childNode = nodeView.getChild(i)
            L.i("childNode: ${childNode.className} text: ${childNode.text}")

            if (childNode.childCount > 0) {
                iteratorRootView(childNode)
            }
/*
            for (j in 0 until childNode.childCount) {
                val secondChildNode = childNode.getChild(j)
                L.i("第三级 childNode: ${secondChildNode.className} text: ${secondChildNode.text}")

                for (k in 0 until secondChildNode.childCount) {
                    val thirdChildNode = secondChildNode.getChild(k)
                    L.i("第四级 childNode: ${thirdChildNode.className} text: ${thirdChildNode.text}")

                    for (l in 0 until thirdChildNode.childCount)
                    {
                        L.i("")
                    }
                }

            }*/

        }
    }

    private fun createAddress() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedText: String) {

                }

                override fun onTaskFinished() {

                }
            })
            .setNodeParams("手动添加收货地址")
            .setNodeParams(
                "com.xunmeng.pinduoduo:id/gw", 2,
                isClicked = false,
                isScrolled = false,
                editorInputText = "秦先生"
            )
            .setNodeParams(
                "com.xunmeng.pinduoduo:id/gx", 2,
                isClicked = false,
                isScrolled = false,
                editorInputText = "13513613721"
            )
            .setNodeParams(
                "com.xunmeng.pinduoduo:id/h3", 2,
                isClicked = false,
                isScrolled = false,
                editorInputText = "长安街道088号"
            )
            .setNodeParams("选择地区")
            .setNodeParams("四川省", 0, isClicked = true, isScrolled = true)
            .setNodeParams("遂宁市", 0, isClicked = true, isScrolled = true)
            .setNodeParams("安居区", 0, isClicked = true, isScrolled = true)
            .setNodeParams("保存")
            .setNodeParams("更多支付方式")
            .setNodeParams("QQ钱包")
            .setNodeParams("立即支付")
            .create()
            .execute()

    }
}