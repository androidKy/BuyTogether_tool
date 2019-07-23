package com.accessibility.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.TaskService
import com.accessibility.service.listener.TaskFinishedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {


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
            chooseLogin()
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
            NodeController.Builder()
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("判断是跳转到主页还是登录界面")
                        loginByQQ()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed(failedText)
                    }
                })
                .setNodeService(this@MyAccessibilityService)
                .setNodeParams("好的", true)
                .setNodeParams("允许", true)
                .setNodeParams("个人中心")
                .setNodeParams("点击登录")
                .setNodeParams("请使用其它方式登录")
                .setNodeParams("QQ登录")
                .create()
                .execute()
        }
    }

    /**
     * QQ输入账号和密码授权登录
     */
    private fun loginByQQ() {
        if (mCurPageType == PageEnum.CHOOSING_LOGIN_PAGE) {
            setCurPageType(PageEnum.INDEX_PAGE)

            val login_name = TaskDataUtil.instance.getLogin_name()
            L.i("login_name: $login_name")

            /* NodeController.Builder()
                 .setNodeService(this)
                 .setTaskListener(object : TaskListener {
                     override fun onTaskFinished() {
                         L.i("登录成功")
                         setIsLogined(true)
                         searchGoods()
                     }

                     override fun onTaskFailed(failedText: String) {
                         L.i("$failedText was not found.")
                         responTaskFailed(failedText)
                     }
                 })
                 //.setNodeParams("允许")
                 //.setNodeParams("允许", 0, 5)
                 //.setNodeParams("同意", 0, 5)
                 .setNodeParams("登录", 0, 5)
                 //.setNodeFilter("首页")
                 .setNodeParams("QQ号/手机号/邮箱", 0, false, TaskDataUtil.instance.getLogin_name()!!)
                 .setNodeParams("com.tencent.mobileqq:id/password", 2, false, TaskDataUtil.instance.getLogin_psw()!!)
                 .setNodeParams("com.tencent.mobileqq:id/login", 2)
                 .setNodeParams("登录", 0, 30)   //授权登录
                 .create()
                 .execute()*/
            AdbScriptController.Builder()
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("登录成功")
                        setIsLogined(true)
                        searchGoods()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed(failedText)
                    }
                })
                //.setXY("777,1600", 10 * 1000L)
                .setXY("540,320")   //账号输入框
                .setText(TaskDataUtil.instance.getLogin_name()!!)
                .setXY("540,450")   //密码输入框
                .setText(TaskDataUtil.instance.getLogin_psw()!!)
                .setXY("540,680")  //登录按钮
                .create()
                .execute()
        }
    }

    /**
     * 搜索商品
     */
    private fun searchGoods() {
        if (mCurPageType == PageEnum.INDEX_PAGE) {
            L.i("mIsLogined : $mIsLogined")
            setCurPageType(PageEnum.GOODS_INFO_PAGE)

            NodeController.Builder()
                .setNodeService(this)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("搜索商品成功")
                        doTask()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed(failedText)
                    }
                })
                .setNodeParams("授权并登录", true)
                .setNodeParams("搜索", true)
                .setNodeParams("com.xunmeng.pinduoduo:id/a8f", 2, 5, true)
                .setNodeParams("com.xunmeng.pinduoduo:id/fq", 2, 5)
                .setNodeParams(WidgetConstant.EDITTEXT, 3, false, TaskDataUtil.instance.getGoods_name()!!)
                .setNodeParams("搜索")
                .setNodeParams("7.9", 0, isClicked = true, isScrolled = true)
                .create()
                .execute()
        }
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        if (mCurPageType == PageEnum.GOODS_INFO_PAGE) {
            setCurPageType(PageEnum.PAYING_PAGE)

            TaskService.getInstance(this)
                .setTaskFinishedListener(object : TaskFinishedListener {
                    override fun onTaskFinished(result: Boolean) {
                        L.i("任务完成，重新开始下一轮任务: $result")
                        if (result) {
                            initParams()
                            mTaskListener?.onTaskFinished()
                        } else {
                            mTaskListener?.onTaskFailed("")
                        }
                    }
                })
                .doOnEvent()
        }
    }

    private fun responTaskFailed(msg: String) {
        mTaskListener?.onTaskFailed(msg)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
    }

}