package com.accessibility.service.login

import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.data.TaskDataUtil
import com.accessibility.service.data.WidgetConstant
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
class QQloginService(accessibilityService: MyAccessibilityService, event: AccessibilityEvent) :
    BaseEventService(accessibilityService, event) {
    override fun doOnEvent() {
        /* val loginBtn = accessibilityService.findViewById("com.tencent.mobileqq:id/btn_login")*/
        L.i("doOnEvent------")
        val loginBtn = accessibilityService.findViewByText("登录")

        loginBtn.let {
            L.i("login text: ${it?.text}")
            accessibilityService.performViewClick(it, 1)
            L.i("当前界面：登录")
        }

    }

    fun login() {
        L.i("当前界面：输入账号和密码")
        accessibilityService.findViewByClassName(
            accessibilityService.rootInActiveWindow,
            WidgetConstant.EDITTEXT,
            object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo) {
                    L.i("account edittext was found.")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val arguments = Bundle()
                        arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            TaskDataUtil.instance.getLogin_name()
                        )
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    }
                }
            }
        )
        val pswNodeInfo = accessibilityService.findViewById("com.tencent.mobileqq:id/password")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pswNodeInfo?.let {
                val pswBundle = Bundle()
                pswBundle.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    TaskDataUtil.instance.getLogin_psw()
                )

                it.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, pswBundle)
            }
        }


        accessibilityService.findViewByClassName(accessibilityService.rootInActiveWindow, WidgetConstant.IMAGEVIEW,
            object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo) {
                    L.i("login btn was found.")
                   // accessibilityService.performViewClick(nodeInfo, 2)
                }

            })
    }
}