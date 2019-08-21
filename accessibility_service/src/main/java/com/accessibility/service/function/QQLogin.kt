package com.accessibility.service.function

import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.data.AccountBean
import com.accessibility.service.data.TaskBean
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.google.gson.Gson
import com.safframework.log.L
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

    fun login(taskListener: TaskListener) {
        mLoginFailedCount = 0
        mTaskListener = taskListener
        mUserName = TaskDataUtil.instance.getLogin_name()
        mUserPsw = TaskDataUtil.instance.getLogin_psw()
        mUserId = TaskDataUtil.instance.getPdd_account_id()
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
                    //isLoginSucceed()
                    isUnvalid()
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
            .setNodeParams("授权并登录", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
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
            .setNodeParams("输入验证码", 0, false, 8)
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
            L.i("验证码登录完成,检查登录结果")
            checkLoginResult()
        }

        override fun onTaskFailed(failedMsg: String) {
            L.i("验证登录失败：$failedMsg")
            responTaskFailed("验证登录失败: $failedMsg")
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
                    else
                        responTaskFailed("评论任务：账号已失效")
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
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    loginSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("验证码校验失败")
                }
            })
            .setNodeParams("授权并登录", 0, 5)
            .create()
            .execute()
    }

    private fun loginSucceed() {
        L.i("登录成功，账号ID: $mUserId 账号名: $mUserName")
        saveAccountName()
        updateAccount(1)
        myAccessibilityService.setIsLogined(true)
        mTaskListener?.onTaskFinished()
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
                    responTaskFailed("账号涉嫌违规被冻结: $failedMsg was not found.")
                }
            })
            .create()
            .execute()
    }

    private fun retryGetQQ() {
        //重新请求QQ账号
        updateAccount(2)
        if (mLoginFailedCount <= 18) {
            getAccount()
        } else {
            responTaskFailed("重新拉取账号超过10次")
        }
    }

    /**
     * 拉取账号
     */
    private fun getAccount() {
        val taskId =
            SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME).getInt(Constant.KEY_TASK_ID)
        L.i("账号无效，重新拉取账号，任务ID：$taskId")
        if (taskId > 0) {
            ApiManager()
                .getQQAccount(taskId.toString(), object : DataListener {
                    override fun onSucceed(result: String) {
                        try {
                            L.i("获取账号结果：$result")
                            val accountBean = Gson().fromJson(result, AccountBean::class.java)
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
                            }
                        } catch (e: Exception) {
                            L.e(e.message)
                            responTaskFailed("重新获取账号失败: ${e.message}")
                        }
                    }

                    override fun onFailed(errorMsg: String) {
                        responTaskFailed("重新拉取账号失败: $errorMsg")
                    }
                })
        } else {
            responTaskFailed("任务ID不存在，拉取账号失败")
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
                    val taskDataStr = SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
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