package com.accessibility.service.util

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.safframework.log.L

/**
 * Description:通过ADB命令控制滑动
 * Created by Quinin on 2019-07-27.
 **/
class AdbScrollUtils private constructor() {

    companion object {
        const val MSG_ADB_SCROLL: Int = 888
        const val SCROLL_TOTAL_DEFAULT_TIME: Long = 18 * 1000 //默认的滑动总时间
        const val SCROLL_SPEED_DEFAULT_TIME: Long = 1000     //默认的滑动间隔时间,1秒
        const val DEFAULT_START_XY: String = "540,1740"      //默认滑动的起点
        const val DEFAULT_STOP_XY: String = "540,1040"        //默认滑动的终点

        val instantce: AdbScrollUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AdbScrollUtils()
        }
    }

    private var mScrollTotalTime: Long = 0L   //滑动的时间
    private var mFindText: String = ""      //查找节点text
    private var mStartXY: String = ""    //设置滑动的起点
    private var mStopXY: String = ""     //设置滑动的终点
    private var mScrollSpeed: Long = 0L      //设置滑动的速度,多长时间滑动一次
    private var mNodeService: BaseAccessibilityService? = null     //节点的服务，用于查找text的节点

    private var mNodeFoundListener: NodeFoundListener? = null

    private val mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_ADB_SCROLL -> {
                AdbScriptController.Builder()
                    .setSwipeXY(mStartXY, mStopXY)
                    .setTaskListener(object : TaskListener {
                        override fun onTaskFinished() {
                            startScroll()
                        }

                        override fun onTaskFailed(failedMsg: String) {
                            responFailed(failedMsg)
                        }
                    })
                    .create()
                    .execute()
            }
        }

        false
    }

    init {
        initData()
    }

    fun setInitData(): AdbScrollUtils {
        initData()
        return this
    }

    fun setScrollTotalTime(scrollTotalTime: Long): AdbScrollUtils {
        mScrollTotalTime = scrollTotalTime
        return this
    }

    fun setScrollSpeed(scrollSpeedTime: Long): AdbScrollUtils {
        mScrollSpeed = scrollSpeedTime
        return this
    }

    fun setNodeService(nodeService: BaseAccessibilityService): AdbScrollUtils {
        mNodeService = nodeService
        return this
    }

    fun setFindText(text: String): AdbScrollUtils {
        mFindText = text

        return this
    }

    fun setStartXY(startXY: String): AdbScrollUtils {
        mStartXY = startXY
        return this
    }

    fun setStopXY(stopXY: String): AdbScrollUtils {
        mStopXY = stopXY
        return this
    }

    fun setNodeFoundListener(nodeFoundListener: NodeFoundListener): AdbScrollUtils {
        mNodeFoundListener = nodeFoundListener
        return this
    }


    /**
     * 开始滑动
     */
    fun startScroll() {
        if (mFindText.isNotEmpty() && mNodeService == null) {
            responFailed("没有设置节点服务")
            return
        }
        if (mScrollTotalTime <= 0)   //滑动时间结束
        {
            responFailed("滑动时间结束,$mFindText was not found.")
            return
        }
        mScrollTotalTime -= mScrollSpeed
        L.i("mScrollTotalTime: $mScrollTotalTime")

        //不设置节点text的时候，表示不查找text
        if (TextUtils.isEmpty(mFindText)) {
            mHandler.sendEmptyMessageDelayed(MSG_ADB_SCROLL, mScrollSpeed)
            return
        }

        val resultInfo = findNode()
        if (resultInfo == null) {
            mHandler.sendEmptyMessageDelayed(MSG_ADB_SCROLL, mScrollSpeed)
        } else {
            responSucceed(resultInfo)
        }
    }

    private fun findNode(): AccessibilityNodeInfo? {

        /* if (nodeResult == null) {
            nodeResult = mNodeService?.findViewById(mFindText)
        }*/
        return mNodeService?.findViewByFullText(mFindText)
    }

    fun startSearchGood() {
        if (mFindText.isNotEmpty() && mNodeService == null) {
            responFailed("没有设置节点服务")
            return
        }
        if (mScrollTotalTime <= 0)   //滑动时间结束
        {
            responFailed("滑动时间结束,$mFindText was not found.")
            return
        }
        mScrollTotalTime -= mScrollSpeed
        L.i("mScrollTotalTime: $mScrollTotalTime")

        //不设置节点text的时候，表示不查找text
        if (TextUtils.isEmpty(mFindText)) {
            mHandler.sendEmptyMessageDelayed(MSG_ADB_SCROLL, mScrollSpeed)
            return
        }

        val resultInfo = findHalfNode()
        if (resultInfo == null) {
            mHandler.sendEmptyMessageDelayed(MSG_ADB_SCROLL, mScrollSpeed)
        } else {
            responSucceed(resultInfo)
        }
    }

    private fun findHalfNode(): AccessibilityNodeInfo? {

        /* if (nodeResult == null) {
            nodeResult = mNodeService?.findViewById(mFindText)
        }*/
        return mNodeService?.findViewByText(mFindText)
    }


    private fun initData() {
        mHandler.removeMessages(MSG_ADB_SCROLL)
        mScrollTotalTime = SCROLL_TOTAL_DEFAULT_TIME
        mStartXY = DEFAULT_START_XY
        mStopXY = DEFAULT_STOP_XY
        mScrollSpeed = SCROLL_SPEED_DEFAULT_TIME
    }


    private fun responFailed(msg: String) {
        L.i("滑动找节点失败：$msg")
        initData()
        mNodeFoundListener?.onNodeFound(null)
    }

    private fun responSucceed(nodeInfo: AccessibilityNodeInfo?) {
        initData()
        mNodeFoundListener?.onNodeFound(nodeInfo)
    }

}