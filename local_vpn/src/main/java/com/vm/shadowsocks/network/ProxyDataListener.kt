package com.vm.shadowsocks.network

import com.vm.shadowsocks.bean.ProxyIPBean


/**
 * Description:
 * Created by Quinin on 2019-08-16.
 **/
interface ProxyDataListener {
    fun onFailed(failedMsg: String)
    fun onResponProxyData(proxyIPBean: ProxyIPBean?)
}