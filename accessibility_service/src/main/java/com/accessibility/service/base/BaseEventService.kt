package com.accessibility.service.base

import android.view.accessibility.AccessibilityEvent

/**
 * Description:
 * Created by Quinin on 2019-07-05.
 **/
abstract class BaseEventService(val accessibilityService: BaseAccessibilityService,val event: AccessibilityEvent) {
    abstract fun doOnEvent()
}