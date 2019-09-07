package com.accessibility.service.function

import android.content.ClipboardManager
import android.content.Context
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.PackageManagerUtils

/**
 * Description:
 * Created by Quinin on 2019-08-15.
 **/
class SearchByBrowser(private val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {

    //540,365
    //970,140
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
        PackageManagerUtils.getInstance().startApplication(Constant.XIAOMI_BROWSER_PKG)
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
        }, 8)
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
            .setNodeParams("跳过", 1, 3)
            .setNodeParams("游戏", 1, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    skipNavigation()
                }

                override fun onTaskFailed(failedMsg: String) {
                    skipNavigation()
                }
            })
            .create()
            .execute()

    }

    /**
     * 跳过导航
     */
    private fun skipNavigation() {
        AdbScriptController.Builder()
            .setSwipeXY("1000,950", "100,950")
            .setSwipeXY("1000,950", "100,950")
            .setSwipeXY("1000,950", "100,950")
            .setXY("960,175")   //点击跳过 CDJ坐标
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
                    //responFailed("跳转商品链接失败:$failedMsg")
                    clickSearchBox()
                }
            })
            .create()
            .execute()
    }

    private fun clickSearchBox() {
        AdbScriptController.Builder()
            .setXY("550,300")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("clickSearchBox()。。。即将输入商品 URL")
                    inputGoodUrl()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("clickSearchBox()。。。点击搜索框失败")
                }

            })
            .create()
            .execute()
    }

    private fun inputGoodUrl() {
        val goodUrl = TaskDataUtil.instance.getGoodUrl()
        AdbScriptController.Builder()
            .setText(goodUrl!!)
            .setXY("980,160")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //
                    L.i("inputGoodUrl()")
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
            .setXY("960,300", 8000)

            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    auth2pdd()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("跳转商品链接失败：应用未获得root权限")
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
            .setNodeParams("确定")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSucceed()

                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("浏览器授权跳转失败：$failedMsg")
                    responFailed("浏览器授权跳转失败：$failedMsg")
                }
            })
            .create()
            .execute()
    }
}