package com.accessibility.service.function

import android.content.Intent
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.safframework.log.L
import com.utils.common.screen.ScreenShotActivity
import com.utils.common.verifycode.VerifyCodeUtils
import java.io.File

/**
 * Description:
 * Created by Quinin on 2019-07-26.
 **/
class QQLoginVerify(val myAccessibilityService: MyAccessibilityService) {
    private var mVerifyCount = 0
    private var mTaskListener: TaskListener? = null
    /**
     * 开始校验验证码
     */
    fun startVerify(taskListener: TaskListener) {
        mTaskListener = taskListener
        mVerifyCount++
        if (mVerifyCount > 5) {
            responTaskFailed("验证码校验失败次数达到限制：$mVerifyCount")
            return
        }
        screenShot()
    }

    /**
     * 截取屏幕
     */
    private fun screenShot() {
        Intent("android.screen.shot").run {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            myAccessibilityService.startActivity(this)

            ScreenShotActivity.setScreenShotListener(ScreenShotListenerImpl())
        }

        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("立即开始", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("点击开始截图")
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("验证码截图失败")
                }
            })
            .create()
            .execute()
    }

    inner class ScreenShotListenerImpl : ScreenShotActivity.ScreenShotListener {
        override fun onScreenShotFinish() {
            L.i("屏幕截图完成,开始提取验证码")
            pictureShot()
        }

    }

    /**
     * 把屏幕截图裁剪成验证码的图片,需要延时
     */
    private fun pictureShot() {
        myAccessibilityService.postDelay(
            Runnable {
                val fileDir = myAccessibilityService.getExternalFilesDir("screenshot")
                val picUrl = "${fileDir!!.absoluteFile.path}/screenShot.png"
                L.i("验证码截图的位置：$picUrl")
                val picFile = File(picUrl)
                if (!picFile.exists()) {
                    responTaskFailed("验证码截图失败")
                } else {
                    VerifyCodeUtils.doOcr(picFile, object : VerifyCodeUtils.ResultListener {
                        override fun onResult(result: String) {
                            L.i("校验验证码结果：$result")
                            if (result.isNotEmpty()) {
                                inputVerifyCode(result)
                            } else {
                                L.i("验证码校验失败，重新校验")
                                updateVerifyCode()
                            }
                        }
                    })
                }
            }, 3
        )
    }


    /**
     * 更新验证码
     */
    private fun updateVerifyCode() {
        AdbScriptController.Builder()
            .setXY("540,450")   //更换验证码
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("验证码图片已更新，重新开始校验")
                    myAccessibilityService.postDelay(Runnable {
                        startVerify(mTaskListener!!)
                    }, 5)
                }

                override fun onTaskFailed(failedMsg: String) {

                }
            })
            .create()
            .execute()
/*
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("看不清？换一张", 1, 5)
            .setNodeFoundListener(object : TaskListener {
                override fun onTaskFinished() {
                    2000)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("验证码图片已更新失败")
                    responTaskFailed("更新验证码失败")
                }
            })
            .create()
            .execute()*/
    }

    /**
     * 输入验证码·
     */
    private fun inputVerifyCode(verifyCode: String) {
        AdbScriptController.Builder()
            .setXY("540,590")
            .setText(verifyCode)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    NodeController.Builder()
                        .setNodeService(myAccessibilityService)
                        // .setNodeParams(WidgetConstant.EDITTEXT, 3, false, false, verifyCode)
                        .setNodeParams("完成", 0, 10)
                        .setNodeParams("授权并登录",0,5)

                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
//                                checkVerifyResult()
                                LoginFailed(myAccessibilityService)
                                    .setTaskListener(object :TaskListener{
                                        override fun onTaskFinished() {
                                            mTaskListener?.onTaskFinished()
                                        }

                                        override fun onTaskFailed(failedMsg: String) {
                                            checkVerifyResult()
                                        }

                                    })
                                    .startService()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                L.i("$failedMsg was not found.")
                                responTaskFailed("登录验证失败")
                            }
                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedMsg: String) {

                }
            })
            .create()
            .execute()
    }

    /**
     * 检查校验结果，如果页面没发生跳转，验证码错误，重新请求验证码
     */
    private fun checkVerifyResult() {
        myAccessibilityService.apply {
            postDelay(Runnable {
                NodeController.Builder()
                    .setNodeService(this)
                    .setNodeParams("输入验证码", 0, 3)
                    .setTaskListener(object : TaskListener {
                        override fun onTaskFinished() {
                            //验证码错误
                            L.i("验证码错误")
                            startVerify(mTaskListener!!)
                        }

                        override fun onTaskFailed(failedMsg: String) {
                            mTaskListener?.onTaskFinished()
                        }
                    })
                    .create()
                    .execute()
            }, 5)
        }
    }


    fun responTaskFailed(msg: String) {
        mVerifyCount = 0
        myAccessibilityService.setCurPageType(PageEnum.START_PAGE)
        mTaskListener?.onTaskFailed(msg)
    }
}