package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.login.WXloginService
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.GetNodeUtils
import com.accessibility.service.util.SingletonHolder
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-11.
 **/
class LoginService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<LoginService, MyAccessibilityService>(::LoginService)

    private var mIsLogining = false

    override fun doOnEvent() {
        if (mIsLogining) {
            L.i("正在登录...")
            return
        }

        if (nodeService.mIsLogined)
            return
        mIsLogining = true
        loginByNotWechat()
    }

    private fun loginByNotWechat() {
        GetNodeUtils.getNodeByFullText(nodeService, "请使用其它方式登录", object : NodeFoundListener {
            override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                nodeInfo?.let {
                    L.i("当前界面：选择登录")
                    nodeService.apply {
                        if (TaskDataUtil.instance.getLogin_channel() != 1)    //不是微信登录
                            performViewClick(it, object : AfterClickedListener {
                                override fun onClicked() {
                                    findViewByFullText("QQ登录")?.apply {
                                        performViewClick(this, object : AfterClickedListener {
                                            override fun onClicked() {
                                                mIsLogining = false
                                                setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
                                                mTaskFinishedListener?.onTaskFinished()
                                            }
                                        })
                                    }
                                }
                            })
                        else {
                            WXloginService(nodeService).doOnEvent()
                        }
                    }
                }
            }
        })
    }


}