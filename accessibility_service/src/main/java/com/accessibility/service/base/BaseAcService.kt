package com.accessibility.service.base

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.listener.TaskListener

/**
 * Description:
 * Created by Quinin on 2019-08-07.
 **/
abstract class BaseAcService(myAccessibilityService: MyAccessibilityService){
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