package com.accessibility.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.page.PageEnum

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {

    private var mCurPageType = PageEnum.INDEX_PAGE
    private var mIsLogined = false

    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
    }

    override fun onInterrupt() {

    }


    override fun onServiceConnected() {

        serviceInfo?.let {
            it.packageNames = arrayOf(PKG_PINDUODUO, PKG_QQ)
            serviceInfo = it
        }

        /* val accessibilityServiceInfo = serviceInfo
         accessibilityServiceInfo.packageNames = arrayOf(PKG_PINDUODUO, PKG_QQ)

         serviceInfo = accessibilityServiceInfo*/

        super.onServiceConnected()
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {

        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        val eventType = event?.eventType
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED
        ) {

        }
    }
}