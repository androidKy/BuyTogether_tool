package com.utils.common

import android.app.Activity
import android.util.DisplayMetrics


/**
 * Description:
 * Created by Quinin on 2019-07-15.
 **/
class DisplayUtils {

    companion object {
        /**
         * 获取屏幕真实宽，包括导航栏和状态栏
         */
        fun getRealWidth(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)

            return displayMetrics.widthPixels
        }

        fun getRealHeight(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)

            return displayMetrics.heightPixels
        }

        /**
         * 获取屏幕显示的宽度
         */
        fun getWidth(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

            return displayMetrics.widthPixels
        }

        fun getHeight(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

            return displayMetrics.heightPixels
        }
    }
}