package com.buy.together


/**
 * Description:MainActivity的接口回调
 * Created by Quinin on 2019-07-29.
 **/
interface MainAcView {

    fun onResponUpdateTask(result:Boolean)
    fun onPermissionGranted()

    fun onAccessibilityService()

    fun onClearDataResult()
}