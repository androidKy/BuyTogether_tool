package com.accessibility.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.BuyGoodsService
import com.accessibility.service.function.LoginService
import com.accessibility.service.function.SearchGoodsService
import com.accessibility.service.function.TaskService
import com.accessibility.service.listener.TaskFinishedListener
import com.accessibility.service.login.QQloginService
import com.accessibility.service.page.PageEnum
import com.safframework.log.L

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {


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
            LoginService.getInstance(this)
                .setTaskFinishedListener(object : TaskFinishedListener {
                    override fun onTaskFinished() {
                        loginByQQ()
                    }
                })
                .doOnEvent()
        }
    }

    /**
     * QQ输入账号和密码授权登录
     */
    private fun loginByQQ() {
        if (mCurPageType == PageEnum.CHOOSING_LOGIN_PAGE) {
            QQloginService.getInstance(this)
                .setTaskFinishedListener(object : TaskFinishedListener {
                    override fun onTaskFinished() {
                        searchGoods()
                    }
                })
                .doOnEvent()
        }
    }

    /**
     * 搜索商品
     */
    private fun searchGoods() {
        if (mCurPageType == PageEnum.INDEX_PAGE) {
            L.i("mIsLogined : $mIsLogined")
            if (mIsLogined) {
                SearchGoodsService.getInstance(this)
                    .setTaskFinishedListener(object : TaskFinishedListener {
                        override fun onTaskFinished() {
                            doTask()
                        }
                    })
                    .doOnEvent()
            }
        }
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        if (mCurPageType == PageEnum.GOODS_INFO_PAGE) {
            TaskService.getInstance(this)
                .setTaskFinishedListener(object : TaskFinishedListener {
                    override fun onTaskFinished() {
                        BuyGoodsService.getInstance(this@MyAccessibilityService).doOnEvent()
                    }
                })
                .doOnEvent()
        }
    }


}