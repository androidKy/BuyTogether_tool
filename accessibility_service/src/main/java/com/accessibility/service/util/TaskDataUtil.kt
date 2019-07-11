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
    fun getLogin_channel(): Int? {
        return mTaskServiceData?.run {
            task?.login_channel ?: 0
        }
    }

    /**
     * 获取账号名字
     */
    fun getLogin_name(): String? {
        return mTaskServiceData?.run {
            task?.account?.qq?.name
        }
    }

    /**
     * 获取账号密码
     */
    fun getLogin_psw(): String? {
        return mTaskServiceData?.run {
            task?.account?.qq?.psw
        }
    }

    /**
     * 获取商品名字
     */
    fun getGoods_name(): String? {
        return mTaskServiceData?.run {
            task?.goods?.get(0)?.name
        }
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
        return mTaskServiceData?.run {
            task?.task_type ?: 1
        }
    }

    /**
     * 获取聊天信息
     */
    fun getTalk_msg(): String? {
        return mTaskServiceData?.run {
            task?.goods?.get(0)?.talk_msg ?: "老板你好"
        }
    }

    /**
     * 获取选择的商品信息
     */
    fun getChoose_info(): List<String>? {
        return mTaskServiceData?.run {
            task?.goods?.get(0)?.choose_info ?: ""
        }.run {
            this?.split(",")
        }
    }
}