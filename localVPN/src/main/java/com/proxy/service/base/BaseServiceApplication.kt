package com.proxy.service.base

import android.app.Application

/**
 * Description:VPN service Application
 * Created by Quinin on 2019-07-18.
 **/
class BaseServiceApplication : Application() {

    private val tag = BaseServiceApplication::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

    }
}