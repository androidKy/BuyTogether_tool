package com.accessibility.service

import android.content.Context
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.LoginService
import com.accessibility.service.function.TaskService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.TaskDataUtil
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {

    private var mScreenWidth: Int = 1080
    private var mScreenHeight: Int = 1920


    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
        var mTaskListener: TaskListener? = null

        fun setTaskListener(taskListener: TaskListener) {
            mTaskListener = taskListener
        }

    }

    override fun onInterrupt() {

    }

    override fun onCreate() {
        super.onCreate()
        L.i("MyAccessibilityService onCreate()")
        mScreenWidth = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_width", 0)
        mScreenHeight = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_height", 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("MyAccessibilityService onDestroy()")
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

            try {
                chooseLogin()
            } catch (e: Exception) {
                L.e(e.message)
            }
        }
    }

    /**
     * 选择登录
     */
    private fun chooseLogin() {
        if (mCurPageType == PageEnum.START_PAGE && !mIsInited) {
            initTaskData()
            L.i("拼多多登录界面")
            setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)

            /*   OcrUtils.recognizePic(
                   File("/storage/emulated/0/Android/data/screenShot.png"),
                   this.applicationContext
               )

               return
   */

            NodeController.Builder()
                .setNodeService(this@MyAccessibilityService)
                .setNodeParams("好的", true)
                .setNodeParams("允许", true)
                .setNodeParams("个人中心", 0, 5, true)
                .setNodeParams("点击登录", 0, 5, true)
                .setNodeParams("请使用其它方式登录")
                .setNodeParams("QQ登录")
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("判断是跳转到主页还是登录界面")
                        LoginService(this@MyAccessibilityService).login(LoginListenerImpl())
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed("拼多多登录授权失败")
                    }
                })
                .create()
                .execute()
        }
    }

    inner class LoginListenerImpl : TaskListener {
        override fun onTaskFinished() {
            //登录完成后，有时候会自动跳转到见面福利的界面
            back2main()
            searchGoods()
        }

        override fun onTaskFailed(failedText: String) {
            responTaskFailed(failedText)
        }
    }

    private fun back2main() {
        NodeController.Builder()
            .setNodeService(this)
            .setNodeParams("见面福利", 0, false, 20)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    performBackClick()
                }

                override fun onTaskFailed(failedText: String) {
                    L.d("没有跳转到见面福利界面")
                    NodeController.Builder()
                        .setNodeService(this@MyAccessibilityService)
                        .setNodeParams("直接退出", 0, 5)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {

                            }

                            override fun onTaskFailed(failedText: String) {

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
     * 搜索商品
     */
    private fun searchGoods() {
        L.i("mIsLogined : $mIsLogined")

        val goodName = TaskDataUtil.instance.getGoods_name()
        val searchPrice = TaskDataUtil.instance.getSearchPrice()
        val mallName = TaskDataUtil.instance.getMall_name()

        if (goodName.isNullOrEmpty() || mallName.isNullOrEmpty() || searchPrice.isNullOrEmpty()
        ) {
            L.i("商品信息为空，自动查找商品失败")
            responTaskFailed("商品信息为空，自动查找商品失败")
            return
        }

        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("开始确认商品")
                    clickSearchEditText(goodName, searchPrice, mallName)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                    //responTaskFailed("搜索商品失败")
                    searchType(goodName, searchPrice, mallName)

                }
            })
            .setNodeParams("搜索", 0, 30)
            .create()
            .execute()


        /*
           .setNodeParams("com.xunmeng.pinduoduo:id/a8f", 2, 3, true)
           .setNodeParams("com.xunmeng.pinduoduo:id/fq", 2, 3)
           .setNodeParams(WidgetConstant.EDITTEXT, 3, false, keyWord)
           .setNodeParams("搜索")
           .create()
           .execute()*/

    }

    private fun searchType(goodName: String, searchPrice: String, mallName: String) {
        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("开始确认商品")
                    clickSearchEditText(goodName, searchPrice, mallName)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                    responTaskFailed("搜索商品失败")
                }
            })
            .setNodeParams("分类", 0, 10)
            .create()
            .execute()
    }

    /**
     * 点击搜索框
     */
    private fun clickSearchEditText(goodName: String, searchPrice: String, mallName: String) {
        AdbScriptController.Builder()
            .setXY("540,245")      //搜索框的坐标
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    val keyWord = TaskDataUtil.instance.getGoods_keyword()
                    if (keyWord.isNullOrEmpty()) {
                        responTaskFailed("搜索关键词不能为空")
                        return
                    }
                    NodeController.Builder()
                        .setNodeService(this@MyAccessibilityService)
                        .setNodeParams(WidgetConstant.EDITTEXT, 3, false, keyWord)
                        .setNodeParams("搜索")
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                confirmGoods(goodName, searchPrice, mallName)
                            }

                            override fun onTaskFailed(failedText: String) {
                                responTaskFailed("商品搜索失败")
                            }
                        })
                        .create()
                        .execute()
                }

                override fun onTaskFailed(failedText: String) {
                    responTaskFailed("应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 确认商品是需要刷的商品
     */
    private fun confirmGoods(goodName: String, searchPrice: String, mallName: String) {
        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("价格一致,继续对比店铺的名字是否一样")
                    confirmMallName(goodName, searchPrice, mallName)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText not found.关键词查找失败")
                    responTaskFailed("根据关键字搜索商品失败")
                    //lookforwardGood(goodName, searchPrice, mallName)
                }
            })
            .setNodeParams(
                searchPrice, 0, true,
                isScrolled = true,
                editInputText = "null",
                foundNodeTimeOut = 2,
                findNextFlag = false
            )
            .create()
            .execute()
    }


    /**
     * 确认店铺名称
     */
    private fun confirmMallName(goodName: String, searchPrice: String, mallName: String) {
        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("店铺名称相同，找到需要刷的商品")
                    performBackClick(1, object : AfterClickedListener {
                        override fun onClicked() {
                            doTask()
                        }
                    })
                }

                override fun onTaskFailed(failedText: String) {
                    performBackClick(0)
                    performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            //返回继续查找
                            L.i("店铺名称不一样：$failedText was not found.返回继续查找")
                            continueLookGood(goodName, searchPrice, mallName)
                        }
                    })
                }
            })
            .setNodeParams("客服")
            .setNodeParams(mallName, 0, false, 5)
            .create()
            .execute()
    }

    private fun continueLookGood(goodName: String, searchPrice: String, mallName: String) {
        AdbScriptController.Builder()
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    confirmGoods(goodName, searchPrice, mallName)
                }

                override fun onTaskFailed(failedText: String) {
                }
            })
            .setSwipeXY(
                "${mScreenWidth / 2},${mScreenHeight * 0.8}",
                "${mScreenWidth / 2},${mScreenHeight * 0.3}"
            )
            .create()
            .execute()
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        TaskService.getInstance(this)
            .setScreenDensity(mScreenWidth, mScreenHeight)
            .setTaskFinishedListener(object : TaskListener {
                override fun onTaskFinished() {
                    //uploadOrderInfo()
                    mHandler.postDelayed({
                        responTaskFinished()
                    }, 20 * 1000)
                }

                override fun onTaskFailed(failedText: String) {
                    //mTaskListener?.onTaskFailed(failedText)
                    responTaskFailed(failedText)
                }
            })
            .doOnEvent()
    }


    private fun responTaskFinished() {
        L.i("任务完成，重新开始下一轮任务")
        initParams()
        mTaskListener?.onTaskFinished()
    }

    private fun responTaskFailed(msg: String) {
        initParams()
        mTaskListener?.onTaskFailed(msg)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
        mIsInited = false
        mIsLogined = false
    }

}