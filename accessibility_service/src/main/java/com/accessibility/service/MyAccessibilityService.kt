package com.accessibility.service

import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.base.BaseAccessibilityService

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService: BaseAccessibilityService(){
    override fun onInterrupt() {
    }

    override fun onServiceConnected() {


        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }
}