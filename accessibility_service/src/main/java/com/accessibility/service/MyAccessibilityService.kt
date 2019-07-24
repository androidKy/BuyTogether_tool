package com.accessibility.service

import android.content.Context
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
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
            chooseLogin()
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
            NodeController.Builder()
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("判断是跳转到主页还是登录界面")
                        loginByQQ()
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed(failedText)
                    }
                })
                .setNodeService(this@MyAccessibilityService)
                .setNodeParams("好的", true)
                .setNodeParams("允许", true)
                .setNodeParams("个人中心")
                .setNodeParams("点击登录")
                .setNodeParams("请使用其它方式登录")
                .setNodeParams("QQ登录")
                .create()
                .execute()
        }
    }

    /**
     * QQ输入账号和密码授权登录
     */
    private fun loginByQQ() {
        if (mCurPageType == PageEnum.CHOOSING_LOGIN_PAGE) {
            setCurPageType(PageEnum.INDEX_PAGE)

            val login_name = TaskDataUtil.instance.getLogin_name()
            L.i("login_name: $login_name")

            /* NodeController.Builder()
                 .setNodeService(this)
                 .setTaskListener(object : TaskListener {
                     override fun onTaskFinished() {
                         L.i("登录成功")
                         setIsLogined(true)
                         searchGoods()
                     }

                     override fun onTaskFailed(failedText: String) {
                         L.i("$failedText was not found.")
                         responTaskFailed(failedText)
                     }
                 })
                 //.setNodeParams("允许")
                 //.setNodeParams("允许", 0, 5)
                 //.setNodeParams("同意", 0, 5)
                 .setNodeParams("登录", 0, 5)
                 //.setNodeFilter("首页")
                 .setNodeParams("QQ号/手机号/邮箱", 0, false, TaskDataUtil.instance.getLogin_name()!!)
                 .setNodeParams("com.tencent.mobileqq:id/password", 2, false, TaskDataUtil.instance.getLogin_psw()!!)
                 .setNodeParams("com.tencent.mobileqq:id/login", 2)
                 .setNodeParams("登录", 0, 30)   //授权登录
                 .create()
                 .execute()*/
            NodeController.Builder()
                .setNodeService(this)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("已跳转到QQ登录界面")
                        AdbScriptController.Builder()
                            .setTaskListener(object : TaskListener {
                                override fun onTaskFinished() {
                                    checkLoginResult()
                                }

                                override fun onTaskFailed(failedText: String) {
                                    L.i("$failedText was not found.")
                                    responTaskFailed(failedText)
                                }
                            })
                            //.setXY("777,1600", 10 * 1000L)
                            //.setXY("540,310")
                            .setXY("540,320")   //账号输入框
                            .setText(TaskDataUtil.instance.getLogin_name()!!)
                            .setXY("540,450")   //密码输入框
                            .setText(TaskDataUtil.instance.getLogin_psw()!!)
                            .setXY("540,680")  //登录按钮
                            .create()
                            .execute()
                    }

                    override fun onTaskFailed(failedText: String) {
                    }

                })
                .setNodeParams("TIM登录")
                .create()
                .execute()
        }
    }

    /**
     * 检查登录结果
     */
    private fun checkLoginResult() {
        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
                    loginByQQ() //todo 登录失败次数限制
                }

                override fun onTaskFailed(failedText: String) {
                    NodeController.Builder()
                        .setNodeService(this@MyAccessibilityService)
                        .setTaskListener(object : TaskListener {
                            override fun onTaskFinished() {
                                L.i("登录成功")
                                setIsLogined(true)

                                mHandler.postDelayed({
                                    searchGoods()
                                }, 3000)
                            }

                            override fun onTaskFailed(failedText: String) {
                                setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
                                loginByQQ() //todo 登录失败次数限制
                            }
                        })
                        .setNodeParams("授权并登录", 0, 2)
                        .create()
                        .execute()
                }

            })
            .setNodeParams("登录失败", 0, 2)
            .create()
            .execute()


    }

    /**
     * 搜索商品
     */
    private fun searchGoods() {
        if (mCurPageType == PageEnum.INDEX_PAGE) {
            L.i("mIsLogined : $mIsLogined")
            setCurPageType(PageEnum.GOODS_INFO_PAGE)

            val goodName = TaskDataUtil.instance.getGoods_name()
            val keyWord = TaskDataUtil.instance.getGoods_keyword()
            val searchPrice = TaskDataUtil.instance.getSearchPrice()
            val mallName = TaskDataUtil.instance.getMall_name()

            if (goodName.isNullOrEmpty() || mallName.isNullOrEmpty() ||
                keyWord.isNullOrEmpty() || searchPrice.isNullOrEmpty()
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
                        confirmGoods(goodName, searchPrice, mallName)
                    }

                    override fun onTaskFailed(failedText: String) {
                        L.i("$failedText was not found.")
                        responTaskFailed(failedText)
                    }
                })
                .setNodeParams("搜索", false)
                .setNodeParams("com.xunmeng.pinduoduo:id/a8f", 2, 3, true)
                .setNodeParams("com.xunmeng.pinduoduo:id/fq", 2, 3)
                .setNodeParams(WidgetConstant.EDITTEXT, 3, false, keyWord)
                .setNodeParams("搜索")
                .create()
                .execute()
        }
    }

    /**
     * 确认商品是需要刷的商品
     */
    private fun confirmGoods(goodName: String, searchPrice: String, mallName: String) {
        /* mHandler.postDelayed({
             val nodeList = findViewsByFullText(goodName)
             nodeList?.run {
                 if (size > 0) {

                 }
             }
         }, 1000)*/

        /* val screenWidth = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_width", 0)
         val screenHeight = getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_height", 0)
         AdbScriptController.Builder()
             .setSwipeXY("${screenWidth / 2},${screenHeight * 0.7}", "${screenWidth / 2},${screenHeight * 0.6}")
             .create()
             .execute()*/

        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("价格一致,继续对比店铺的名字是否一样")
                    confirmMallName(goodName, searchPrice, mallName)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText not found.关键词查找失败")
                    responTaskFailed(failedText)
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
     * 确认商品名称是否一样
     */
    /* private fun confirmGoodName(goodName: String, searchPrice: String, mallName: String) {
         NodeController.Builder()
             .setNodeService(this)
             .setTaskListener(object : TaskListener {
                 override fun onTaskFinished() {
                     confirmMallName(goodName, searchPrice, mallName)
                 }

                 override fun onTaskFailed(failedText: String) {
                     //商品名称不一样,返回向下滑动继续查找
                     L.i("$failedText was not found.商品名称不一样，返回继续查找")
                     performBackClick()

                     continueLookGood(goodName, searchPrice, mallName)
                 }
             })
             .setNodeParams(goodName, 0, false, 2)
             .create()
             .execute()
     }*/

    /**
     * 确认店铺名称
     */
    private fun confirmMallName(goodName: String, searchPrice: String, mallName: String) {
        NodeController.Builder()
            .setNodeService(this)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("店铺名称相同，找到需要刷的商品")
                    performBackClick()
                    doTask()
                    //mTaskListener?.onTaskFinished()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("店铺名称不一样：$failedText was not found.")
                    performBackClick(0)
                    performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            //返回继续查找
                            continueLookGood(goodName, searchPrice, mallName)
                        }
                    })
                }

            })
            .setNodeParams("客服")
            .setNodeParams(mallName, 0, false, 10)
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
                "${mScreenWidth / 2},${mScreenHeight * 0.2}"
            )
            .create()
            .execute()
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        if (mCurPageType == PageEnum.GOODS_INFO_PAGE) {
            setCurPageType(PageEnum.PAYING_PAGE)

            TaskService.getInstance(this)
                .setScreenDensity(mScreenWidth, mScreenHeight)
                .setTaskFinishedListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("任务完成，重新开始下一轮任务")
                        initParams()
                        mTaskListener?.onTaskFinished()
                    }

                    override fun onTaskFailed(failedText: String) {
                        mTaskListener?.onTaskFailed(failedText)
                    }
                })
                .doOnEvent()
        }
    }

    private fun responTaskFailed(msg: String) {
        setCurPageType(PageEnum.START_PAGE)
        mTaskListener?.onTaskFailed(msg)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
    }

}