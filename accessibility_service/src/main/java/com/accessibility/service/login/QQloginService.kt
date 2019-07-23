package com.accessibility.service.login

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.NodeUtils
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:QQ登录和授权
 * Created by Quinin on 2019-07-02.
 **/
class QQloginService private constructor(nodeService: MyAccessibilityService) :
    BaseEventService(nodeService) {

    private var mIsDoing = false //是否正在进行任务

    companion object : com.utils.common.SingletonHolder<QQloginService, MyAccessibilityService>(::QQloginService)

    /**
     * 点击登录跳转到登录界面
     */
    override fun doOnEvent() {
        /* val loginBtn = nodeService.findViewById("com.tencent.mobileqq:id/btn_login")*/
        if (mIsDoing) {
            L.i("QQ登录任务已经开始 ... ")
            return
        }
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.let {
                        mIsDoing = true

                        nodeService.apply {
                            L.i("login textList: ${it.text}")
                            if (findViewByFullText("使用QQ登录") != null) { //如果QQ已登录
                                authLogin()
                            } else
                                performViewClick(it, 1, AfterLoginClickedImpl())
                        }
                    }
                }
            })
            .getNodeByFullText(nodeService, "登录")
    }

    inner class AfterLoginClickedImpl : AfterClickedListener {
        override fun onClicked() {
            login()
        }
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

                        nodeService.apply {
                            //账号节点
                            findViewByClassName(
                                nodeService.rootInActiveWindow,
                                WidgetConstant.EDITTEXT,
                                object : NodeFoundListener {
                                    override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                                        nodeInfo?.apply {
                                            L.i("account edittext was found.")
                                            WidgetConstant.setEditText(TaskDataUtil.instance.getLogin_name(), this)
                                        }
                                    }
                                }
                            )

                            //登录按钮
                            val loginNodeInfo = nodeService.findViewById("com.tencent.mobileqq:id/login")
                            performViewClick(loginNodeInfo, 2, AfterQQfilledImpl())
                        }
                    }

                }
            })
            .getNodeById(nodeService, "com.tencent.mobileqq:id/password")
    }

    /**
     * QQ账号和密码输入后点击登录
     */
    inner class AfterQQfilledImpl : AfterClickedListener {
        override fun onClicked() {
            //1、有验证码 2、无验证码
            authLogin()
        }
    }

    /**
     * 授权登录界面
     */
    fun authLogin() {
        NodeUtils.instance
            .setTimeOut(5)
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeService.apply {
                        if (nodeInfo == null) {
                            setCurPageType(PageEnum.INDEX_PAGE)
                            setIsLogined(true)
                            mIsDoing = false

                            mTaskFinishedListener?.onTaskFinished(false)
                        } else {
                            performViewClick(nodeInfo, 1, object : AfterClickedListener {
                                override fun onClicked() {
                                    setCurPageType(PageEnum.INDEX_PAGE)
                                    setIsLogined(true)
                                    mIsDoing = false

                                    mTaskFinishedListener?.onTaskFinished(true)
                                }
                            })
                        }
                    }
                }
            })
            .getNodeByFullText(nodeService, "登录")

    }
}