package com.buy.together

import com.buy.together.base.BaseView

/**
 * Description:MainActivity的接口回调
 * Created by Quinin on 2019-07-29.
 **/
interface MainAcView:BaseView {
    fun onResponUpdateTask()
    fun onPermissionGranted()
}