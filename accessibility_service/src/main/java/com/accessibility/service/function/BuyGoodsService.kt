package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseEventService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.util.*

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
class BuyGoodsService private constructor(nodeService: MyAccessibilityService) : BaseEventService(nodeService) {

    companion object : SingletonHolder<BuyGoodsService, MyAccessibilityService>(::BuyGoodsService)

    override fun doOnEvent() {
        fillAddress()
    }


    /**
     * 手动添加收货地址
     */
    private fun fillAddress() {
        GetNodeUtils.getNodeByText(nodeService, "更多支付方式", object : NodeFoundListener {
            override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                nodeInfo?.let {
                    nodeService.apply {
                        performViewClick(it, object : AfterClickedListener {  //收货地址被点击
                            override fun onClicked() {
                                //com.xunmeng.pinduoduo:id/gw 收货姓名
                                findViewById("com.xunmeng.pinduoduo:id/gw ")?.apply {
                                    WidgetConstant.setEditText("某某某", this)
                                }

                                //com.xunmeng.pinduoduo:id/gx 联系方式
                                findViewById("com.xunmeng.pinduoduo:id/gx")?.apply {
                                    WidgetConstant.setEditText("12345678", this)
                                }

                                //com.xunmeng.pinduoduo:id/h3 详细地址，街道号
                                findViewById("com.xunmeng.pinduoduo:id/h3")?.apply {
                                    WidgetConstant.setEditText("暴富街道888号", this)
                                }

                                //选择地区
                                findViewByFullText("选择地区")?.apply {
                                    performViewClick(this, object : AfterClickedListener {
                                        override fun onClicked() {
                                            val addressList = ArrayList<String>()
                                            addressList.add("山东省")
                                            addressList.add("烟台市")
                                            addressList.add("开发区")

                                            for (address in addressList) {
                                                val nodeAddress = findViewByFullText(address)
                                                if (nodeAddress == null) {
                                                    findViewByClassName(nodeService.rootInActiveWindow,
                                                        WidgetConstant.RECYCLERVIEW,
                                                        object : NodeFoundListener {
                                                            override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                                                                nodeInfo?.apply {
                                                                    ScrollUtils(nodeService, nodeInfo)
                                                                        .setForwardTotalTime(8)
                                                                        .setNodeText(address)
                                                                        .setNodeFoundListener(object :
                                                                            NodeFoundListener {
                                                                            override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                                                                                nodeInfo?.apply {
                                                                                    performViewClick(this)
                                                                                }
                                                                            }
                                                                        })
                                                                        .scrollForward()
                                                                }
                                                            }
                                                        })
                                                } else performViewClick(nodeAddress)
                                            }

                                        }
                                    })
                                }
                            }
                        })
                    }

                }
            }
        })
    }

    /**
     * 选择好购买数量
     */
    private fun chooseCount() {

    }

    /**
     * 选择支付渠道
     */
    private fun choosePayChannel() {

    }

    /**
     * 立即支付
     */
    private fun payNow() {

    }


}