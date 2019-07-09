package com.accessibility.service.util

import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.data.TaskServiceData

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
class TaskDataUtil private constructor() {

    private var mTaskServiceData: TaskServiceData? = null

    companion object {
        val instance: TaskDataUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TaskDataUtil()
        }
    }

    fun initData(taskServiceData: TaskServiceData) {
        mTaskServiceData = taskServiceData
    }


    /**
     * 获取登录渠道方式,默认是0
     */
    fun getLogin_channel(): Int {
        mTaskServiceData?.apply {
            return task?.login_channel ?: 0
        }

        return 0
    }

    /**
     * 获取账号名字
     */
    fun getLogin_name(): String? {
        mTaskServiceData?.apply {
            return task?.account?.qq?.name
        }

        return ""
    }

    /**
     * 获取账号密码
     */
    fun getLogin_psw(): String? {
        mTaskServiceData?.apply {
            return task?.account?.qq?.psw
        }

        return ""
    }

    /**
     * 获取商品名字
     */
    fun getGoods_name(): String? {
        mTaskServiceData?.apply {
            return task?.goods?.get(0)?.name
        }

        return ""
    }

    /**
     * 获取任务类型
     * {
     *  1：刷浏览数
     *  2：与卖家沟通
     *  3：刷收藏数
     *  4：刷订单数
     *  12：刷浏览和沟通
     * }
     */
    fun getTask_type(): Int? {
        mTaskServiceData?.apply {
            return task?.task_type
        }

        return 1
    }

}