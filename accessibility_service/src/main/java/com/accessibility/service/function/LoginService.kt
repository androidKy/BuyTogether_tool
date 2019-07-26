package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil

/**
 * Description:
 * Created by Quinin on 2019-07-25.
 **/
class LoginService constructor(val myAccessibilityService: MyAccessibilityService) {

    fun login(taskListener: TaskListener) {
        when (TaskDataUtil.instance.getLogin_channel()) {
            0 -> QQLogin(myAccessibilityService).login(taskListener)

            else -> QQLogin(myAccessibilityService).login(taskListener)
        }
    }
}