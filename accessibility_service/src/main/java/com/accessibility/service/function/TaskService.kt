package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.util.SingletonHolder
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:做任务
 * Created by Quinin on 2019-07-09.
 **/
class TaskService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<TaskService, MyAccessibilityService>(::TaskService)

    override fun doOnEvent() {
        val taskType = TaskDataUtil.instance.getTask_type()
        L.i("taskType = $taskType")

        when (taskType) {
            1 -> scaningGoods()
            2 -> talkWithSaler()
            3 -> collectGoods()
            4 -> buyGoods()
        }
    }

    /**
     * 正在浏览商品
     */
    fun scaningGoods() {

    }

    /**
     * 与卖家沟通
     */
    fun talkWithSaler() {

    }

    /**
     * 收藏商品
     */
    fun collectGoods() {

    }

    /**
     * 购买商品
     */
    fun buyGoods() {

    }
}