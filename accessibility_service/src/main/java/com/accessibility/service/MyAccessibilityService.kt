package com.accessibility.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.TaskService
import com.accessibility.service.listener.TaskFinishedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.TaskDataUtil
import com.utils.common.ThreadUtils
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
open class MyAccessibilityService : BaseAccessibilityService() {


    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
    }

    override fun onInterrupt() {

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
        if (mCurPageType == PageEnum.START_PAGE) {
            initTaskData()
            L.i("拼多多登录界面")
            setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
            NodeController.Builder()
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("已跳转到QQ登录界面")
                        loginByQQ()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                    }
                })
                .setNodeService(this@MyAccessibilityService)
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

            NodeController.Builder()
                .setNodeService(this)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("登录成功")
                        setIsLogined(true)
                        searchGoods()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                    }
                })
                .setNodeParams("登录")
                //.setNodeFilter("首页")
                .setNodeParams(WidgetConstant.EDITTEXT, 3, false, TaskDataUtil.instance.getLogin_name()!!)
                .setNodeParams("com.tencent.mobileqq:id/password", 2, false, TaskDataUtil.instance.getLogin_psw()!!)
                .setNodeParams("com.tencent.mobileqq:id/login", 2)
                .setNodeParams("登录")
                .create()
                .execute()

            /*   QQloginService.getInstance(this)
                   .setTaskFinishedListener(object : TaskFinishedListener {
                       override fun onTaskFinished() {
                           searchGoods()
                       }
                   })
                   .doOnEvent()*/
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
                    }
                })
                .setNodeParams("搜索", 0, 5)
                .setNodeParams("com.xunmeng.pinduoduo:id/a8f", 2, 5)
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
                    override fun onTaskFinished() {
                        L.i("任务完成，重新开始下一轮任务")
                        initParams()
                    }
                })
                .doOnEvent()
        }
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
    }
}