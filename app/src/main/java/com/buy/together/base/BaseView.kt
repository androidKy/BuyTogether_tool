package com.buy.together.base

import com.buy.together.bean.CloseProxyBean

/**
 * Description:
 * Created by Quinin on 2019-06-26.
 **/
interface BaseView {
    fun onFailed(msg:String?)
    fun onRequestPortsResult(port:String)
    fun onResponPortsFailed(failedMsg:String)
    fun onResponClosePort(closeProxyBean: CloseProxyBean?)
}