package com.accessibility.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.*
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.login.QQloginService
import com.accessibility.service.login.WXloginService
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
            startLogin()
            searchGoods()
        }
    }

    /**
     * 选择登录
     */
    private fun chooseLogin() {
        when (mCurPageType) {
            PageEnum.INDEX_PAGE -> {
                initTaskData()
                findViewByText("请使用其它方式登录")?.let {
                    setCurPageType(PageEnum.CHOOSE_LOGIN_PAGE)
                    L.i("当前界面：选择登录")
                    if (TaskDataUtil.instance.getLogin_channel() != 1)    //不是微信登录
                        performViewClick(it, 1)
                    else {
                        WXloginService(this).doOnEvent()
                    }
                }
            }

            PageEnum.CHOOSE_LOGIN_PAGE -> {
                findViewByText("QQ登录")?.let {
                    L.i("当前界面：处于选择状态")
                    setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
                    performViewClick(it, 1)
                }
            }

            else -> return
        }
    }

    /**
     * QQ输入账号和密码授权登录
     */
    private fun startLogin() {
        when (mCurPageType) {
            PageEnum.CHOOSING_LOGIN_PAGE -> QQloginService.getInstance(this).doOnEvent()
            PageEnum.QQ_LOGIN_PAGE -> QQloginService.getInstance(this).login()
            PageEnum.QQ_LOGINING_PAGE -> QQloginService.getInstance(this).authLogin()

            else -> return
        }
    }

    /**
     * 搜索商品
     */
    private fun searchGoods() {
        when (mCurPageType) {
            PageEnum.INDEX_PAGE, PageEnum.AUTH_LOGIN_PAGE -> SearchGoodsService.getInstance(this).doOnEvent()
            PageEnum.SEARCH_PAGE -> SearchGoodsService.getInstance(this).jump2search()
            PageEnum.SERARCHING_PAGE -> SearchGoodsService.getInstance(this).searching()
            PageEnum.SEARCH_RESULT_PAGE -> SearchGoodsService.getInstance(this).chooseGood()

            else -> return
        }
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        if (mCurPageType == PageEnum.GOODS_INFO_PAGE) {
            TaskService.getInstance(this).doOnEvent()
        }
    }

}