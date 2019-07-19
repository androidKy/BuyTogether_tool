package com.proxy.service

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE
import com.proxy.service.base.BaseService
import com.safframework.log.L
import com.utils.common.ToastUtils

/**
 * Description: 用来启动VPN Service，保证长链接，不轻易被杀死
 * Created by Quinin on 2019-07-18.
 **/
class MyVpnService : BaseService() {
    private var mInitProxyData = false


    override fun onCreate() {
        super.onCreate()

        createNotification(VPN_NOTIFICATION_ID, CHANNEL_NAME)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            val authUser = getStringExtra(LocalVpnService.AUTHUSER_KEY)
            val authPsw = getStringExtra(LocalVpnService.AUTHPSW_KEY)
            val domain = getStringExtra(LocalVpnService.DOMAIN_KEY)
            val port = getStringExtra(LocalVpnService.PORT_KEY)
            L.i("vpnService authUser:$authUser authPsw:$authPsw domain:$domain port:$port")
            initProxyData(authUser, authPsw, domain, port)
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }

    /**
     * 设置代理数据
     */
    private fun initProxyData(authUser: String, authPsw: String, domain: String, port: String) {

        val proxyUrl =
            "http://($authUser:$authPsw)@$domain:$port"
        L.i("save proxyUrl: $proxyUrl")

        LocalVpnService.ProxyUrl = proxyUrl

        val preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("CONFIG_URL_KEY", proxyUrl)
        editor.apply()

        mInitProxyData = true
    }

    inner class MyBinder : Binder() {

        /**
         * 开始连接VPN
         */
        fun connectVPN(activity: Activity) {
            if (!mInitProxyData) {
                ToastUtils.showToast(this@MyVpnService, "未设置代理数据")
                return
            }

            LocalVpnService.setAcitivity(activity)

            val intent = LocalVpnService.prepare(activity)
            if (intent == null) {
                startConnect(activity)
            } else activity.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE)
        }

        /**
         * 已连接上，更换代理IP
         */
        fun changeIP(activity: Activity) {
            connectVPN(activity)
        }

        /**
         * 重连IP
         */
        fun reconnect() {

        }

        /**
         * 断掉代理IP
         */
        fun disconnect() {

        }
    }

    private fun startConnect(activity: Activity) {
        val intent = Intent(activity, LocalVpnService::class.java)
        startService(intent)
    }
}