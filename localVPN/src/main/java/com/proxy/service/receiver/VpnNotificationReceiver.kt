package com.proxy.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-18.
 **/
class VpnNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        L.i("VpnNotificationReceiver onReceive ...")
    }
}