package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.AdbScrollUtils
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-08-09.
 **/
class SearchGoods(val nodeService: MyAccessibilityService) : BaseAcService() {
    private var mGoodName: String? = null    //商品名称
    private var mSearchPrice: String? = null //商品在搜索显示的价格
    private var mMallName: String? = null    //商品的店铺名称
    private var mKeyWordList: ArrayList<String> = ArrayList()   //搜索商品的关键字列表
    private var mCurKeyWord: String? = null //当前正在查找的关键字

    companion object {
        const val XY_SEARCH_EDITTEXT = "540,245"    //搜索框的坐标
    }

    /**
     * 1、判断是否进入主页
     * 2、判断是"搜索"还是"分类"
     */
    override fun startService() {
        mGoodName = TaskDataUtil.instance.getGoods_name()
        mSearchPrice = TaskDataUtil.instance.getSearchPrice()
        mMallName = TaskDataUtil.instance.getMall_name()
        val keywordList = TaskDataUtil.instance.getGoodKeyWordList()

        if (mGoodName.isNullOrEmpty() || mMallName.isNullOrEmpty() || mSearchPrice.isNullOrEmpty()
        ) {
            L.i("商品信息为空，自动查找商品失败")
            responFailed("商品信息为空，自动查找商品失败")
            return
        }
        if (keywordList == null || keywordList.isEmpty()) {
            L.i("搜索商品的关键字不能为空")
            responFailed("搜索商品的关键字不能为空")
            return
        }
        for (i in 0 until keywordList.size) {
            mKeyWordList.add(keywordList[i])
        }
        L.i("查找搜索入口")
        NodeController.Builder()
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到搜索入口：搜索")
                    searchingGoods()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("找不到搜索入口：$failedText was not found.")
                    searchGoodsType()

                }
            })
            .setNodeParams("搜索", 0, 30)
            .create()
            .execute()
    }

    /**
     * 搜索节点没找到，查找分类节点
     */
    private fun searchGoodsType() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("分类", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到搜索入口：分类")
                    searchingGoods()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("找不到搜索和分类的入口")
                    dealAccident()
                }
            })
            .create()
            .execute()
    }

    /**
     * 处理找不到搜索入口的突发事件，例如登录后跳转到红包界面
     */
    private fun dealAccident() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("见面福利", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    nodeService.performBackClick(0, object : AfterClickedListener {
                        override fun onClicked() {
                            L.i("跳转到见面福利界面，返回主页")
                            startService()
                        }
                    })
                }

                override fun onTaskFailed(failedText: String) {
                    NodeController.Builder()
                        .setNodeService(nodeService)
                        .setNodeParams("直接退出", 0, 5)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                L.i("跳转到见面福利界面，弹框提示，返回主页")
                                startService()
                            }

                            override fun onTaskFailed(failedText: String) {
                                responFailed("遇到其他突发事件，找不到搜索入口")
                            }

                        })
                        .create()
                        .execute()
                }
            })
            .create()
            .execute()
    }

    /**
     * 已找到搜索入口，开始搜索，点击搜索框
     */
    private fun searchingGoods() {
        AdbScriptController.Builder()
            .setXY(XY_SEARCH_EDITTEXT)      //搜索框的坐标
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //随机选一个关键字，并记录关键字当前的index
                    inputKeyword()
                }

                override fun onTaskFailed(failedText: String) {
                    responFailed("应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 输入搜索关键字
     */
    private fun inputKeyword() {
        var keyWord: String? = null
        var index: Int = 0
        if (mKeyWordList.isNotEmpty()) {
            L.i("关键字size: ${mKeyWordList.size}")
            index = (0 until mKeyWordList.size).random()
            keyWord = mKeyWordList[index]

            mKeyWordList.remove(keyWord)
        } else {
            val keywords = TaskDataUtil.instance.getGoodKeyWord()
            L.i("搜索的关键字已用完: $keywords")
            responFailed("根据关键字搜索商品失败：$keywords")
            return
        }
        mCurKeyWord = keyWord

        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(WidgetConstant.EDITTEXT, 3, false, keyWord)
            .setNodeParams("搜索")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    //confirmGoods(goodName, searchPrice, mallName)
                    L.i("正在根据关键字[$keyWord]搜索商品")
                    startSearchByKeyWord()
                }

                override fun onTaskFailed(failedText: String) {
                    responFailed("没找到搜索按钮")
                }
            })
            .create()
            .execute()
    }

    /**
     * 开始根据关键字搜索
     */
    private fun startSearchByKeyWord() {
        AdbScrollUtils.instantce
            .setNodeService(nodeService)
            .setFindText(mSearchPrice!!)
            .setScrollTotalTime(60 * 1000)
            .setScrollSpeed(1000)
            .setStartXY("540,1700")
            .setStopXY("540,1100")
            .setTaskListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) {
                        L.i("查找关键字[$mCurKeyWord]搜索失败，重新根据另一个关键字查找")
                        retrySearch()
                    } else {
                        L.i("$mSearchPrice 已找到，判断是否是同一商铺")
                        isRightSaler(nodeInfo)
                    }
                }
            })
            .startScroll()
    }

    /**
     * 重复根据另一个关键字查找，先向下拉显示搜索框，再点击搜素框查找
     */
    private fun retrySearch() {
        AdbScriptController.Builder()
            .setSwipeXY("540,1100", "540,1600")
            .setXY("540,145")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputKeyword()
                }

                override fun onTaskFailed(failedText: String) {
                    responFailed("应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 点击进去查看是否是同一卖家
     */
    private fun isRightSaler(nodeInfo: AccessibilityNodeInfo) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(mSearchPrice!!, 0, 5)
            .setNodeParams("客服", 0, 8)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("点击 $mSearchPrice")
                    verifySaler()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                    responFailed("校验是否同一卖家失败")
                }
            })
            .create()
            .execute()
    }

    private fun verifySaler() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(mMallName!!, 0, false, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("是需要做任务的卖家,返回继续做任务")
                    nodeService.performBackClick(0, object : AfterClickedListener {
                        override fun onClicked() {
                            responSucceed()
                        }
                    })
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("不是需要做任务的卖家，退出重新查找")
                    nodeService.apply {
                        performBackClick(0, object : AfterClickedListener {
                            override fun onClicked() {
                                performBackClick(500, object : AfterClickedListener {
                                    override fun onClicked() {
                                        startSearchByKeyWord()
                                    }
                                })
                            }
                        })
                    }
                }
            })
            .create()
            .execute()

    }
}