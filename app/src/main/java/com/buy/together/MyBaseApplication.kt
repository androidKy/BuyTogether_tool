package com.buy.together

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger.addLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.DiskLogAdapter




class MyBaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        com.utils.common.Utils.init(this)

        CrashReport.initCrashReport(this, "bddd8c650b", false)

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(1)         // (Optional) How many method line to show. Default 2
            .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
           // .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("Pdd_Log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }


}