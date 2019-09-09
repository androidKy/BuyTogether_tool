package com.buy.together

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.CrashReport




class MyBaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        com.utils.common.Utils.init(this)

        CrashReport.initCrashReport(this, "bddd8c650b", false)


        val formatStrategy = PrettyFormatStrategy.newBuilder()
            //.showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
            //.methodCount(0)         // (Optional) How many method line to show. Default 2
            //.methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("Pdd_Log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }


}