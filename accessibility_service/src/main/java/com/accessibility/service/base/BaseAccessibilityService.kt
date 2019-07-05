package com.accessibility.service.base

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.page.PageEnum
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

        private lateinit var mDataList: ArrayList<String>


        fun isAccessibilitySettingsOn(context: Context, canonicalName: String): Boolean {
            var accessibilityEnabled = 0
            // TestService为对应的服务
            val service = context.packageName + "/" + canonicalName
            // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
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
                // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
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


        public fun setDataList(dataList: ArrayList<String>) {
            this.mDataList = dataList
        }

        public fun getDataList(): ArrayList<String> {
            return mDataList
        }
    }

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

        return null
    }

    fun findViewByClassName(nodeInfo: AccessibilityNodeInfo, className: String): AccessibilityNodeInfo? {
        if (nodeInfo.className != null && nodeInfo.className == className)
            return nodeInfo

        val childCount = nodeInfo.childCount
        L.i("className: $className childCount: $childCount")
        if (childCount > 0) {
            for (i in 0 until childCount) {
                findViewByClassName(nodeInfo.getChild(i), className)
            }
        }

        return null
    }

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
        }, delayTime*1000L)
    }

}