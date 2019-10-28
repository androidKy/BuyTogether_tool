package com.proxy.droid.network

import com.proxy.droid.bean.ProxyIPBean

/**
 * Description:
 * Created by Quinin on 2019-08-16.
 **/
interface ProxyDataListener {
    fun onFailed(failedMsg: String)
    fun onResponProxyData(proxyIPBean: ProxyIPBean?)
}