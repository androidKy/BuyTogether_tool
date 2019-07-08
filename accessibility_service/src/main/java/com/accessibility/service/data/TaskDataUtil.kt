package com.accessibility.service.data

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
class TaskDataUtil {

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

}