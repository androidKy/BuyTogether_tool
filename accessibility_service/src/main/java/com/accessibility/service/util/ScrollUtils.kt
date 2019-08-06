package com.accessibility.service.util

import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.NodeFoundListener
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-10.
 **/
class ScrollUtils constructor(private val nodeService: BaseAccessibilityService, private val recyclerViewNode: AccessibilityNodeInfo) {

    /* companion object {
         var instance: ScrollUtils? = null

         fun getInstance(nodeService: MyAccessibilityService, recyclerViewNode: AccessibilityNodeInfo) {
             if (instance == null) {
                 synchronized(this) {
                     if (instance == null)
                         instance = ScrollUtils(nodeService, recyclerViewNode)
                 }
             }
         }
     }*/
    companion object {
        private const val MSG_FORWARD_WHAT: Int = 10000
        private const val MSG_BACKWARD_WHAT: Int = 20000
    }

    private var mIsNodeFound: Boolean = false

    private var mForwardTime: Int = 10
    private var mBackwardTime: Int = 10

    private var mScrollListener: ScrollListener? = null
    private var mNodeFoundListener: NodeFoundListener? = null
    private var mNodeText: String? = null
    private var mNodeId: String? = null

    init {
        mIsNodeFound = false
        mForwardTime = (10..15).random()
        mBackwardTime = (10..15).random()
    }

    private val mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_FORWARD_WHAT -> {
                scrollForward()
            }

            MSG_BACKWARD_WHAT -> {
                scrollBackward()
            }
        }
        false
    }


    /**
     * 设置向上滑动的总时间
     * 单位为秒
     */
    fun setForwardTotalTime(forwardTime: Int): ScrollUtils {
        this.mForwardTime = forwardTime
        return this
    }

    /**
     * 设置向下拉的总时间·
     * 单位为秒
     */
    fun setBackwardTime(backwardTime: Int): ScrollUtils {
        this.mBackwardTime = backwardTime
        return this
    }

    /**
     * 滑动监听
     */
    fun setScrollListener(scrollListener: ScrollListener): ScrollUtils {
        this.mScrollListener = scrollListener
        return this
    }

    fun setNodeText(nodeText: String): ScrollUtils {
        this.mNodeText = nodeText
        return this
    }

    fun setNodeId(nodeId: String): ScrollUtils {
        this.mNodeId = nodeId
        return this
    }

    /**
     * 设置节点查找监听
     */
    fun setNodeFoundListener(nodeFoundListener: NodeFoundListener): ScrollUtils {
        this.mNodeFoundListener = nodeFoundListener
        return this
    }


    fun scrollForward() {
        if (mForwardTime > 0) {
            L.i("向上滑动：$mForwardTime mIsNodeFound:$mIsNodeFound")
            nodeService.performScrollForward(recyclerViewNode)
            mForwardTime--

            mHandler.sendEmptyMessageDelayed(MSG_FORWARD_WHAT, 1000)
            findNode()
        } else {
            if (!mIsNodeFound) {
                mNodeFoundListener?.onNodeFound(null)
            }
            mScrollListener?.onScrollFinished(recyclerViewNode)
            mHandler.removeMessages(MSG_FORWARD_WHAT)
        }
    }

    fun scrollBackward() {
        if (mBackwardTime > 0) {
            L.i("向下拉：$mBackwardTime")
            nodeService.performScrollBackward(recyclerViewNode)
            mBackwardTime--

            mHandler.sendEmptyMessageDelayed(MSG_BACKWARD_WHAT, 1000)
            findNode()
        } else {
            if (!mIsNodeFound) {
                mNodeFoundListener?.onNodeFound(null)
            }
            mScrollListener?.onScrollFinished(recyclerViewNode)
            mHandler.removeMessages(MSG_BACKWARD_WHAT)
        }
    }

    private fun findNode() {
        if (mIsNodeFound) {
            removeMsg()
            return
        }
        mNodeText?.apply {
            nodeService.findViewByFullText(this)?.apply {
                mIsNodeFound = true
                removeMsg()
                mNodeFoundListener?.onNodeFound(this)
            }
        }

        mNodeId?.apply {
            nodeService.findViewById(this)?.apply {
                mIsNodeFound = true
                removeMsg()
                mNodeFoundListener?.onNodeFound(this)
            }
        }
    }

    private fun removeMsg() {
        mHandler.removeMessages(MSG_BACKWARD_WHAT)
        mHandler.removeMessages(MSG_FORWARD_WHAT)
    }

    interface ScrollListener {
        fun onScrollFinished(nodeInfo: AccessibilityNodeInfo)
    }
}