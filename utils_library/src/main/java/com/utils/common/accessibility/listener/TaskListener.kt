package com.utils.common.accessibility.listener

/**
 * Description:
 * Created by Quinin on 2019-07-12.
 **/
interface TaskListener {
    fun onTaskFinished()
    fun onTaskFailed(failedMsg: String)
}