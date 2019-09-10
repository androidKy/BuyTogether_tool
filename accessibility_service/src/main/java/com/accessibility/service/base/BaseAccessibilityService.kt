package com.accessibility.service.base

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.data.TaskBean
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.accessibility.service.util.Constant
import com.accessibility.service.util.TaskDataUtil
import com.google.gson.Gson
import com.safframework.log.L
import com.utils.common.PackageManagerUtils
import com.utils.common.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
abstract class BaseAccessibilityService : AccessibilityService() {
    var mCurPageType = PageEnum.START_PAGE
    var mIsLogined = false

    val mHandler = Handler(Looper.getMainLooper())

    public companion object {

        fun isAccessibilitySettingsOn(context: Context, canonicalName: String): Boolean {
            var accessibilityEnabled = 0
            // TestService为对应的服务
            val service = context.packageName + "/" + canonicalName
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    context.applicationContext.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }

            val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                    context.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (settingValue != null) {
                    mStringColonSplitter.setString(settingValue)
                    while (mStringColonSplitter.hasNext()) {
                        val accessibilityService = mStringColonSplitter.next()

                        if (accessibilityService.equals(service, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            } else {
                L.i("不支持自动点击服务")
            }
            return false
        }

    }

    override fun onCreate() {
        super.onCreate()
        L.i("BaseAccessibilityService onCreate()")
    }

    /**
     * 设置当前处于哪一个页面
     */
    fun setCurPageType(pageEnum: PageEnum) {
        // L.i("setCurPageType mCurPageType = ${pageEnum.name}")
        this.mCurPageType = pageEnum
    }

    fun setIsLogined(loginStatus: Boolean) {
        this.mIsLogined = loginStatus
    }

    fun getHandler(): Handler {
        return mHandler
    }

    /**
     * 获取任务数据
     */
    fun initTaskData() {
        try {
            SPUtils.getInstance(this, Constant.SP_TASK_FILE_NAME).getString(Constant.KEY_TASK_DATA)
                .let {
                    L.i("无障碍服务初始化数据: $it")
                    if (!TextUtils.isEmpty(it)) {
                        val taskServiceData = Gson().fromJson(it, TaskBean::class.java)
                        TaskDataUtil.instance.initData(taskServiceData)
                    }
                }
        } catch (e: Exception) {
            L.e(e.message)
        }
    }

    /**
     * 查找符合条件的node集合
     */
    fun findViewsByText(text: String): List<AccessibilityNodeInfo> {
        val rootNode = rootInActiveWindow
        return if (rootNode != null)
            rootNode.findAccessibilityNodeInfosByText(text)
        else ArrayList<AccessibilityNodeInfo>()
    }


    /**
     * 根据text获取节点
     */
    fun findViewByText(text: String): AccessibilityNodeInfo? {
        try {
            if (rootInActiveWindow == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val nodeWindowList = windows
                    //L.i("window size:${nodeWindowList.size}")
                    (0 until nodeWindowList.size)
                        .forEach { i ->
                            //L.i("window id: ${nodeWindowList[i].id}")
                            val nodeInfo = nodeWindowList[i].root
                            if (nodeInfo != null) {
                                val nodeList = nodeInfo.findAccessibilityNodeInfosByText(text)
                                // L.i("nodeList size = ${nodeList.size} textList = $text ")
                                if (nodeList.size > 0) {
                                    for (node in nodeList) {
                                        //  L.i("nodeList textList = ${node.text} className = ${node.className}")
                                        if (node.text == text)
                                            return node
                                    }
                                    return nodeList[0]
                                }
                            }
                        }
                }
            } else {
                val nodeList = rootInActiveWindow.findAccessibilityNodeInfosByText(text)
                // L.i("nodeList size = ${nodeList.size} textList = $text ")
                if (nodeList.size > 0) {
                    for (node in nodeList) {
                        //  L.i("nodeList textList = ${node.text} className = ${node.className}")
                        if (node.text == text)
                            return node
                    }
                    return nodeList[0]
                }
            }
        } catch (e: Exception) {
            L.i("半查找节点无障碍服务崩溃：${e.message}")
            PackageManagerUtils.restartApplication(Constant.PKG_NAME, "com.buy.together.MainActivity")
        }
        //L.i("$text not found")
        return null
    }

    /**
     * 查找与text完全相同的节点
     */
    fun findViewByFullText(text: String): AccessibilityNodeInfo? {
        try {
            if (rootInActiveWindow == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val nodeWindowList = windows
                    // L.i("window size:${nodeWindowList.size}")
                    (0 until nodeWindowList.size)
                        .forEach { i ->
                            //L.i("window id: ${nodeWindowList[i].id}")
                            val nodeInfo = nodeWindowList[i].root
                            if (nodeInfo != null) {
                                val nodeList = nodeInfo.findAccessibilityNodeInfosByText(text)
                                // L.i("nodeList size = ${nodeList.size} textList = $text ")
                                if (nodeList.size > 0) {
                                    for (node in nodeList) {
                                        //  L.i("nodeList textList = ${node.text} className = ${node.className}")
                                        if (node.text == text)
                                            return node
                                    }
                                }
                            }
                        }
                }
            } else {
                val accessibilityNodeInfo = rootInActiveWindow ?: return null

                val nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)

                if (nodeList.size > 0) {
                    for (node in nodeList) {
                        // L.i("nodeList textList = ${node.text} className = ${node.className}")
                        if (node.text == text)
                            return node
                    }
                }
            }
        } catch (e: Exception) {
            L.i("全查找节点无障碍服务崩溃：${e.message}")
            PackageManagerUtils.restartApplication(Constant.PKG_NAME, "com.buy.together.MainActivity")
        }

        //L.i("$text not found")
        return null
    }

    /**
     * 查找与text完全相同的节点集合
     */
    fun findViewsByFullText(text: String): List<AccessibilityNodeInfo>? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null

        val nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        val resultList = ArrayList<AccessibilityNodeInfo>()
        // L.i("nodeList size = ${nodeList.size}")
        if (nodeList.size > 0) {
            for (node in nodeList) {
                //  L.i("nodeList textList = ${node.text} className = ${node.className}")
                if (node.text == text)
                    resultList.add(node)
            }
        }
        // L.i("$text nodeSize: ${resultList.size}")
        return resultList
    }


    /**
     * 根据view className获取节点
     */
    fun findViewByClassName(
        nodeInfo: AccessibilityNodeInfo,
        className: String,
        nodeFoundListener: NodeFoundListener
    ) {
        if (nodeInfo.className != null && nodeInfo.className == className) {
            nodeFoundListener.onNodeFound(nodeInfo)
            return
        }
        val childCount = nodeInfo.childCount
        // L.i("className: ${nodeInfo.className} childCount: $childCount")

        for (i in 0 until childCount) {
            val childNode = nodeInfo.getChild(i) ?: continue
            if (childNode.className != null && childNode.className == className) {
                nodeFoundListener.onNodeFound(childNode)
                return
            }
            findViewByClassName(childNode, className, nodeFoundListener)
        }
    }


    /**
     * 根据view id获取节点
     */
    fun findViewById(id: String): AccessibilityNodeInfo? {
        val nodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id)

        return if (nodeList.size > 0) nodeList[0] else null
        /* recyclerViewNode.findAccessibilityNodeInfosByViewId(id).let {
             if (it.size > 0)
                 return it[0]
         }

         return recyclerViewNode*/
    }

    fun performBackClick() {
        performBackClick(0)
    }

    /**
     * 返回，延时时间：单位/秒
     */
    fun performBackClick(delayTime: Int) {
        performBackClick(delayTime, null)
    }

    fun performBackClick(delayTime: Int, afterClickedListener: AfterClickedListener?) {
        mHandler.postDelayed({
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            afterClickedListener?.onClicked()
        }, delayTime * 1000L)
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo recyclerViewNode
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?) {
        L.i("performViewClick click ${nodeInfo?.text} ")
        var nodeInfo1: AccessibilityNodeInfo? = nodeInfo ?: return
        while (nodeInfo1 != null) {
            if (nodeInfo1.isClickable) {
                L.i("${nodeInfo1.className} was clicked")
                nodeInfo1.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                postDelay(Runnable {
                    if (nodeInfo1?.text == "授权并登录") { //有时候点击一次没用
                        nodeInfo1?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                },2)
                return
            }
            nodeInfo1 = nodeInfo1.parent
        }
    }

    /**
     * 模拟向下滑动事件
     */
    fun performScrollForward(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo?.apply {
            performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        }
    }

    /**
     * 模拟向上滑动事件
     */
    fun performScrollBackward(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo?.run {
            performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        }
    }

    /**
     * 延迟点击事件
     *
     * @param nodeInfo
     * @param delayTime
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?, delayTime: Long) {
        performViewClick(nodeInfo, delayTime, null)
    }

    fun performViewClick(nodeInfo: AccessibilityNodeInfo?, clickedListener: AfterClickedListener?) {
        performViewClick(nodeInfo, 0, clickedListener)
    }

    fun performViewClick(
        nodeInfo: AccessibilityNodeInfo?,
        delayTime: Long,
        clickedListener: AfterClickedListener?
    ) {
        mHandler.postDelayed({
            performViewClick(nodeInfo)
            clickedListener?.onClicked()
            // clickedListener?.onClicked()
        }, delayTime * 1000L)
    }

    /**
     * 延迟执行
     */
    fun postDelay(runnable: Runnable, delayTime: Int) {
        mHandler.postDelayed(runnable, delayTime * 1000L)
    }

    fun removeMsg() {
        //  mHandler.removeCallbacksAndMessages(null)
    }
}