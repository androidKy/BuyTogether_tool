package com.accessibility.service.login

import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.base.BaseEventService

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
class WXloginService(accessibilityService: BaseAccessibilityService, event: AccessibilityEvent) :
    BaseEventService(accessibilityService, event)  {

    override fun doOnEvent() {

    }
}