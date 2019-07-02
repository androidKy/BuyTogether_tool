package com.login.test.accessibility

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.login.test.util.Constants
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
class QQLoginAccessService : BaseAccessibilityService() {

    private var mCurPageEnum: PageEnum = PageEnum.PAGE_QQ_LOGIN

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        val info = serviceInfo
        info.packageNames = arrayOf(
            //"android",
            //"com.google.android.packageinstaller",
            Constants.QQ_TIM_PKG
        )
        serviceInfo = info

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
            L.i("QQ eventType = $eventType")
            val node = findViewByText("登录")
            if(node != null)
            {
                L.i("result node className: ${node.className}")
            }

            val classNameNode = findViewByClassName(rootInActiveWindow,"android.widget.EditText")
            if(classNameNode != null)
            {
                L.i("result classNameNode text: ${classNameNode.text}")
            }
        }
    }
}