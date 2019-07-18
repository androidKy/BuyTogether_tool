package com.proxy.service

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.proxy.service.LocalVpnService.Companion.START_VPN_SERVICE_REQUEST_CODE
import com.proxy.service.base.BaseService
import com.safframework.log.L
import com.utils.common.ToastUtils
import java.util.*

/**
 * Description: 用来启动VPN Service，保证长链接，不轻易被杀死
 * Created by Quinin on 2019-07-18.
 **/
class MyVpnService : BaseService() {

    override fun onCreate() {
        super.onCreate()

        createNotification(VPN_NOTIFICATION_ID, CHANNEL_NAME)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        private var mInitProxyData = false
        /**
         * 设置代理数据
         */
        fun setProxyData(authUser: String, authPsw: String, domain: String, ports: Array<String>): MyBinder {
            var portPosition = 0
            if (ports.size > 1) {
                portPosition = Random().nextInt(ports.size - 1)
            }
            val proxyUrl =
                "http://($authUser:$authPsw)@$domain:${ports[portPosition]}"
            L.i("save proxyUrl: $proxyUrl")

            LocalVpnService.ProxyUrl = proxyUrl

            val preferences = getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("CONFIG_URL_KEY", proxyUrl)
            editor.apply()

            mInitProxyData = true

            return this
        }

        /**
         * 开始连接VPN
         */
        fun connectVPN(activity: Activity) {
            if (!mInitProxyData)
                ToastUtils.showToast(this@MyVpnService, "未设置代理数据")

            val intent = LocalVpnService.prepareVPN(activity)
            if (intent == null)
                startConnect(activity)
            else activity.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE)
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