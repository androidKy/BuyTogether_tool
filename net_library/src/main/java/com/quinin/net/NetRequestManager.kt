package com.quinin.net

/**
 * Description: 请求网络数据管理类
 * Created by Quinin on 2019-07-17.
 **/
class NetRequestManager private constructor() {

    companion object {
        val instance: NetRequestManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetRequestManager()
        }
    }
}