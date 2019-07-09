package com.accessibility.service.util

import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.NodeFoundListener

/**
 * Description:
 * Created by Quinin on 2019-07-09.
 **/
class NodeUtils private constructor() {
    private val TIME_OUT_SECOND = 20
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
            return
        }

        nodeService.postDelay(Runnable {
            mTimeOut++
            getNodeById(nodeService, id)
        }, 1)
    }

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
                        return
                    }

                    nodeService.postDelay(Runnable {
                        mTimeOut++
                        getSingleNodeByClassName(nodeService, className)
                    }, 1)
                }
            })
    }


}