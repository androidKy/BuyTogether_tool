package com.accessibility.service.util

import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.base.BaseAccessibilityService
import kotlin.random.Random

/**
 * Description:
 * Created by Quinin on 2019-07-10.
 **/
class ScrollUtils constructor(val nodeService: BaseAccessibilityService, val nodeInfo: AccessibilityNodeInfo) {

    /* companion object {
         var instance: ScrollUtils? = null

         fun getInstance(nodeService: MyAccessibilityService, nodeInfo: AccessibilityNodeInfo) {
             if (instance == null) {
                 synchronized(this) {
                     if (instance == null)
                         instance = ScrollUtils(nodeService, nodeInfo)
                 }
             }
         }
     }*/

    private var mForwardTime: Int = 0
    private var mBackwardTime: Int = 0
    private val MSG_FORWARD_WHAT: Int = 100
    private val MSG_BACKWARD_WHAT: Int = 200
    private var mScrollListener: ScrollListener? = null


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

    fun setScrollListener(scrollListener: ScrollListener): ScrollUtils {
        this.mScrollListener = scrollListener
        return this
    }


    fun scrollForward() {
        if (mForwardTime > 0) {
            nodeService.performScrollForward(nodeInfo)
            mForwardTime--
            mHandler.sendEmptyMessageDelayed(MSG_FORWARD_WHAT, 1000)
        } else {
            mScrollListener?.onScrollFinished(nodeInfo)
            mHandler.removeMessages(MSG_FORWARD_WHAT)
        }
    }

    fun scrollBackward() {
        if (mBackwardTime > 0) {
            nodeService.performScrollBackward(nodeInfo)
            mBackwardTime--
            mHandler.sendEmptyMessageDelayed(MSG_BACKWARD_WHAT, 1000)
        } else {
            mScrollListener?.onScrollFinished(nodeInfo)
            mHandler.removeMessages(MSG_BACKWARD_WHAT)
        }
    }

    interface ScrollListener {
        fun onScrollFinished(nodeInfo: AccessibilityNodeInfo)
    }
}