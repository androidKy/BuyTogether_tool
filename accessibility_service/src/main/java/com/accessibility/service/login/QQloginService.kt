package com.accessibility.service.login

import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.NodeUtils
import com.accessibility.service.util.SingletonHolder
import com.safframework.log.L

/**
 * Description:QQ登录和授权
 * Created by Quinin on 2019-07-02.
 **/
class QQloginService private constructor(nodeService: MyAccessibilityService) :
    BaseEventService(nodeService) {


    companion object : SingletonHolder<QQloginService, MyAccessibilityService>(::QQloginService)

    /**
     * 点击登录跳转到登录界面
     */
    override fun doOnEvent() {
        /* val loginBtn = nodeService.findViewById("com.tencent.mobileqq:id/btn_login")*/
        L.i("doOnEvent------")

        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.let {
                        nodeService.apply {
                            L.i("login text: ${it.text}")
                            setCurPageType(PageEnum.QQ_LOGIN_PAGE)
                            performViewClick(it, 1)
                            L.i("当前界面：登录")
                        }
                    }
                }
            })
            .getNodeByFullText(nodeService, "登录")
    }

    /**
     * 输入账号和密码，点击登录
     */
    fun login() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    L.i("当前界面：输入账号和密码")
                    //密码节点
                    nodeInfo?.apply {
                        WidgetConstant.setEditText(TaskDataUtil.instance.getLogin_psw(), this)
                    }

                    //账号节点
                    nodeService.findViewByClassName(
                        nodeService.rootInActiveWindow,
                        WidgetConstant.EDITTEXT,
                        object : NodeFoundListener {
                            override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                                L.i("account edittext was found.")
                                nodeInfo?.apply {
                                    WidgetConstant.setEditText(TaskDataUtil.instance.getLogin_name(), this)
                                }
                            }
                        }
                    )

                    nodeService.apply {
                        //登录按钮
                        val loginNodeInfo = nodeService.findViewById("com.tencent.mobileqq:id/login")
                        setCurPageType(PageEnum.QQ_LOGINING_PAGE)
                        performViewClick(loginNodeInfo, 2)
                    }
                }
            })
            .getNodeById(nodeService, "com.tencent.mobileqq:id/password")
    }

    /**
     * 授权登录界面
     */
    fun authLogin() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.let {
                        nodeService.apply {
                            setCurPageType(PageEnum.AUTH_LOGIN_PAGE)
                            setIsLogined(true)
                            performViewClick(it, 2)
                        }
                    }
                }
            })
            .getNodeByFullText(nodeService, "登录")

    }
}