package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.AdbScrollUtils
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.CMDUtil
import com.utils.common.ThreadUtils

/**
 * Description:下订单服务
 * Notice:
 * 1、下订单的方式：
 *      参团购买:0
 *      发起拼单:1
 *      单独购买:2
 * 2、选择规格：竖着方向找不到，需要横着找
 * 3、需要
 * Created by Quinin on 2019-08-10.
 **/
class BuyGoods(val nodeService: MyAccessibilityService) : BaseAcService(nodeService) {
    private var mChooseInfoList: List<String> = ArrayList<String>()
    private var mChoosedCount: Int = 0   //已经选择的规格数量
    private var mChooseSize: Int = 0 //选择规格的数量
    private var mIsConfrimChoosed = false //是否已选择好规格并且点击确认
    private var mCurChooseIndex: Int = 0 //当前正在选择的规格index

    override fun startService() {
        confirmBuyType()
    }

    /**
     * 确定购买方式
     */
    private fun confirmBuyType() {
        val buyType = TaskDataUtil.instance.getBuy_type()
        L.i("购买方式：$buyType")
        when (buyType) {
            0 -> buyByJoin()
            1 -> buyWithOther()
            2 -> buyBySelf()
            else -> responFailed("购买方式下发错误：$buyType")
        }
    }

    /**
     * 参团购买:
     * 立即拼单
     */
    private fun buyByJoin() {
        NodeController.Builder()
            .setNodeService(nodeService)
            // .setNodeParams("查看更多", 0, 5, true)
            //.setNodeParams("插队拼单", 0, 5, true)
            .setNodeParams("去拼单", 0, 5, true)
            .setNodeParams("参与拼单", 0, 1, true)
            .setNodeParams("参与拼单", 0, 1, true)
            .setNodeParams("抢先拼单", 0, 1, true)
            .setNodeParams("确定", 0, false, 1)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    buyByJoin2()
                }
            })
            .create()
            .execute()
    }

    private fun buyByJoin2() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("查看更多", 0, 1, true)
            .setNodeParams("插队拼单", 0, 1, true)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    NodeController.Builder()
                        .setNodeService(nodeService)
                        .setNodeParams("确定", 0, false, 5)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                chooseInfo()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                L.i("不用选择规格")
                                setPageStatus()
                            }
                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("参团购买失败，换成发起拼单")
                    buyWithOther()
                }
            })
            .create()
            .execute()
    }

    /**
     * 单独购买
     */
    private fun buyBySelf() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("单独购买")
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("没有规格可选")
                    setPageStatus()
                }
            })
            .create()
            .execute()
    }

    /**
     * 发起拼单
     */
    private fun buyWithOther() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("发起拼单")
            .setNodeParams("确定", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    //没有规格可以选
                    L.i("没有规格可选")
                    setPageStatus()
                }
            })
            .create()
            .execute()
    }

    /**
     * 选择商品参数
     */
    private fun chooseInfo() {
        try {
            mChooseInfoList = TaskDataUtil.instance.getChoose_info()!!
            L.i("商品规格参数size：${mChooseInfoList.size}")
            if (mChooseInfoList.isNullOrEmpty()) {
                responFailed("商品的选择参数不能为空")
                return
            }
            mChooseSize = mChooseInfoList.size
            mCurChooseIndex = 0
            chooseInfo(mChooseInfoList[mCurChooseIndex])
        } catch (e: Exception) {
            L.e(e.message, e)
        }
    }


    private fun chooseInfo(nodeText: String) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    //chooseInfoFailed(choose_info)
                    dealChooseFailed(failedMsg)
                }

                override fun onTaskFinished() {
                    L.i("商品规格选择成功：$nodeText")
                    mChoosedCount++
                    pressConfirm()
                }
            })
            .setNodeParams(nodeText, 0, 5)
            .create()
            .execute()
    }

    /**
     * 规格选择完成，点击确定
     */
    private fun pressConfirm() {
        L.i("已选择的规格数量：$mChoosedCount 总规格数量：$mChooseSize")
        if (mCurChooseIndex < mChooseSize - 1) {
            mCurChooseIndex++
            chooseInfo(mChooseInfoList[mCurChooseIndex])
        }
        if (mChoosedCount == mChooseSize) {
            NodeController.Builder()
                .setNodeService(nodeService)
                .setNodeParams("确定", 0, 5)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        setPageStatus()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("选择规格完成，找不到节点：$failedMsg")
                    }
                })
                .create()
                .execute()
        }
    }

    /**
     * 处理选择失败时的情况
     */
    private fun dealChooseFailed(chooseText: String) {
        AdbScrollUtils.instantce
            .setNodeService(nodeService)
            .setFindText(chooseText)
            .setScrollSpeed(1000)
            .setScrollTotalTime(8 * 1000)
            .setStartXY("1000,1000")
            .setStopXY("200,1000")
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo != null) {
                        mChoosedCount++
                        nodeService.performViewClick(nodeInfo)
                        pressConfirm()
                    } else {
                        L.i("开始竖向滑动找节点:$chooseText")
                        chooseByVertical(chooseText)
                    }
                }
            })
            .startScroll()
    }

    /**
     * 竖向查找节点
     */
    private fun chooseByVertical(chooseText: String) {
        AdbScrollUtils.instantce
            .setNodeService(nodeService)
            .setFindText(chooseText)
            .setScrollSpeed(1000)
            .setScrollTotalTime(8 * 1000)
            .setStartXY("540,1500")
            .setStopXY("540,800")
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo != null) {
                        mChoosedCount++
                        nodeService.performViewClick(nodeInfo)
                        pressConfirm()
                    } else {
                        L.i("找不到规格节点:$chooseText")
                        responFailed("找不到规格节点:$chooseText")
                    }
                }
            })
            .startScroll()
    }


    /**
     * 设置界面处于正在支付界面的状态
     */
    private fun setPageStatus() {
        if (!mIsConfrimChoosed) {
            mIsConfrimChoosed = true
            nodeService.setCurPageType(PageEnum.PAYING_PAGE)
            nodeService.postDelay(Runnable {
                chooseAddress()
            }, 5)
        }
    }

    /**
     * 选择收货人的地址
     */
    private fun chooseAddress() {
        FillAddressService(nodeService)
            .setTaskFinishedListener(object : TaskListener {
                override fun onTaskFinished() {
                    //开始支付
//                    payForNow()
                    // todo 应该先查找有无 “更多支付方式”
                    hasPayMoreWay()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .doOnEvent()
    }

    private fun hasPayMoreWay() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("更多支付方式", 1, true, 5, true)
            .setNodeParams("支付宝", 1, true, 5, true)
            .setNodeParams("立即支付", 0, true, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    payByAlipay()
                }

                override fun onTaskFailed(failedMsg: String) {
                    // todo
                    L.i("支付环节有BUG，待修复")
                }

            })
            .create()
            .execute()
    }

    private fun payForNow() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("立即支付")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    payByAlipay()
                    L.i("点击立即支付")
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("节点找不到：$failedMsg")
                    responFailed("支付页面跳转失败")
                }
            })
            .create()
            .execute()
    }

    /**
     * 支付宝支付
     */
    private fun payByAlipay() {
        AliPayLogin(nodeService)
            .login(object : TaskListener {
                override fun onTaskFinished() {
                    //closeAliPay()
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
    }

    /**
     *  关闭支付宝
     */
    private fun closeAliPay() {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                val closeAlipayCMD = "am force-stop ${Constant.ALI_PAY_PKG};"
                CMDUtil().execCmd(closeAlipayCMD)

                return true
            }

            override fun onSuccess(result: Boolean?) {
                L.i("支付成功后，成功关闭支付宝")
            }

            override fun onCancel() {
                L.i("关闭支付宝中断")
            }

            override fun onFail(t: Throwable?) {
                L.i("没有关闭支付宝")
            }

        })
    }

}