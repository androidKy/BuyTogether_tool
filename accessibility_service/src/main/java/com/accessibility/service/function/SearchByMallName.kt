package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:根据搜索店铺找到商品
 * Created by Quinin on 2019-08-15.
 **/
class SearchByMallName(val myAccessibilityService: MyAccessibilityService) : BaseAcService(myAccessibilityService) {

    override fun startService() {
        val mallName = TaskDataUtil.instance.getMall_name()
        if (mallName.isNullOrEmpty()) {
            responFailed("店铺名称为空")
            return
        }
        AdbScriptController.Builder()
            .setSwipeXY("540,1100", "540,1600")
            .setXY(SearchGoods.XY_SEARCH_RESULT_EDITTEXT)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputMallName(mallName)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 输入店铺名字查找店铺
     */
    private fun inputMallName(mallName: String) {
        //根据店铺前面几个字查找
        var searchMallName: String = mallName
        if (mallName.length > 6) {
            searchMallName = mallName.substring(0, mallName.length - 3)
        }
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("搜索店铺")
            .setNodeParams(WidgetConstant.EDITTEXT, 3, false, searchMallName)
            .setNodeParams("搜索")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    findMall(mallName)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("搜索店铺($mallName)失败")
                }
            })
            .create()
            .execute()
    }

    /**
     * 查找店铺
     */
    private fun findMall(mallName: String) {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams(mallName, 0, true, true, 5, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到店铺:$mallName")
                    searchGood()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("查找店铺($mallName)失败")
                }
            })
            .create()
            .execute()
    }

    /**
     * 搜索商品
     */
    private fun searchGood() {
        val goodName = TaskDataUtil.instance.getGoods_name()
        if (goodName.isNullOrEmpty()) {
            responFailed("商品名字为空")
            return
        }
        AdbScriptController.Builder()
            .setXY("895,135")   //搜索入口
            .setText(goodName)  //搜索输入框
            .setXY("990,150")   //搜索按钮坐标
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    findGood(goodName)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("根据店铺搜索商品：应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 根据商品名字查找商品
     */
    private fun findGood(goodName: String) {
        val goodPrice = TaskDataUtil.instance.getSearchPrice()

        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams(goodPrice!!,1)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("查找商品($goodName)失败")
                }
            })
            .create()
            .execute()
    }

}