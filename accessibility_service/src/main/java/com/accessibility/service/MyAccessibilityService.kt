package com.accessibility.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.function.*
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
import com.utils.common.PackageManagerUtils
import com.utils.common.SPUtils

/**
 * Description:无障碍服务最上层
 * Created by Quinin on 2019-07-02.
 **/
class MyAccessibilityService : BaseAccessibilityService() {

    private var mScreenWidth: Int = 1080
    private var mScreenHeight: Int = 1920

    private var testFlag: Boolean = false
    private var mTaskStatusReceiver: BroadcastReceiver? = null


    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
        const val ACTION_TASK_STATUS: String = "com.task.status"
        const val ACTION_CONTINUE_TASK: String = "com.task.continue"
        const val ACTION_DEAD_SERVICE: String = "com.service.dead"

        var mTaskListener: TaskListener? = null

        fun setTaskListener(taskListener: TaskListener) {
            mTaskListener = taskListener
        }

    }

    override fun onInterrupt() {

    }

    inner class TaskStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                if (action == ACTION_TASK_STATUS) {
                    initParams()
                } else if (action == ACTION_CONTINUE_TASK) {
                    afterLoginSucceed()
                } else if (action == ACTION_DEAD_SERVICE) {
                    //doTask()
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        L.i("MyAccessibilityService onCreate()")
        mScreenWidth =
            getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_width", 0)
        mScreenHeight =
            getSharedPreferences("spUtils", Context.MODE_PRIVATE).getInt("key_screen_height", 0)


        mTaskStatusReceiver = TaskStatusReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_TASK_STATUS)
        registerReceiver(mTaskStatusReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("MyAccessibilityService onDestroy()")
        mTaskStatusReceiver?.apply {
            unregisterReceiver(this)
        }
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
                confirmPayResult()
//                test()
            } catch (e: Exception) {
                L.e(e.message)
            }
        }
    }

    private fun test() {
        if(!testFlag){
            NodeController.Builder()
                .setNodeService(this)
                .setNodeParams("快如闪电",0,10,true)
                .setTaskListener(object :TaskListener{
                    override fun onTaskFinished() {
                        L.i("？？？")
                    }

                    override fun onTaskFailed(failedMsg: String) {
                    }

                })
                .create()
                .execute()
        }

    }

    private fun confirmPayResult() {
        if (mCurPageType == PageEnum.PAY_CONFIRM_PAGE) {
            setCurPageType(PageEnum.PAY_SUCCEED)
            ConfirmPayResult(this)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        responTaskFinished()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed(failedMsg)
                    }
                })
                .startService()
        }
    }

    /**
     * 正在支付界面时，选择支付宝方式
     */
    private fun paying() {
        if (mCurPageType == PageEnum.PAYING_PAGE) {
            L.i("正在支付")
            val morePayChannel = findViewByText("更多支付方式")
            if (morePayChannel != null) {
                performViewClick(morePayChannel, object : AfterClickedListener {
                    override fun onClicked() {
                        L.i("更多支付方式节点被点击")
                        performViewClick(findViewByText("支付宝"), 0, object : AfterClickedListener {
                            override fun onClicked() {
                                L.i("支付宝方式被点击")
                                setCurPageType(PageEnum.PAY_SUCCEED)
                            }
                        })
                    }
                })
            }
            val alipayNode = findViewByText("支付宝")
            if (alipayNode != null) {
                performViewClick(alipayNode, object : AfterClickedListener {
                    override fun onClicked() {
                        L.i("支付宝节点被点击")
                        setCurPageType(PageEnum.PAY_SUCCEED)
                    }
                })
            }
        }
    }


    /**
     * 选择登录
     */
    private fun chooseLogin() {
        if (mCurPageType == PageEnum.START_PAGE) {
            setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
            initTaskData()
            L.i("拼多多登录界面")
            /*   OcrUtils.recognizePic(
                   File("/storage/emulated/0/Android/data/screenShot.png"),
                   this.applicationContext
               )
               return
   */
            //确保同一个账号不会重复下单，应该在登陆前面执行
            val orderNumber = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                .getString(Constant.KEY_ORDER_NUMBER)
            L.i("拼多多订单号：$orderNumber")
            if (!TextUtils.isEmpty(orderNumber)) {
                setCurPageType(PageEnum.PAY_CONFIRM_PAGE)
                confirmPayResult()
                return
            }

            val isLogined =
                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_IS_LOGINED)
            L.i("是否已登录：$isLogined")
            if (isLogined)   //已经登录成功
            {
                NodeController.Builder()
                    .setNodeService(this)
                    .setNodeParams("允许", 0, true, 18)
                    .create()
                    .execute()
                afterLoginSucceed()     //任务失败，重新进来，不再重新登录
                return
            }


            NodeController.Builder()
                .setNodeService(this@MyAccessibilityService)
                .setNodeParams("好的", 0, 3, true)
                .setNodeParams("允许", 0, 3, true)
                .setNodeParams("请使用其它方式登录", 0, 2)
                .setNodeParams("QQ登录", 0, 2)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("判断是跳转到主页还是登录界面")
                        LoginService(this@MyAccessibilityService).login(LoginListenerImpl())
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("$failedMsg was not found.")
                        //responTaskFailed("拼多多登录授权失败")
                        enterLoginFailed()
                    }
                })
                .create()
                .execute()
        }
    }

    private fun enterLoginFailed() {
        NodeController.Builder()
            .setNodeService(this@MyAccessibilityService)
            .setNodeParams("个人中心", 0, 3, true)
            .setNodeParams("点击登录", 0, 3, true)
            .setNodeParams("请使用其它方式登录")
            .setNodeParams("QQ登录")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("判断是跳转到主页还是登录界面")
                    LoginService(this@MyAccessibilityService).login(LoginListenerImpl())
                }

                override fun onTaskFailed(failedMsg: String) {
                   // 有可能弹出见面福利
                    closeBenefit()
                }
            })
            .create()
            .execute()
    }

    private fun closeBenefit() {
        NodeController.Builder()
            .setNodeService(this)
            .setNodeParams("见面福利",0,false,10)
            .setTaskListener(object :TaskListener{
                override fun onTaskFinished() {
                   performBackClick(3,object :AfterClickedListener{
                       override fun onClicked() {
                           enterLoginFailed()
                       }

                   })
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed("找不到见面福利，并且登录失败")
                }

            })
            .create()
            .execute()

    }

    inner class LoginListenerImpl : TaskListener {
        override fun onTaskFinished() {
            //登录完成后，判断是评论任务还是正常任务
            afterLoginSucceed()
        }

        override fun onTaskFailed(failedMsg: String) {
            responTaskFailed(failedMsg)
        }
    }

    private fun afterLoginSucceed() {

        if (!TaskDataUtil.instance.isCommentTask()!!) {
            L.i("开始自动执行正常任务")
            SearchGoods(this@MyAccessibilityService)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        doTask()
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed(failedMsg)
                    }
                })
                .startService()
        } else {
            L.i("开始自动执行评论任务")
            CommentTaskService(this@MyAccessibilityService)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        mHandler.postDelayed({
                            responTaskFinished()
                        }, 3 * 1000)
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responTaskFailed(failedMsg)
                    }
                })
                .startService()
        }
    }

    /**
     * 开始做任务
     */
    private fun doTask() {
        TaskService(this)
            .setScreenDensity(mScreenWidth, mScreenHeight)
            .setTaskFinishedListener(object : TaskListener {
                override fun onTaskFinished() {
                    //uploadOrderInfo()
                    /* mHandler.postDelayed({
                         responTaskFinished()
                     }, 8 * 1000)*/
                    verifyPaySucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    //mTaskListener?.onTaskFailed(failedText)
                    responTaskFailed(failedMsg)
                }
            })
            .doOnEvent()
    }

    /**
     * 验证是否支付成功
     */
    private fun verifyPaySucceed() {
        L.i("重启PDD，验证是否支付成功")
        setCurPageType(PageEnum.PAY_CONFIRM_PAGE)
        PackageManagerUtils.getInstance()
            .restartApplication(
                PKG_PINDUODUO,
                "${PKG_PINDUODUO}.ui.activity.MainFrameActivity"
            )
    }


    private fun responTaskFinished() {
        L.i("任务完成，重新开始下一轮任务")
        //initParams()
        mTaskListener?.onTaskFinished()
    }

    private fun responTaskFailed(msg: String) {
        //initParams()
        mTaskListener?.onTaskFailed(msg)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
        mIsLogined = false
        TaskDataUtil.instance.clearData()
    }

}