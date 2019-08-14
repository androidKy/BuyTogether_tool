package com.utils.common.accessibility.utils

import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
class WidgetConstant {
    companion object {
        const val EDITTEXT = "android.widget.EditText"
        const val IMAGEVIEW = "android.widget.ImageView"
        const val RECYCLERVIEW = "android.support.v7.widget.RecyclerView"

        fun setEditText(text: String?, nodeInfo: AccessibilityNodeInfo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val arguments = Bundle()
                arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            }
        }

    }
}