package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.NodeUtils
import com.accessibility.service.util.SingletonHolder
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description: 搜索商品
 * Created by Quinin on 2019-07-02.
 **/
class SearchGoodsService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<SearchGoodsService, MyAccessibilityService>(::SearchGoodsService)

    /**
     * 导航栏点击搜索跳转到搜索界面
     */
    override fun doOnEvent() {
        L.i("mCurPageType == PageEnum.INDEX_PAGE")
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) return
                    nodeService.apply {
                        setCurPageType(PageEnum.SEARCH_PAGE)
                        L.i("搜索className: ${nodeInfo?.className} \n parentClassName: ${nodeInfo?.parent?.className}")
                        performViewClick(nodeInfo?.parent, 1)
                    }
                }
            })
            .getNodeByText(nodeService, "搜索")
    }

    /**
     * 处于搜索界面
     */
    fun jump2search() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) return
                    nodeService.apply {
                        setCurPageType(PageEnum.SERARCHING_PAGE)
                        performViewClick(nodeInfo, 1)
                    }
                }
            })
            .getNodeById(nodeService, "com.xunmeng.pinduoduo:id/fq")
    }

    /**
     * 开始搜索商品
     */
    fun searching() {
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) return
                    nodeService.apply {
                        setCurPageType(PageEnum.SEARCH_RESULT_PAGE)
                        L.i(
                            "开始搜索商品：${TaskDataUtil.instance.getGoods_name()} \n" +
                                    "accountName: ${TaskDataUtil.instance.getLogin_name()}"
                        )
                        WidgetConstant.setEditText(TaskDataUtil.instance.getGoods_name(), nodeInfo)

                        findViewById("com.xunmeng.pinduoduo:id/lg").let {
                            performViewClick(it, 2)
                        }
                    }
                }
            })
            .getSingleNodeByClassName(nodeService, WidgetConstant.EDITTEXT)
    }

    /**
     * 选择商品
     */
    fun chooseGood() {
        //todo 根据商品名称寻找特定的商品
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.let {
                        L.i("选择商品：${it.text}")
                        nodeService.setCurPageType(PageEnum.GOODS_INFO_PAGE)
                        nodeService.performViewClick(it, 2)
                    }
                }
            })
            .getNodeByText(nodeService, "3罐装恋绿干薄荷叶茶新鲜")
    }
}