package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.util.SingletonHolder

/**
 * Description: 浏览商品
 * Created by Quinin on 2019-07-02.
 **/
class ScanGoodsService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<ScanGoodsService, MyAccessibilityService>(::ScanGoodsService)

    /**
     * 开始浏览商品
     */
    override fun doOnEvent() {

    }

}