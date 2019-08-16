package com.proxy.service.network

import com.proxy.service.bean.ProxyIPBean

/**
 * Description:
 * Created by Quinin on 2019-08-16.
 **/
interface ProxyDataListener {
    fun onFailed(failedMsg: String)
    fun onResponProxyData(proxyIPBean: ProxyIPBean?)
}