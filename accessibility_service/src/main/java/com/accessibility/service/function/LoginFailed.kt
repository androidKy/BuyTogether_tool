package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.LoginFailedType
import com.safframework.log.L

class LoginFailed(val nodeService: MyAccessibilityService) : BaseAcService(nodeService) {


    var loginFailedType: Int? = 0
    var mTypeListener: TypeListener? = null

    interface TypeListener {
        fun onResponType(failedType: Int)
    }

    fun setTypeListener(typeListener: TypeListener): LoginFailed {
        mTypeListener = typeListener

        return this
    }


    override fun startService() {
        // 1、掉线初始化任务数据
        initTaskData()
        // 2、
        dealDropLine()
    }

//    public fun getLoginFailedType(): Int {
//        if (loginFailedType != null) {
//            return loginFailedType!!
//        } else {
//            return 0
//        }
//    }

    private fun dealDropLine() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("请使用其它方式登录", 0, 5)
            .setNodeParams("QQ登录", 0, 2)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("dealDropLine()...处理掉线情况")
                    loginFailedType = LoginFailedType.DROP_LINE
                    // responSucceed()
                    mTypeListener?.onResponType(loginFailedType!!)
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("是否被封号。")
//                    responFailed("")
                    isUnvalid()
                }

            })
            .create()
            .execute()
    }

    private fun isUnvalid() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到 ‘确定’ 节点，确认账号被封。")
                    loginFailedType = LoginFailedType.UNVAILD
                    mTypeListener?.onResponType(loginFailedType!!)

                }

                override fun onTaskFailed(failedMsg: String) {
                    isFindAddAccount()
                }

            })
            .create()
            .execute()
    }

    /**
     *  是否找到 “添加帐号”
     */
    private fun isFindAddAccount() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("添加账号",0,false,10)
            .setTaskListener(object :TaskListener{
                override fun onTaskFinished() {
                    loginFailedType = LoginFailedType.ADD_ACCOUNT
                    mTypeListener?.onResponType(loginFailedType!!)
                }

                override fun onTaskFailed(failedMsg: String) {
                    // 有时会跳出某些 广告界面。
//                    dealAdPage()
                }

            })
            .create()
            .execute()
    }

    /**
     * 获取任务数据
     */
    private fun initTaskData() {
        L.i("断线之后，TaskBean为空，初始化")
        nodeService?.initTaskData()
        /*if (nodeService != null) {
          .initTaskData()
        } else {
            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getString(Constant.KEY_TASK_DATA)
                .let {
                    if (!TextUtils.isEmpty(it)) {
                        L.i("断线之后，TaskBean为空，初始化")
                        val taskServiceData = Gson().fromJson(it, TaskBean::class.java)
                        TaskDataUtil.instance.initData(taskServiceData)
                    }
                }
        }*/
    }


}