package com.accessibility.service

import android.content.Context
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.CommentTaskService
import com.accessibility.service.function.LoginService
import com.accessibility.service.function.SearchGoods
import com.accessibility.service.function.TaskService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.CMDUtil
import com.utils.common.ThreadUtils

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {

    private var mScreenWidth: Int = 1080
    private var mScreenHeight: Int = 1920

    private var testFlag :Boolean = false


    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
        var mTaskListener: TaskListener? = null

        fun setTaskListener(taskListener: TaskListener) {
            mTaskListener = taskListener
        }

    }

    override fun onInterrupt() {

    }

    override fun onCreate() {
        super.onCreate()
        L.i("MyAccessibilityService onCreate()")
        mScreenWidth = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_width", 0)
        mScreenHeight = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_height", 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("MyAccessibilityService onDestroy()")
    }

    /**
     * 在代码中通过setServiceInfo设置
     * 建议使用meta-data的方式进行配置，因为我实践过程中发现有的属性不能通过代码配置
     */
    override fun onServiceConnected() {

        /* serviceInfo?.let {
             it.packageNames = arrayOf(PKG_PINDUODUO, PKG_QQ)
             serviceInfo = it
         }*/
        super.onServiceConnected()
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {

        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        val eventType = event?.eventType
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED
        ) {

            try {
                chooseLogin()
//                paying()
//                checkPayResult()
//                test()
            } catch (e: Exception) {
                L.e(e.message)
            }
        }
    }

//    private fun test() {
//        NodeController.Builder()
//            .setNodeService(this)
//            .setNodeParams("不支持",1,false,10,true)
//            .setTaskListener(object :TaskListener{
//                override fun onTaskFinished() {
//                    L.i("成功找到")
//                }
//
//                override fun onTaskFailed(failedMsg: String) {
//                    L.i("成功找不到")
//                }
//
//            })
//            .create()
//            .execute()
//
//    }

    /**
     * 正在支付界面时，选择支付宝方式
     */
    private fun paying() {
        if (mCurPageType == PageEnum.PAYING_PAGE) {
            L.i("正在支付")
            val morePayChannel = findViewByText("更多支付方式")
            if (morePayChannel != null) {
                performViewClick(morePayChannel, object : AfterClickedListener {
                    override fun onClicked() {
                        L.i("更多支付方式节点被点击")
                        performViewClick(findViewByText("支付宝"), 0, object : AfterClickedListener {
                            override fun onClicked() {
                                L.i("支付宝方式被点击")
                                setCurPageType(PageEnum.PAY_SUCCEED)
                            }
                        })
                    }
                })
            }
            val alipayNode = findViewByText("支付宝")
            if (alipayNode != null) {
                performViewClick(alipayNode, object : AfterClickedListener {
                    override fun onClicked() {
                        L.i("支付宝节点被点击")
                        setCurPageType(PageEnum.PAY_SUCCEED)
                    }
                })
            }
        }
    }

    /**
     * 检查支付成功与否
     */
    private fun checkPayResult() {
        if (mCurPageType == PageEnum.PAY_SUCCEED) {
            L.i("支付结果：")
        }
    }

    /**
     * 选择登录
     */
    private fun chooseLogin() {
        if (mCurPageType == PageEnum.START_PAGE && !mIsInited) {
            initTaskData()
            L.i("拼多多登录界面")
            setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
            /*   OcrUtils.recognizePic(
                   File("/storage/emulated/0/Android/data/screenShot.png"),
                   this.applicationContext
               )
               return
   */

            NodeController.Builder()
                .setNodeService(this@MyAccessibilityService)
                .setNodeParams("请使用其它方式登录", 0, 2)
                .setNodeParams("QQ登录",0,2)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("判断是跳转到主页还是登录界面")
                        LoginService(this@MyAccessibilityService).login(LoginListenerImpl())
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("$failedMsg was not found.")
                        //responTaskFailed("拼多多登录授权失败")
                        enterLoginFailed()
                    }
                })
                .create()
                .execute()
        }
    }

    private fun enterLoginFailed() {
        NodeController.Builder()
            .setNodeService(this@MyAccessibilityService)
            .setNodeParams("好的", 0, 15, true)
            .setNodeParams("允许", 0, 3, true)
            .setNodeParams("个人中心", 0, 3, true)
            .setNodeParams("点击登录", 0, 2, true)
            .setNodeParams("请使用其它方式登录")
            .setNodeParams("QQ登录")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("判断是跳转到主页还是登录界面")
                    LoginService(this@MyAccessibilityService).login(LoginListenerImpl())
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    responTaskFailed("拼多多登录授权失败")
                }
            })
            .create()
            .execute()
    }

    inner class LoginListenerImpl : TaskListener {
        override fun onTaskFinished() {
            //登录完成后，判断是评论任务还是正常任务
            if (!TaskDataUtil.instance.isCommentTask()!!) {
                L.i("开始自动执行正常任务")
                SearchGoods(this@MyAccessibilityService)
                    .setTaskListener(object : TaskListener {
                        override fun onTaskFinished() {
                            doTask()
                        }

                        override fun onTaskFailed(failedMsg: String) {
                            responTaskFailed(failedMsg)
                        }
                    })
                    .startService()
            } else {
                L.i("开始自动执行评论任务")
                CommentTaskService(this@MyAccessibilityService)
                    .setTaskListener(object : TaskListener {
                        override fun onTaskFinished() {
                            mHandler.postDelayed({
                                responTaskFinished()
                            }, 3 * 1000)
                        }

                        override fun onTaskFailed(failedMsg: String) {
                            responTaskFailed(failedMsg)
                        }
                    })
                    .startService()
            }
        }

        override fun onTaskFailed(failedMsg: String) {
            responTaskFailed(failedMsg)
        }
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        TaskService(this)
            .setScreenDensity(mScreenWidth, mScreenHeight)
            .setTaskFinishedListener(object : TaskListener {
                override fun onTaskFinished() {
                    //uploadOrderInfo()
                    mHandler.postDelayed({
                        responTaskFinished()
                    }, 8 * 1000)
                }

                override fun onTaskFailed(failedMsg: String) {
                    //mTaskListener?.onTaskFailed(failedText)
                    responTaskFailed(failedMsg)
                }
            })
            .doOnEvent()
    }


    private fun responTaskFinished() {
        L.i("任务完成，重新开始下一轮任务")
        initParams()
        mTaskListener?.onTaskFinished()
    }

    private fun responTaskFailed(msg: String) {
        initParams()
        mTaskListener?.onTaskFailed(msg)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
        mIsInited = false
        mIsLogined = false
    }

}