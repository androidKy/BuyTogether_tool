package com.accessibility.service.function

import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.data.TaskBean
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.ThreadUtils

class LoginFailed(val nodeService: MyAccessibilityService):BaseAcService(nodeService) {




    override fun startService() {
        // 1、掉线初始化任务数据
        initTaskData()
        // 2、
        dealDropLine()
    }

    private fun dealDropLine() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("请使用其它方式登录",0,5)
            .setNodeParams("QQ登录",0)
            .setTaskListener(object :TaskListener{
                override fun onTaskFinished() {
                    L.i("dealDropLine()...处理掉线情况")
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("是否被封号。")
                    responFailed("")
                }

            })
            .create()
            .execute()
    }

    /**
     * 获取任务数据
     */
    fun initTaskData() {
            SPUtils.getInstance(nodeService, Constant.SP_TASK_FILE_NAME).getString(Constant.KEY_TASK_DATA)
                .let {
                    if (!TextUtils.isEmpty(it)) {
                        L.i("断线之后，TaskBean为空，初始化")
                        val taskServiceData = Gson().fromJson(it, TaskBean::class.java)
                        TaskDataUtil.instance.initData(taskServiceData)
                    }
                }
    }






}