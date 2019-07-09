package com.accessibility.service.base


/**
 * Description:
 * Created by Quinin on 2019-07-05.
 **/
abstract class BaseEventService(val nodeService: BaseAccessibilityService) {
    abstract fun doOnEvent()
}