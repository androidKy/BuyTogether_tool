package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.NodeUtils
import com.utils.common.SingletonHolder
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description: 搜索商品
 * Created by Quinin on 2019-07-02.
 **/
class SearchGoodsService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : com.utils.common.SingletonHolder<SearchGoodsService, MyAccessibilityService>(::SearchGoodsService)

    private var mIsDoing = false

    /**
     * 导航栏点击搜索跳转到搜索界面
     */
    override fun doOnEvent() {
        if (mIsDoing) {
            L.i("搜索任务已经开始 ... ")
            return
        }

        //搜索按钮在导航栏显示
        NodeUtils.instance
            .setTimeOut(8)
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.apply {
                        L.i("搜索className: $className \n parentClassName: ${parent?.className}")
                        nodeService.performViewClick(nodeInfo.parent, 1, object : AfterClickedListener {
                            override fun onClicked() {
                                mIsDoing = true
                                jump2search()
                            }
                        })
                    }
                }
            })
            .getNodeByFullText(nodeService, "搜索")

        //搜索框在主页显示
        NodeUtils.instance
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    nodeInfo?.apply {
                        nodeService.performViewClick(this, 1, object : AfterClickedListener {
                            override fun onClicked() {
                                mIsDoing = true
                                searching()
                            }
                        })
                    }
                }
            })
            .getNodeById(nodeService, "com.xunmeng.pinduoduo:id/a8f")
        //.getSingleNodeByClassName(nodeService, WidgetConstant.EDITTEXT)
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
                        performViewClick(nodeInfo, 1, object : AfterClickedListener {
                            override fun onClicked() {
                                searching()
                            }
                        })
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
                        L.i(
                            "开始搜索商品：${TaskDataUtil.instance.getGoods_name()} \n" +
                                    "accountName: ${TaskDataUtil.instance.getLogin_name()}"
                        )
                        WidgetConstant.setEditText(TaskDataUtil.instance.getGoods_name(), nodeInfo)

                        findViewByFullText("搜索")?.let {
                            performViewClick(it, 2, object : AfterClickedListener {
                                override fun onClicked() {
                                    chooseGood()
                                }
                            })
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
                    L.i("选择商品：$nodeInfo")
                    nodeInfo?.let {
                        nodeService.apply {
                            performViewClick(it, 2, object : AfterClickedListener {
                                override fun onClicked() {
                                    setCurPageType(PageEnum.GOODS_INFO_PAGE)
                                    mTaskFinishedListener?.onTaskFinished()
                                }
                            })
                        }
                    }
                }
            })
            .getNodeByFullText(nodeService, "9.9")
    }


}