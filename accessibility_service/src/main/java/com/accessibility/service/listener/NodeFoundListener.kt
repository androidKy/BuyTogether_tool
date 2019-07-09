package com.accessibility.service.listener

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
interface NodeFoundListener {
    fun onNodeFound(nodeInfo: AccessibilityNodeInfo?)
}