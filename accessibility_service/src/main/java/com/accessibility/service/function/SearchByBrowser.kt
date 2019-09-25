package com.accessibility.service.function

import android.content.ClipboardManager
import android.content.Context
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.PackageManagerUtils
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-08-15.
 **/
class SearchByBrowser(private val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {

    //540,365
    //970,140
    companion object {
        //    浏览器类型
        const val BROWSER_A: String = "A"
        const val BROWSER_B: String = "B"
    }

    private var mFailedCount: Int = 0


    override fun startService() {
        //复制文本内容到剪贴板，然后打开浏览器
        val goodUrl = TaskDataUtil.instance.getGoodUrl()
        L.i("商品链接：$goodUrl")
        if (goodUrl.isNullOrEmpty()) {
            responFailed("商品链接不能为空")
            return
        }

        val clipboardManager =
            myAccessibilityService.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.text = goodUrl

        L.i("开始打开浏览器")
        PackageManagerUtils.startXiaomiBrowser(Constant.XIAOMI_BROWSER_PKG)
        /*myAccessibilityService.packageManager.getLaunchIntentForPackage(Constant.XIAOMI_BROWSER_PKG).apply {
            if (this != null)
                myAccessibilityService.startActivity(this)
            else responFailed("通过链接进入商品时，未安装浏览器")
        }*/
        //监测浏览器是否打开
        myAccessibilityService.postDelay(Runnable {
            L.i("浏览器已打开")
            checkBrowseType()
//            skipNavigation()
        }, 5)
        /* NodeController.Builder()
             .setNodeService(myAccessibilityService)
             .setNodeFoundListener(object : TaskListener {
                 override fun onTaskFinished() {

                 }

                 override fun onTaskFailed(failedMsg: String) {
                     L.i("浏览器是否打开：$failedMsg")
                     responFailed("浏览器是否打开：$failedMsg")
                 }
             })
             .create()
             .execute()*/
    }

    private fun checkBrowseType() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("快如闪电", 0, 6)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    browseType(BROWSER_A)
                }

                override fun onTaskFailed(failedMsg: String) {
                    browseType(BROWSER_B)
                }
            })
            .create()
            .execute()

    }

    private fun browseType(type: String) {
        when (type) {
            BROWSER_A -> skipNavigation()
            BROWSER_B -> openGoodUrl_TypeB()
        }
    }


    /**
     * 跳过导航
     */
    private fun skipNavigation() {
        AdbScriptController.Builder()
            .setSwipeXY("1000,950", "100,950")
            .setSwipeXY("1000,950", "100,950")
            .setSwipeXY("1000,950", "100,950")
//            .setXY("960,175")   //点击跳过 CDJ坐标
            .setXY("960,110")   // 点击跳过 XKY坐标
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    openGoodUrl()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("跳过导航失败:$failedMsg")
                    responFailed("打开浏览器：应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 打开商品链接
     */
    private fun openGoodUrl() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("同意并使用")
            .setNodeParams("确定", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到商品详情页面")
                    click2pdd()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("跳转商品链接失败:$failedMsg")
                    // B型机会进入此处通过搜索框搜索。
                    clickSearchBox()
                }
            })
            .create()
            .execute()
    }

    /**
     * 打开商品链接
     */
    private fun openGoodUrl_TypeB() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("跳过", 0, true, 10, true)
            .setNodeParams("同意并使用")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
//                    L.i("已跳转到商品详情页面")
//                    click2pdd()
                    clickSearchBox()
                }

                override fun onTaskFailed(failedMsg: String) {
//                    L.i("跳转商品链接失败:$failedMsg")
                    // B型机会进入此处通过搜索框搜索。
//                    clickSearchBox()
                    responFailed("跳转商品链接失败:$failedMsg")
                }
            })
            .create()
            .execute()
    }

    private fun clickSearchBox() {
        val goodUrl = TaskDataUtil.instance.getGoodUrl()
        if (goodUrl.isNullOrEmpty()) {
            responFailed("商品连接为空")
            return
        }
        AdbScriptController.Builder()
            .setXY("550,300")
            .setText(goodUrl)
            .setXY("980,160")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("打开商品连接，准备点击按钮打开拼多多")
                    click2pdd()
                }

                override fun onTaskFailed(failedMsg: String) {
                }
            })
            .create()
            .execute()
    }


    /**
     * 点击跳转到拼多多，960，300
     */
    private fun click2pdd() {
        AdbScriptController.Builder()
            .setXY("960,300", 4000)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    auth2pdd()
                }

                override fun onTaskFailed(failedMsg: String) {
                }
            })
            .create()
            .execute()
    }

    /**
     * 授权跳转到拼多多
     */
    private fun auth2pdd() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("确定", 0, 8)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    mFailedCount++
                    L.i("浏览器授权跳转失败次数：$mFailedCount")
                    if (mFailedCount <= 3)
                        reInputUrl()
                    else responFailed("浏览跳转失败")
                }
            })
            .create()
            .execute()
    }

    private fun reInputUrl(){
        val goodUrl = TaskDataUtil.instance.getGoodUrl()
        if (goodUrl.isNullOrEmpty()) {
            responFailed("商品连接为空")
            return
        }
        AdbScriptController.Builder()
            .setXY("540,160")
            .setText(goodUrl)
            .setXY("980,160")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("打开商品连接，准备点击按钮打开拼多多")
                    click2pdd()
                }

                override fun onTaskFailed(failedMsg: String) {
                }
            })
            .create()
            .execute()
    }

}