package com.proxy.service.base

import android.app.Service

/**
 * Description:
 * Created by Quinin on 2019-07-18.
 **/
abstract class BaseService : Service() {

    companion object {
        const val CHANNEL_ID = "vpn"
        const val CHANNEL_NAME = "公牛代理"
        const val CHANNEL_DESCRIPTION = "国内代理IP"
        const val VPN_NOTIFICATION_ID = 0x01 //通知的唯一ID
        const val VPN_NOTIFICATION_NAME = "VpnService"
        const val VPN_NOTIFICATION_DESC = "国内代理IP切换"
    }

    override fun onCreate() {
        super.onCreate()

    }

}