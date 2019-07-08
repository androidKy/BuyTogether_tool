package com.accessibility.service.base

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.data.TaskDataUtil
import com.accessibility.service.data.TaskServiceData
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.page.PageEnum
import com.google.gson.Gson
import com.safframework.log.L
import java.util.*

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


    fun setCurPageType(pageEnum: PageEnum) {
        // L.i("setCurPageType mCurPageType = ${pageEnum.name}")
        this.mCurPageType = pageEnum
    }

    /**
     * 获取任务数据
     */
    fun initTaskData() {
        try {
            getSharedPreferences("pinduoduo_task_sp", Context.MODE_PRIVATE).getString("task_data", "")
                .let {
                    if (!TextUtils.isEmpty(it)) {
                        val taskServiceData = Gson().fromJson(it, TaskServiceData::class.java)
                        TaskDataUtil.instance.initData(taskServiceData)
                    }
                }
        } catch (e: Exception) {
            L.e(e.message)
        }
    }


    /**
     * 根据text获取节点
     */
    fun findViewByText(text: String): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null

        val nodeList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        L.i("nodeList size = ${nodeList.size}")
        if (nodeList.size > 0) {
            for (node in nodeList) {
                L.i("nodeList text = ${node.text} className = ${node.className}")
                if (node.text == text)
                    return node
            }
            return nodeList[0]
        }
        L.i("$text not found")
        return null
    }

    /**
     * 根据view className获取节点
     */
    fun findViewByClassName(nodeInfo: AccessibilityNodeInfo, className: String, nodeFoundListener: NodeFoundListener) {
        if (nodeInfo.className != null && nodeInfo.className == className) {
            nodeFoundListener.onNodeFound(nodeInfo)
            return
        }
        val childCount = nodeInfo.childCount
        L.i("className: ${nodeInfo.className} childCount: $childCount")

        for (i in 0 until childCount) {
            val childNode = nodeInfo.getChild(i)
            if (childNode.className != null && childNode.className == className) {
                nodeFoundListener.onNodeFound(childNode)
                return
            }
            findViewByClassName(childNode, className, nodeFoundListener)
        }
    }

    /**
     * 根据view className和text获取节点·
     */
    fun findViewByNameAndText(
        nodeInfo: AccessibilityNodeInfo,
        className: String,
        text: String
    ): AccessibilityNodeInfo? {
        L.i("className: ${nodeInfo.className} text: ${nodeInfo.text}")

        if (nodeInfo.className != null && nodeInfo.className == className
            && nodeInfo.text != null && nodeInfo.text == text
        ) {
            return nodeInfo
        }

        val childCount = nodeInfo.childCount
        if (childCount > 0) {
            for (i in 0 until childCount) {
                return findViewByNameAndText(nodeInfo.getChild(i), className, text)
            }
        }

        return null
    }

    /**
     * 根据
     */
    fun findViewById(id: String): AccessibilityNodeInfo? {
        val nodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id)

        return if (nodeList.size > 0) nodeList[0] else null
        /* nodeInfo.findAccessibilityNodeInfosByViewId(id).let {
             if (it.size > 0)
                 return it[0]
         }

         return nodeInfo*/
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?) {

        var nodeInfo1: AccessibilityNodeInfo? = nodeInfo ?: return
        while (nodeInfo1 != null) {
            if (nodeInfo1.isClickable) {
                nodeInfo1.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
            nodeInfo1 = nodeInfo1.parent
        }
    }

    /**
     * 延迟点击事件
     *
     * @param nodeInfo
     * @param delayTime
     */
    fun performViewClick(nodeInfo: AccessibilityNodeInfo?, delayTime: Long) {
        mHandler.postDelayed({
            performViewClick(nodeInfo)
        }, delayTime * 1000L)
    }

    /**
     * 延迟执行
     */
    fun postDelay(runnable: Runnable, delayTime: Int) {
        mHandler.postDelayed(runnable, delayTime * 1000L)
    }

}