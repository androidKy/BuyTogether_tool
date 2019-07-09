package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.util.SingletonHolder

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
class TalkService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<TalkService, MyAccessibilityService>(::TalkService)

    override fun doOnEvent() {

    }
}