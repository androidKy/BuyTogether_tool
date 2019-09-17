package com.accessibility.service.function

import android.content.Intent
import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.data.AccountBean
import com.accessibility.service.data.TaskBean
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.CommentStatus
import com.accessibility.service.page.LoginFailedType
import com.accessibility.service.util.Constant
import com.accessibility.service.util.DeviceParams
import com.accessibility.service.util.PackageManagerUtils
import com.accessibility.service.util.TaskDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.CMDUtil
import com.utils.common.SPUtils
import com.utils.common.ThreadUtils
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener

/**
 * Description:QQ登录
 * Created by Quinin on 2019-07-25.
 **/
open class QQLogin constructor(val myAccessibilityService: MyAccessibilityService) {
    private var mTaskListener: TaskListener? = null
    private var mLoginFailedCount: Int = 0
    private var mUserName: String? = null
    private var mUserPsw: String? = null
    private var mUserId: Int? = null
    private var mNickName: String? = null //账号昵称

    fun initLoginInfo() {
        mUserName = TaskDataUtil.instance.getLogin_name()
        mUserPsw = TaskDataUtil.instance.getLogin_psw()
        mUserId = TaskDataUtil.instance.getPdd_account_id()
    }

    fun login(taskListener: TaskListener) {

        initLoginInfo()
        mLoginFailedCount = 0
        mTaskListener = taskListener

        // mUserName = "2408973767"
        // mUserPsw = "lqy12021004"
        if (!TextUtils.isEmpty(mUserName)) {
            NodeController.Builder()
                .setNodeService(myAccessibilityService)
                .setNodeParams("拒绝", 1)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        login(mUserName!!, mUserPsw!!)
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        login(mUserName!!, mUserPsw!!)
                    }
                })
                .create()
                .execute()
        } else {
            responTaskFailed("账号或者密码为空")
        }
    }

    fun login(userName: String, userPsw: String) {
        isQQloginPage(userName, userPsw)
    }

    /**
     * 是否跳转到登录界面
     */
    private fun isQQloginPage(userName: String, userPsw: String) {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("TIM登录", 0, false, 30)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到QQ登录界面")
                    inputAccount(userName, userPsw)
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("跳转不到QQ登录界面")
                    responTaskFailed("跳转不到QQ登录界面")
                }
            })
            .create()
            .execute()
    }

    /**
     * 输入账号密码
     */
    private fun inputAccount(userName: String, userPsw: String) {
        AdbScriptController.Builder()
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
//                    isLoginSucceed()
//                    isUnvalid()
                    // 输入账号密码后，一般是 验证码校验
                    verifyCode()
//                    retryGetQQ()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed(failedMsg)
                }
            })
            .setXY("540,320")   //账号输入框
            .setXY("1010,320")  //点击清除账号
            .setText(userName)
            .setXY("540,450")   //密码输入框
            .setText(userPsw)
            .setXY("540,680")  //登录按钮
            .create()
            .execute()
    }

    /**
     * 是否直接登录成功
     */
    private fun isLoginSucceed() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("授权并登录", 0, 9)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("授权登录，是否这里卡住.")
                    loginSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    verifyCode()
                }
            })
            .create()
            .execute()
    }

    /**
     * 账号是否被封
     */
    private fun isUnvalid() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("确定", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    retryGetQQ()
                }

                override fun onTaskFailed(failedMsg: String) {
                    isLoginSucceed()
                }
            })
            .create()
            .execute()
    }

    /**
     * 校验验证码
     */
    private fun verifyCode() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("输入验证码", 0, false, 6)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("开始验证码校验")
                    QQLoginVerify(myAccessibilityService).startVerify(VerifyCodeListener())
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("不需要验证码校验")
                    checkLoginResult()
                }
            })
            .create()
            .execute()
    }


    /**
     * 验证码结果监听
     */
    inner class VerifyCodeListener : TaskListener {
        override fun onTaskFinished() {
//            L.i("验证码登录完成,检查登录结果")
            L.i("验证码检验并授权登录成功")
            loginSucceed()
        }

        override fun onTaskFailed(failedMsg: String) {
            L.i("验证登录失败：$failedMsg")
            //responTaskFailed("验证登录失败: $failedMsg")
            checkLoginResult()
        }

    }


    /**
     * 检查登录结果
     */
    private fun checkLoginResult() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("登录失败", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //如果是评论任务，不重新获取账号，直接上报登录失败，正常任务就重新获取账号测试
                    val isCommentTask = TaskDataUtil.instance.isCommentTask()
                    if (!isCommentTask!!)
                        dealAccountError()
                    else {
                        SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).put(Constant.KEY_COMMENT_SUCCESS_CODE,CommentStatus.COMMENT_MISSION_FAILED)
                        responTaskFailed("评论任务：id=${mUserId}-${mUserName}账号已失效")
                    }
                }

                override fun onTaskFailed(failedMsg: String) {
                    authLogin()
                }
            })
            .create()
            .execute()
    }

    /**
     * 授权登录
     */
    private fun authLogin() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("授权并登录", 0, 9)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    loginSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("验证码校验失败")
                }
            })

            .create()
            .execute()
    }

    /**
     * 登录成功
     */
    private fun loginSucceed() {

        NodeController.Builder()
            .setNodeService(myAccessibilityService)
//            .setNodeParams("登录",0,false,5)
            .setNodeParams("个人中心", 0, false, 15)    //时间长一点，防止网络卡顿
            .setTaskListener(object : TaskListener {

                override fun onTaskFinished() {
                    SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).put(Constant.KEY_IS_LOGINED, true)
                    saveAccountName()
                    //closeQQ_TIM()
                    updateAccount(1)
                    myAccessibilityService.setIsLogined(true)
                    mTaskListener?.onTaskFinished()
                }

                override fun onTaskFailed(failedMsg: String) {
                    myAccessibilityService.performBackClick()
                    L.i("找不到个人中心。。。")
                    LoginFailed(myAccessibilityService)
                        .setTypeListener(object : LoginFailed.TypeListener {
                            override fun onResponType(failedType: Int) {
                                L.i("LoginFailed = $failedType")
                                when (failedType) {

                                    LoginFailedType.DROP_LINE -> dealDropLine()
                                    LoginFailedType.UNVAILD -> isUnvalid()
                                    LoginFailedType.ADD_ACCOUNT -> dealAddAccount()
                                }
                            }
                        })
                        .startService()
                }
            })
            .create()
            .execute()

    }

    /**
     *  处理页面返回 “添加账号”
     */
    private fun dealAddAccount() {
        myAccessibilityService
            .performBackClick(5, object : AfterClickedListener {
                override fun onClicked() {
                    myAccessibilityService.performBackClick(5, object : AfterClickedListener {
                        override fun onClicked() {
                            retryGetQQ()
                        }
                    })
                }

            })
    }

    private fun closeQQ_TIM() {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {

                val closeQQ = "am fore-stop ${Constant.QQ_TIM_PKG};" +
                        "am fore-stop ${Constant.QQ_LIATE_PKG};"
                CMDUtil().execCmd(closeQQ)

                return true
            }

            override fun onSuccess(result: Boolean?) {
                L.i("成功关闭 QQ和浏览器")
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
                L.i("关闭QQ和TIM失败")
            }

        })
    }

    /**
     *  掉线情况，重新输入账号密码。
     */
    private fun dealDropLine() {
        initLoginInfo()
        authLogin()
    }

    /**
     * textList:根据text来查找
     * nodeFlag:根据这个字段来判断是哪种方式查找，0：根据view text全查找，1：根据view text半查找，2：根据ID查找，3：根据className查找
     * isClicked:判断是否点击查找的节点
     * editInputText:是否是EditText节点输入内容
     * foundNodeTimeOut：节点查找超时时间 单位秒
     * findNextFlag:当前节点查找失败后，是否继续查找下一个节点，默认是false，不继续查找，true为继续查找
     * nodeListener:节点查找的结果监听
     *
     */

    private fun isNeedAddAccount() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("TIM登录", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("isNeedAddAccount()...：成功找到‘TIM登录’")
                    // 点击返回
                    myAccessibilityService.performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            // 再次点击返回
                            myAccessibilityService.performBackClick(2,
                                object : AfterClickedListener {
                                    override fun onClicked() {
                                        // 回到登录界面，再次拉取账号测试。
                                        // 上报错误账号，清理 QQ轻聊版，TIM数据，再重新登录
                                        updateAccount(2)
                                        //clearQQAndTimData()
//                                    continueLoginQQ()
                                    }
                                })
                        }
                    })
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("isNeedAddAccount()...：没有找到‘TIM登录’")
                    saveAccountName()
                    updateAccount(1)
                    myAccessibilityService.setIsLogined(true)
                    mTaskListener?.onTaskFinished()
                }

            })
            .create()
            .execute()
    }

    private fun clearQQAndTimData() {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {

                var clearDataCmd = "pm clear ${Constant.QQ_TIM_PKG};" +
                        "pm clear ${Constant.QQ_LIATE_PKG};"
                CMDUtil().execCmd(clearDataCmd)

                return true
            }

            override fun onSuccess(result: Boolean?) {
                continueLoginQQ()
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
                L.i("clearQQAndTimData()。。。清理数据失败")
            }

        })
    }

    private fun continueLoginQQ() {

        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("请使用其它方式登录")
            .setNodeParams("QQ登录")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("continueLoginQQ，准备重新拉取账号测试")
                    getAccount()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("continueLginQQ()执行失败")
                }

            })
            .create()
            .execute()

    }

    /**
     * 获取账号昵称
     */
    private fun saveAccountName() {
        SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
            .put(Constant.KEY_PDD_ACCOUNT, mUserName!!)
        /* val accountNode = myAccessibilityService.findViewByText(mUserName!!)
         if (accountNode != null) {
             accountNode.parent?.run {
                 for (i in 0 until childCount) {
                     val child = getChild(i)
                     L.i("childName: ${child.className} 节点内容:${child.text}")
                 }
             }
         }*/
    }

    /**
     * 处理账号或者密码错误和账号失效，重新拉取账号和密码
     */
    private fun dealAccountError() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            //.setNodeParams("1001", 1, false, 5, true)
            //.setNodeParams("该账号涉嫌违规", 1, false, 3)
            .setNodeParams("确定", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    retryGetQQ()
                }

                override fun onTaskFailed(failedMsg: String) {

                }
            })
            .create()
            .execute()
    }

    private fun retryGetQQ() {
        updateAccount(2)
        //判断是否是评论任务，如果是，不请求任务;否则重新请求QQ账号
        val isCommentTask =
            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_TASK_TYPE)
        if (isCommentTask) {
            responTaskFailed("${mUserName}账号失效，评论任务失败")
            return
        }
        //重新取数据并且重新打开pdd开始任务
        getAccount()
        /*if (mLoginFailedCount <= 18) {

        } else {
            responTaskFailed("重新拉取账号超过10次")
        }*/
    }

    /**
     * 拉取账号
     */
    private fun getAccount() {
        val taskId =
            SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                .getInt(Constant.KEY_TASK_ID)
        L.i("账号无效，重新拉取账号，任务ID：$taskId")
        if (taskId > 0) {
            ApiManager()
                .setDataListener(object:DataListener{
                    override fun onSucceed(result: String) {
                        try {
                            L.i("获取账号结果：$result")
                            val taskBean = Gson().fromJson(result,TaskBean::class.java)
                            if(taskBean.code == 200)
                            {
                                saveData(result)

                                ClearDataService().clearData(object:TaskListener{
                                    override fun onTaskFinished() {
                                        myAccessibilityService.sendBroadcast(Intent(MyAccessibilityService.ACTION_TASK_STATUS))
                                        PackageManagerUtils.startActivity(Constant.BUY_TOGETHER_PKG,
                                            "${Constant.BUY_TOGETHER_PKG}.ui.activity.MainFrameActivity")
                                    }

                                    override fun onTaskFailed(failedMsg: String) {
                                        L.i("清理数据失败")
                                        myAccessibilityService.sendBroadcast(Intent(MyAccessibilityService.ACTION_TASK_STATUS))
                                        PackageManagerUtils.startActivity(Constant.BUY_TOGETHER_PKG,
                                            "${Constant.BUY_TOGETHER_PKG}.ui.activity.MainFrameActivity")
                                    }
                                })
                            }else{
                                responTaskFailed("重新拉取账号失败:${taskBean.msg}")
                            }
                            /* val accountBean = Gson().fromJson(result, AccountBean::class.java)
                             if (accountBean.code != 200) {
                                 L.i("重新拉取账号失败: ${accountBean.code}")
                                 return
                             }
                             accountBean.data.apply {
                                 account.run {
                                     updateTaskData(this)
                                     mTaskListener?.let {
                                         //更新账号信息
                                         if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)) {
                                             mUserName = user
                                             mUserPsw = pwd
                                             mUserId = id
                                             mLoginFailedCount++
                                             login(mUserName!!, mUserPsw!!)
                                         } else responTaskFailed("账号密码为空")
                                     }
                                 }
                             }*/
                        } catch (e: Exception) {
                            L.e(e.message)
                            responTaskFailed("重新获取账号失败: ${e.message}")
                        }
                    }

                    override fun onFailed(errorMsg: String) {
                        responTaskFailed("重新拉取账号失败: $errorMsg")
                    }
                })
                .getQQAccount(taskId.toString())
        } else {
            responTaskFailed("任务ID不存在，拉取账号失败")
        }
    }

    /**
     * 保存数据，重新打开拼多多
     */
    private fun saveData(strData: String) {
        /* taskBean.task.account.id = 3234
         taskBean.task.account.user = "210289767"
         taskBean.task.account.pwd="gx95k1g8ra"*/
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
        spUtils.put(Constant.KEY_TASK_DATA, strData, true)
        val taskBean = Gson().fromJson(strData, TaskBean::class.java)

        saveUploadParams(taskBean)
        saveDeviceParams(taskBean)
    }

    /**
     * 保存请求接口需要上传的参数
     */
    private fun saveUploadParams(taskBean: TaskBean) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
        taskBean.task?.apply {
            spUtils.apply {
                L.i("保存的任务ID：$task_id")
                put(Constant.KEY_TASK_ID, task_id,true)

                account?.let {
                    put(Constant.KEY_ACCOUNT_ID, it.id, true)
                }
            }
        }
    }

    /**
     * 保存设备参数
     */
    private fun saveDeviceParams(taskBean: TaskBean) {
        val spUtils = SPUtils.getInstance(Constant.SP_DEVICE_PARAMS)
        taskBean.task?.device?.run {
            spUtils.apply {
                L.i(
                    "模拟imei: $imei 真实imei: ${SPUtils.getInstance(Constant.SP_REAL_DEVICE_PARAMS)
                        .getString(Constant.KEY_REAL_DEVICE_IMEI)}"
                )
                put(DeviceParams.IMEI_KEY, imei, true)
                put(DeviceParams.IMSI_KEY, imsi, true)
                put(DeviceParams.MAC_KEY, mac, true)
                put(DeviceParams.USER_AGENT_KEY, useragent, true)
                put(DeviceParams.BRAND_KEY, brand, true)
                put(DeviceParams.MODEL_KEY, model, true)
                put(DeviceParams.SDK_KEY, android, true)
                put(DeviceParams.SYSTEM_KEY, system, true)
            }
        }

    }

    /**
     * 更新SP中的任务信息，当任务失败时，重新获取开始任务
     */
    private fun updateTaskData(account: AccountBean.DataBean.Account) {
        L.i("更新SP中的任务信息")
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                try {
                    val taskDataStr =
                        SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                            .getString(Constant.KEY_TASK_DATA)
                    Gson().fromJson(taskDataStr, TaskBean::class.java).apply {
                        task.account.run {
                            this.id = account.id
                            this.type = account.type
                            this.user = account.user
                            this.pwd = account.pwd
                        }
                        Gson().toJson(this).let {
                            SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                                .put(Constant.KEY_TASK_DATA, it)

                            return true
                        }
                    }
                } catch (e: Exception) {
                    L.e(e.message, e)
                }

                return false
            }

            override fun onSuccess(result: Boolean?) {
                L.i("保存重新拉取的账号信息结果：$result")
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }
        })
    }


    /**
     * 更新账号状态
     */
    private fun updateAccount(isValid: Int) {
        L.i("账号更新状态，账号ID：$mUserId isValid:$isValid")
        ApiManager()
            .updateQQAcount(mUserId!!, isValid)
    }


    fun responTaskFailed(msg: String) {
        mLoginFailedCount = 0
        mTaskListener?.onTaskFailed(msg)
    }
}