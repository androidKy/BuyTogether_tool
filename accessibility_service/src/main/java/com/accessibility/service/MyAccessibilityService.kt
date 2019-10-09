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
import com.accessibility.service.data.TaskCategory
import com.accessibility.service.function.*
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.PackageManagerUtils
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L
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

    private var deadLoop: Int = 0


    companion object {
        const val PKG_PINDUODUO = "com.xunmeng.pinduoduo"
        const val PKG_QQ = "com.tencent.mobileqq"
        const val ACTION_TASK_STATUS: String = "com.task.status"
        const val ACTIVITY_PDD_LAUNCHER = "com.xunmeng.pinduoduo.ui.activity.MainFrameActivity"
        const val ACTIVITY_TASK_LAUNCHER = "com.buy.together.MainActivity"

        const val ACTION_EXCEPTION_RESTART = "action_exception_restart" //任务APP发生异常，重新启动任务APP
        const val ACTION_TASK_RESTART = "com.task.restart"      //发生未知错误，任务重新开始，重新请求代理和读取缓存的任务
        const val ACTION_APP_RESTART = "com.pdd.restart"        //拼多多APP重新启动
        const val ACTION_TASK_FAILED = "com.task.failed" //任务失败更新任务状态
        const val ACTION_TASK_SUCCEED = "com.task.succeed"  //任务成功更新任务状态
        const val KEY_TASK_MSG = "key__task_msg" //任务更新的备注

        const val ACTION_BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED
        /* var mTaskListener: TaskListener? = null

         fun setTaskListener(taskListener: TaskListener) {
             mTaskListener = taskListener
         }
 */
    }

    override fun onInterrupt() {

    }

    inner class TaskStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.apply {
                if (this == ACTION_TASK_STATUS) {
                    L.i("接收到任务状态改变的广播")
                    initParams()
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
        // PackageManagerUtils.restartApplication(Constant.PKG_NAME, "com.buy.together.MainActivity")
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
        try {
            clickPermission(event)
            val pkgName = event?.packageName
            if(!pkgName.isNullOrEmpty() && pkgName==Constant.BUY_TOGETHER_PKG)    //避免打开支付宝时进入自动化
            {
                chooseLogin()
            }
        } catch (e: Exception) {
            L.e(e.message)
        }
    }


    private fun clickPermission(event: AccessibilityEvent?) {
        event?.apply {
            postDelay(Runnable {
                findViewByFullText("允许")?.let {
                    L.i("pkgName: ${this.packageName}")
                    performViewClick(it)
                }
            }, 2)

            findViewByFullText("好的")?.let {
                performViewClick(it)
            }
        }
    }

    private fun test() {
        if (!testFlag) {
            testFlag = true
            NodeController.Builder()
                .setNodeService(this)
                .setNodeParams("是否打开已复制链接", 0, false, 10, true)
                .setNodeParams("是否打开", 1, false, 10, true)
                .setNodeParams("https://", 1, false, 10, true)
                .setNodeParams("确定", 1, false, 10, true)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("???")
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        L.i("...")
                    }

                })
                .create()
                .execute()

        }
    }

    /**
     * 选择登录
     */
    private fun chooseLogin() {
        if (mCurPageType == PageEnum.START_PAGE) {
            deadLoop = 0
            setCurPageType(PageEnum.CHOOSING_LOGIN_PAGE)
            initTaskData()
            L.i("拼多多登录界面2")
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
                confirmPayResult()
                return
            }

            val isLogined =
                SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).getBoolean(Constant.KEY_IS_LOGINED)
            L.i("是否已登录：$isLogined")
            if (isLogined)   //已经登录成功
            {
                findPersonal()
                return
            }

            NodeController.Builder()
                .setNodeService(this@MyAccessibilityService)
                //.setNodeParams("好的", 0, 3, true)
                //.setNodeParams("允许", 0, 3, true)
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

    private fun findPersonal() {
        val isDropLine = findViewByText("请使用其它方式登录")
        if (isDropLine != null) {
            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME).remove(Constant.KEY_IS_LOGINED, true)
            initParams()
            chooseLogin()
        } else {
            NodeController.Builder()
                .setNodeService(this)
                .setNodeParams("个人中心", 0, true, 6)
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        L.i("已找到个人中心")
                        val loginButtonNode = findViewByText("点击登录")
                        if (loginButtonNode != null) {
                            SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                                .remove(Constant.KEY_IS_LOGINED, true)
                            initParams()
                            chooseLogin()
                        } else
                            afterLoginSucceed()     //任务失败，重新进来，不再重新登录
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        this@MyAccessibilityService.performBackClick()
                        //findPersonal()
                    }
                })
                .create()
                .execute()
        }
    }

    /**
     * 确认支付是否成功
     */
    private fun confirmPayResult() {
        NodeController.Builder()
            .setNodeService(this)
            .setNodeParams("个人中心", 0, true, 4)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已找到个人中心，开始确认是否支付成功")
                    ConfirmPayResult(this@MyAccessibilityService)
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

                override fun onTaskFailed(failedMsg: String) {
                    this@MyAccessibilityService.performBackClick()
                    confirmPayResult()
                    L.i("deadLoop = $deadLoop")
                    deadLoop++
                    if (deadLoop >= 5) {
                        LoginService(this@MyAccessibilityService).login(object : TaskListener {
                            override fun onTaskFinished() {
                                confirmPayResult()
                            }

                            override fun onTaskFailed(failedMsg: String) {
                                var failedMsg = "账号被封停"
                                responTaskFailed(failedMsg)
                            }

                        })
                    }
                }
            })
            .create()
            .execute()

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
                    // 有可能弹出见面福利,有时成功登录，但找不到点击登录死循环。
                    dealAccident()
                }
            })
            .create()
            .execute()
    }

    private fun dealAccident() {
        performBackClick(2, object : AfterClickedListener {
            override fun onClicked() {
                enterLoginFailed()
            }
        })
    }


    inner class LoginListenerImpl : TaskListener {
        override fun onTaskFinished() {
            //登录完成后，判断是评论任务还是正常任务
            //afterLoginSucceed()
            findPersonal()
        }

        override fun onTaskFailed(failedMsg: String) {
            responTaskFailed(failedMsg)
        }
    }

    /**
     * 登录成功后的处理
     */
    private fun afterLoginSucceed() {
        val taskCategory = TaskDataUtil.instance.getTask_category()
        taskCategory?.apply {
            when (this) {
                TaskCategory.NORMAL_TASK -> startNormalTaskAfterLogined()
                else -> startOtherTaskAfterLogined()
            }
        }
    }

    /**
     * 开始自动执行正常任务
     */
    private fun startNormalTaskAfterLogined() {
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
    }

    /**
     * 开始自动执行非正常任务
     */
    private fun startOtherTaskAfterLogined() {
        L.i("开始自动执行非正常任务")
        CommentTaskService(this@MyAccessibilityService)
            .setCommentStatusListener(object : CommentTaskService.CommentStatusListener {
                override fun responCommentStatus(status: Int) {
                    L.i("评论任务返回值 status = $status")
                    SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)
                        .put(Constant.KEY_COMMENT_SUCCESS_CODE, status, true)
                }
            })
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    this@MyAccessibilityService.postDelay(Runnable{
                        responTaskFinished()
                    }, 3)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responTaskFailed(failedMsg)
                }
            })
            .startService()
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
        /* val launchIntentForPackage =
             packageManager?.getLaunchIntentForPackage(Constant.PKG_NAME)
         launchIntentForPackage?.apply {
             startActivity(this)
         }*/
        startPddTask()

        postDelay(Runnable {
            val taskType = TaskDataUtil.instance.getTask_type()
            if (taskType != null && taskType.toString().contains("4"))   //是否有支付
            {
                PackageManagerUtils.killApplication(Constant.ALI_PAY_PKG)
                PackageManagerUtils.restartApplication(PKG_PINDUODUO, ACTIVITY_PDD_LAUNCHER)
            } else {
                responTaskFinished()
            }
        }, 5)

    }

    @Synchronized
    private fun responTaskFinished() {
        L.i("任务完成，重新开始下一轮任务")
        //initParams()
        startPddTask()
        postDelay(Runnable {
            //mTaskListener?.onTaskFinished()
            sendBroadcast(Intent(ACTION_TASK_SUCCEED))
        }, 2)
    }
    @Synchronized
    private fun responTaskFailed(msg: String) {
        //initParams()
        startPddTask()
        postDelay(Runnable {
            // mTaskListener?.onTaskFailed(msg)
            val intent = Intent(ACTION_TASK_FAILED)
            intent.putExtra(KEY_TASK_MSG, msg)
            sendBroadcast(intent)
        }, 2)
    }

    /**
     * 开始任务之前初始化所有参数
     */
    private fun initParams() {
        setCurPageType(PageEnum.START_PAGE)
        //TaskDataUtil.instance.clearData()
    }

}