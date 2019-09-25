package com.accessibility.service.function

import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.SearchType
import com.accessibility.service.util.AdbScrollUtils
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-08-09.
 **/
class SearchGoods(val nodeService: MyAccessibilityService) : BaseAcService(nodeService) {
    private var mGoodName: String? = null    //商品名称
    private var mSearchPrice: String? = null //商品在搜索显示的价格
    private var mMallName: String? = null    //商品的店铺名称
    private var mKeyWordList: ArrayList<String> = ArrayList()   //搜索商品的关键字列表
    private var mCurKeyWord: String? = null //当前正在查找的关键字
    private var mIsVerifySaler: Boolean = false //是否校验过同一卖家，如果是，则先上滑动再继续查找，否则不先上滑
    private var mStartTime: Long = 0 //搜索开始时间

    companion object {
        const val XY_SEARCH_EDITTEXT = "540,245"    //主界面的搜索框的坐标
        const val XY_SEARCH_RESULT_EDITTEXT = "540,145" //搜索界面的搜索框的坐标
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
            .setNodeParams("搜索", 0, 15)
            .setNodeService(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("找到搜索入口：搜索")
                    searchingGoods()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("找不到搜索入口：$failedMsg was not found.")
                    searchGoodsType()

                }
            })
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

                override fun onTaskFailed(failedMsg: String) {
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
        nodeService.performBackClick(2, object : AfterClickedListener {
            override fun onClicked() {
                L.i("跳转到见面福利界面，返回主页")
                startService()
            }
        })
        /* NodeController.Builder()
             .setNodeService(nodeService)
             .setNodeParams("见面福利", 0, false, 5)
             .setTaskListener(object : TaskListener {
                 override fun onTaskFinished() {

                 }

                 override fun onTaskFailed(failedMsg: String) {
                     NodeController.Builder()
                         .setNodeService(nodeService)
                         .setNodeParams("直接退出", 0, 5)
                         .setTaskListener(object : TaskListener {
                             override fun onTaskFinished() {
                                 L.i("跳转到见面福利界面，弹框提示，返回主页")
                                 startService()
                             }

                             override fun onTaskFailed(failedMsg: String) {
                                 responFailed("遇到其他突发事件，找不到搜索入口")
                             }

                         })
                         .create()
                         .execute()
                 }
             })
             .create()
             .execute()*/
    }

    /**
     * 已找到搜索入口，开始搜索，点击搜索框
     */
    private fun searchingGoods() {
        AdbScriptController.Builder()
            .setXY(XY_SEARCH_EDITTEXT)      //搜索框的坐标
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    val taskFinishCount = TaskDataUtil.instance.getTaskFinishedCount()
                    var searchType =
                        SPUtils.getInstance(Constant.SP_SEARCH_TYPE_FILE)
                            .getInt(Constant.KEY_CUR_SEARCH_TYPE)
                    L.i("搜索方式：$searchType 任务完成的数量：$taskFinishCount")
                    nodeService.postDelay(Runnable {
                        inputKeyword()
                    },1)
                    //searchByBrowser()
                    /*if (taskFinishCount != null && taskFinishCount > 0 && searchType > 0) {
                        if (searchType == SearchType.MALLNAME) {
                            searchByMallName()
                        } else if (searchType == SearchType.BROWSER) {
                            searchByBrowser()
                        }
                    } else {
                        inputKeyword()
                    }*/
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
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
        } else {     //根据关键字搜索失败，搜索店铺然后搜索商品
            val keywords = TaskDataUtil.instance.getGoodKeyWord()
            L.i("搜索的关键字已用完: $keywords")
            searchByMallName()
            return
        }
        mCurKeyWord = keyWord

        AdbScriptController.Builder()
            .setXY(XY_SEARCH_RESULT_EDITTEXT)
            .setText(keyWord)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    NodeController.Builder()
                        .setNodeService(nodeService)
                        //.setNodeParams(WidgetConstant.EDITTEXT, 3, false, keyWord)
                        .setNodeParams("搜索", 0, 3)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                //confirmGoods(goodName, searchPrice, mallName)
                                L.i("正在根据关键字[$keyWord]搜索商品")
                                dealSecureCheck()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                responFailed("没找到搜索按钮")
                            }
                        })
                        .create()
                        .execute()

                }

                override fun onTaskFailed(failedMsg: String) {

                }
            })
            .create()
            .execute()
    }


    /**
     * 处理安全验证的问题
     */
    private fun dealSecureCheck() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("销量", 1, false, 8)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    mStartTime = System.currentTimeMillis()
                    L.i("搜索开始的时间: $mStartTime")
                    startSearchByKeyWord()

                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("搜索有安全验证,直接跳浏览器")
                    //PackageManagerUtils.getInstance().startXiaomiBrowser()
                    searchByBrowser()
                }
            })
            .create()
            .execute()
    }

    /**
     * 关键字搜索不到的情况下
     */
    private fun searchByMallName() {
        SearchByMallName(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("根据店铺找到商品")
                    SPUtils.getInstance(Constant.SP_SEARCH_TYPE_FILE)
                        .put(Constant.KEY_CUR_SEARCH_TYPE, SearchType.MALLNAME)
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("根据店铺找商品失败:$failedMsg")
                    //打开浏览器，输入链接打开商品，然后跳转到拼多多
                    searchByBrowser()
                }
            })
            .startService()
    }

    /**
     * 浏览器根据链接跳转
     */
    private fun searchByBrowser() {
        SearchByBrowser(nodeService)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("浏览器根据链接跳转成功")
                    SPUtils.getInstance(Constant.SP_SEARCH_TYPE_FILE)
                        .put(Constant.KEY_CUR_SEARCH_TYPE, SearchType.BROWSER)
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("浏览器根据链接跳转失败：$failedMsg")
                    responFailed(failedMsg)
                }
            })
            .startService()
    }

    /**
     * 先判断是否校验过卖家
     */
    private fun startSearchByKeyWord() {
        val currentSearchTime = System.currentTimeMillis()
        val searchIntervalTime = currentSearchTime - mStartTime
        if (searchIntervalTime / 1000 > 30)   //搜索时间超过半分钟,每个关键字搜索时间为半分钟
        {
            //mStartTime = currentSearchTime
            //inputKeyword()
            retrySearch()
            return
        }

        if (mIsVerifySaler) {
            AdbScriptController.Builder()
                .setSwipeXY("540,1700", "540,1100")
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        mIsVerifySaler = false
                        startSearch()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responFailed("搜索商品：应用未获得root权限")
                    }
                })
                .create()
                .execute()
        } else startSearch()

    }

    /**
     * 开始查找
     */
    private fun startSearch() {
        var mHalfGoodName = ""
        mGoodName?.apply {
            val length = this.length
            mHalfGoodName = this.substring(0,length-5)
        }

        val searchTime = (8..18).random().toLong()
        AdbScrollUtils.instantce
            .setNodeService(nodeService)
            .setFindText(mSearchPrice!!)
            .setScrollTotalTime(searchTime * 1000)
            .setScrollSpeed(1000)
            .setStartXY("540,1700")
            .setStopXY("540,1100")
            .setNodeFoundListener(object : NodeFoundListener {
                override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) {
                        L.i("查找关键字[$mCurKeyWord]搜索失败，重新根据另一个关键字查找")
                        retrySearch()
                    } else {
                        L.i("$mSearchPrice 已找到，判断是否是同一商铺")
                        isRightSaler(mSearchPrice!!)
                    }
                }
            })
            .startSearchGood()
    }

    /**
     * 重复根据另一个关键字查找，先向下拉显示搜索框，再点击搜素框查找
     */
    private fun retrySearch() {
        AdbScriptController.Builder()
            .setSwipeXY("540,1100", "540,1600")
            .setXY(XY_SEARCH_RESULT_EDITTEXT)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    inputKeyword()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 点击进去查看是否是同一卖家
     */
    private fun isRightSaler(searchPrice:String) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(searchPrice, 1, 6)
            .setNodeParams("客服", 0, 8)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("点击 $mSearchPrice")
                    verifySaler()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    //responFailed("校验是否同一卖家失败")
                    retrySearch()
                }
            })
            .create()
            .execute()
    }

    private fun verifySaler() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(mMallName!!, 1, false, 3)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("是需要做任务的卖家,返回继续做任务")
                    nodeService.performBackClick(1, object : AfterClickedListener {
                        override fun onClicked() {
                            //保存当前搜索的方式
                            verifyGoodName()
//                            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
//                                .put(Constant.KEY_CUR_SEARCH_TYPE, SearchType.KEYWORD)
//                            responSucceed()
                        }
                    })
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("不是需要做任务的卖家，退出重新查找")
                    nodeService.apply {
                        performBackClick(0, object : AfterClickedListener {
                            override fun onClicked() {
                                performBackClick(1, object : AfterClickedListener {
                                    override fun onClicked() {
                                        mIsVerifySaler = true
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

    private fun verifyGoodName() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(mGoodName!!, 1, false, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    SPUtils.getInstance(Constant.SP_SEARCH_TYPE_FILE)
                        .put(Constant.KEY_CUR_SEARCH_TYPE, SearchType.KEYWORD)
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    nodeService.apply {
                        performBackClick(1, object : AfterClickedListener {
                            override fun onClicked() {
                                mIsVerifySaler = true
                                startSearchByKeyWord()
                            }

                        })
                    }
                }

            })
            .create()
            .execute()

    }
}