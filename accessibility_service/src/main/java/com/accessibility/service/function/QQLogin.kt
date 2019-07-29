package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.TaskDataUtil
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
        mTaskListener = taskListener
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("TIM登录")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到QQ登录界面")
                    AdbScriptController.Builder()
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                verifyCode()
                            }

                            override fun onTaskFailed(failedText: String) {
                                responTaskFailed("$failedText cmd was executed failed.")
                            }
                        })
                        //.setXY("777,1600", 10 * 1000L)
                        //.setXY("540,310")
                        .setXY("540,320")   //账号输入框
                        .setText(TaskDataUtil.instance.getLogin_name()!!)
                        .setXY("540,450")   //密码输入框
                        .setText(TaskDataUtil.instance.getLogin_psw()!!)
                        .setXY("540,680")  //登录按钮
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("$failedText node was not found.登录失败")
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
            .setNodeParams("登录失败", 0, false, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    dealInputError()
                    //responTaskFailed("账号登录失败")
                }

                override fun onTaskFailed(failedText: String) {
                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                L.i("登录成功")
                                myAccessibilityService.setIsLogined(true)
                                mTaskListener?.onTaskFinished()
                            }

                            override fun onTaskFailed(failedText: String) {
                                responTaskFailed("$failedText was not found.授权登录失败")
                            }
                        })
                        .setNodeParams("授权并登录", 0, 2)
                        .create()
                        .execute()
                }
            })
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
                    dealAccountError()
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
            .setNodeParams("1001", 1, false, 3, true)
            //.setNodeParams("该账号涉嫌违规", 1, false, 3)
            .setNodeParams("确定", 0, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //重新请求QQ账号
                    L.i("QQ账号已经被封")
                    updateAccount()
                    getAccount()
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
    fun updateAccount() {
        val accountId = SPUtils.getInstance(myAccessibilityService, "pinduoduo_task_sp").getInt("key_account_id")
        L.i("账号无效，更新状态，账号ID：$accountId")
        ApiManager.instance
            .updateQQAcount(accountId, false)
    }

    /**
     * 拉取账号
     */
    private fun getAccount() {
        val taskId = SPUtils.getInstance(myAccessibilityService, "pinduoduo_task_sp").getInt("key_task_id")
        L.i("账号无效，重新拉取账号，任务ID：$taskId")
        if (taskId > 0) {
            ApiManager.instance
                .setDataListener(object : DataListener {
                    override fun onSucceed(result: String) {
                        //todo 重新输入账号和密码
                    }

                    override fun onFailed(errorMsg: String) {
                        responTaskFailed("重新拉去账号失败: $errorMsg")
                    }
                })
                .getQQAccount(taskId.toString())
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