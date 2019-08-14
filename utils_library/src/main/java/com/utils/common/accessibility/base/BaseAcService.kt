package com.utils.common.accessibility.base

import com.utils.common.accessibility.listener.TaskListener

/**
 * Description:
 * Created by Quinin on 2019-08-07.
 **/
abstract class BaseAcService(myAccessibilityService: BaseAccessibilityService) {
    private var mTaskListener: TaskListener? = null

    fun setTaskListener(taskListener: TaskListener): BaseAcService {
        mTaskListener = taskListener
        return this
    }

    abstract fun startService()

    fun responFailed(errorMsg: String) {
        mTaskListener?.onTaskFailed(errorMsg)
    }

    fun responSucceed() {
        mTaskListener?.onTaskFinished()
    }

}