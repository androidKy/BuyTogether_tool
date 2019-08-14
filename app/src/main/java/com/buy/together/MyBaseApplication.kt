package com.buy.together

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport



class MyBaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        com.utils.common.Utils.init(this)

        CrashReport.initCrashReport(this, "bddd8c650b", false)
    }


}