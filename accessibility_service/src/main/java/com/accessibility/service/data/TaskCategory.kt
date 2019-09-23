package com.accessibility.service.data

/**
 * Description:任务类型
 * 0：正常下单任务
 * 1：评论任务
 * 2：确认收货任务
 * Created by Quinin on 2019-09-23.
 **/
class TaskCategory {
    companion object {
        const val NORMAL_TASK = 0
        const val COMMENT_TASK = 1
        const val CONFIRM_SIGNED_TASK = 2
    }
}