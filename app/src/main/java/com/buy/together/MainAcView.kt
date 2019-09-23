package com.buy.together


/**
 * Description:MainActivity的接口回调
 * Created by Quinin on 2019-07-29.
 **/
interface MainAcView {

    fun onResponUpdateTask()
    fun onPermissionGranted()

    fun onAccessibilityService()
}