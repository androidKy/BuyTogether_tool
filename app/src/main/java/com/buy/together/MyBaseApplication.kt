package com.buy.together

import android.app.Application
import com.buy.together.utils.CrashHandler
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.CrashReport
import com.tinkerpatch.sdk.TinkerPatch
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike


class MyBaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        com.utils.common.Utils.init(this)

        CrashReport.initCrashReport(this, "bddd8c650b", false)

        CrashHandler.getInstance().init(this)

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            //.showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
            //.methodCount(0)         // (Optional) How many method line to show. Default 2
            //.methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("Pdd_Log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        initTinker()
    }

    private fun initTinker(){
        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        val tinkerApplication = TinkerPatchApplicationLike.getTinkerPatchApplicationLike()
        TinkerPatch.init(tinkerApplication)
            .reflectPatchLibrary()
            // .setPatchRollbackOnScreenOff(true)
            //  .setPatchRestartOnSrceenOff(true)
            .setFetchPatchIntervalByHours(1)

        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval()
    }
}