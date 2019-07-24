package com.accessibility.service.base

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.listener.TaskListener


/**
 * Description:
 * Created by Quinin on 2019-07-05.
 **/
abstract class BaseEventService(val nodeService: MyAccessibilityService) {
    var mTaskFinishedListener: TaskListener? = null
    abstract fun doOnEvent()

    fun setTaskFinishedListener(taskFinishedListener: TaskListener): BaseEventService {
        this.mTaskFinishedListener = taskFinishedListener
        return this
    }
}