package com.accessibility.service.util

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.NodeFoundListener

/**
 * Description:
 * Created by Quinin on 2019-07-09.
 **/
class NodeUtils private constructor() {
    private var TIME_OUT_SECOND = 20
    private var mTimeOut = 0
    private var mNodeFoundListener: NodeFoundListener? = null

    companion object {
        val instance: NodeUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NodeUtils()
        }
    }

    fun setNodeFoundListener(nodeFoundListener: NodeFoundListener): NodeUtils {
        mNodeFoundListener = nodeFoundListener

        return this
    }

    fun setTimeOut(timeOut: Int): NodeUtils {
        this.TIME_OUT_SECOND = timeOut
        return this
    }

    /**
     * 查找包含text的节点集合的第一个
     */
    fun getNodeByText(nodeService: BaseAccessibilityService, text: String) {
        val nodeInfo = nodeService.findViewByText(text)

        if (nodeInfo != null) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(nodeInfo)
            return
        }
        if (mTimeOut >= TIME_OUT_SECOND) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(null)
            return
        }

        nodeService.postDelay(Runnable {
            mTimeOut++
            getNodeByText(nodeService, text)
        }, 1)
    }

    fun getNodesByText(nodeService: BaseAccessibilityService, text: String): List<AccessibilityNodeInfo> {
        return nodeService.findViewsByText(text)
    }

    /**
     * 查找与text完全匹配的节点
     */
    fun getNodeByFullText(nodeService: BaseAccessibilityService, text: String) {
        val nodeInfo = nodeService.findViewByFullText(text)
        if (nodeInfo != null) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(nodeInfo)
            return
        }

        if (mTimeOut >= TIME_OUT_SECOND) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(null)
            //nodeService.removeMsg()
            return
        }

        nodeService.postDelay(Runnable {
            mTimeOut++
            getNodeByFullText(nodeService, text)
        }, 1)
    }

    /**
     * 根据ID查找节点
     */
    fun getNodeById(nodeService: BaseAccessibilityService, id: String) {
        val nodeInfo = nodeService.findViewById(id)

        if (nodeInfo != null) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(nodeInfo)
            return
        }

        if (mTimeOut >= TIME_OUT_SECOND) {
            mTimeOut = 0
            mNodeFoundListener?.onNodeFound(null)
            //nodeService.removeMsg()
            return
        }

        nodeService.postDelay(Runnable {
            mTimeOut++
            getNodeById(nodeService, id)
        }, 1)
    }

    /**
     * 根据className获取第一个符合条件的节点
     */
    fun getSingleNodeByClassName(nodeService: BaseAccessibilityService, className: String) {
        nodeService.findViewByClassName(nodeService.rootInActiveWindow, className,
            object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo != null) {
                        mTimeOut = 0
                        mNodeFoundListener?.onNodeFound(nodeInfo)
                        return
                    }

                    if (mTimeOut >= TIME_OUT_SECOND) {
                        mTimeOut = 0
                        mNodeFoundListener?.onNodeFound(null)
                        //nodeService.removeMsg()
                        return
                    }

                    nodeService.postDelay(Runnable {
                        mTimeOut++
                        getSingleNodeByClassName(nodeService, className)
                    }, 1)
                }
            })
    }


    private fun removeMsg(nodeService: BaseAccessibilityService)
    {
        nodeService.removeMsg()
    }
}