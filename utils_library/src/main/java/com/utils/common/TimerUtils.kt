package com.utils.common

import java.util.*

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
class TimerUtils {
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var mIsStarted: Boolean = false

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TimerUtils()
        }
    }

    /**
     * @param intervalTime 间隔时间，单位毫秒
     */
    fun start(timerTask: TimerTask, intervalTime: Long) {
        if (mIsStarted)
            return
        if (mTimer == null) {
            mTimer = Timer("定时获取任务")
        }

        mTimerTask = timerTask
        mTimer?.run {
            mTimerTask?.let {
                mIsStarted = true
                schedule(it, intervalTime, intervalTime)
            }
        }
    }


    fun stop() {
        mIsStarted = false
        mTimerTask?.run {
            cancel()
            mTimerTask = null
        }
        mTimer?.run {
            cancel()
            mTimer = null
        }
    }

}