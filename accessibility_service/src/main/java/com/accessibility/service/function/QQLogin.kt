package com.accessibility.service.function

import android.text.TextUtils
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.data.AccountBean
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.SPUtils
import com.utils.common.pdd_api.ApiManager
import com.utils.common.pdd_api.DataListener

/**
 * Description:QQ登录
 * Created by Quinin on 2019-07-25.
 **/
open class QQLogin constructor(val myAccessibilityService: MyAccessibilityService) {
    private var mTaskListener: TaskListener? = null
    private var mLoginFailedCount: Int = 0

    fun login(taskListener: TaskListener) {
        mLoginFailedCount = 0
        mTaskListener = taskListener
        val userName = TaskDataUtil.instance.getLogin_name()
        if (!TextUtils.isEmpty(userName)) {
            login(userName!!, TaskDataUtil.instance.getLogin_psw()!!)
        } else {
            responTaskFailed("账号或者密码为空")
        }
    }

    fun login(userName: String, userPsw: String) {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("TIM登录", 0, false, 20)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到QQ登录界面")
                    AdbScriptController.Builder()
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                verifyCode()
                            }

                            override fun onTaskFailed(failedText: String) {
                                responTaskFailed(failedText)
                            }
                        })
                        //.setXY("777,1600", 10 * 1000L)
                        //.setXY("540,310")
                        //.setXY("1010,450")  //点击清除密码
                        .setXY("540,320")   //账号输入框
                        .setXY("1010,320")  //点击清除账号
                        .setText(userName)
                        .setXY("540,450")   //密码输入框
                        .setText(userPsw)
                        .setXY("540,680")  //登录按钮
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("跳转不到QQ登录界面")
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
            .setNodeParams("输入验证码", 0, false, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("开始验证码校验")
                    QQLoginVerify(myAccessibilityService).startVerify(VerifyCodeListener())
                }

                override fun onTaskFailed(failedText: String) {
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

        override fun onTaskFailed(failedText: String) {
            L.i("验证登录失败：$failedText")
            responTaskFailed("验证登录失败: $failedText")
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
                    //dealInputError()
                    dealAccountError()
                    //responTaskFailed("账号登录失败")
                }

                override fun onTaskFailed(failedText: String) {
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
                    L.i("登录成功")
                    val accountId =
                        SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                            .getInt(Constant.KEY_ACCOUNT_ID)
                    updateAccount(accountId, 1)
                    myAccessibilityService.setIsLogined(true)
                    mTaskListener?.onTaskFinished()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("验证码校验失败")
                }
            })
            .setNodeParams("授权并登录", 0, 5)
            .create()
            .execute()
    }

    /**
     * 处理输入错误
     * 账号或者密码错误码：1001
     * 账号为空：3103
     * 密码为空：3104
     */
    private fun dealInputError() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("3104", 1, false, 3, true)
            .setNodeParams("3103", 1, false, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("账号或者密码为空，重新输入密码")
                    //切换输入法，重新输入账号和密码
                    responTaskFailed("屏幕暂未适配，输入账号失败")
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("账号错误,重新拉取账号")
                    //dealAccountError()
                }
            })
            .create()
            .execute()
    }

    /**
     * 处理账号或者密码错误和账号失效，重新拉取账号和密码
     */
    private fun dealAccountError() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("1001", 1, false, 5, true)
            //.setNodeParams("该账号涉嫌违规", 1, false, 3)
            .setNodeParams("确定", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //重新请求QQ账号
                    L.i("QQ账号已经被封")
                    val accountId =
                        SPUtils.getInstance(myAccessibilityService, Constant.SP_TASK_FILE_NAME)
                            .getInt(Constant.KEY_ACCOUNT_ID)
                    updateAccount(accountId, 2)
                    if (mLoginFailedCount <= 10) {
                        getAccount()
                    } else {
                        responTaskFailed("重新拉取账号超过10次")
                    }
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("账号涉嫌违规被冻结: $failedText was not found.")
                }
            })
            .create()
            .execute()
    }

    /**
     * 更新账号状态
     */
    fun updateAccount(accountId: Int, isValid: Int) {
        L.i("账号无效，更新状态，账号ID：$accountId")
        ApiManager()
            .updateQQAcount(accountId, isValid)
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
                            Gson().fromJson(result, AccountBean::class.java).data.account.run {
                                mTaskListener?.let {
                                    //更新账号信息
                                    if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)) {
                                        mLoginFailedCount++
                                        login(user, pwd)
                                    } else responTaskFailed("账号密码为空")
                                }
                            }
                        } catch (e: Exception) {
                            L.e(e.message)
                            responTaskFailed("账号返回数据格式有误: ${e.message}")
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

    fun responTaskFailed(msg: String) {
        mLoginFailedCount = 0
        myAccessibilityService.setCurPageType(PageEnum.START_PAGE)
        mTaskListener?.onTaskFailed(msg)
    }
}