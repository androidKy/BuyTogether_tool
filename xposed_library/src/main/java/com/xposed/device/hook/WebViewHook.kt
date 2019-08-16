package com.xposed.device.hook

import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Description:
 * Created by Quinin on 2019-07-12.
 **/
class WebViewHook : HookListener {
    private val tag = WebViewHook::class.java.simpleName

    override fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        if (loadPkgParam.packageName != "com.xunmeng.pinduoduo") return
        HookUtil().apply {
            hookMethod(loadPkgParam, "com.xunmeng.pinduoduo.activity.NewPageActivity", "onCreate",
                Bundle::class.java, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        log(tag, "NewPageActivity onCreate(Bundle)")

                        hookMethod(loadPkgParam, "android.webkit.WebView", "loadUrl", String::class.java,
                            object : XC_MethodHook() {
                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    log(tag, "WebView loadUrl(String) param[0] = ${param?.args?.get(0)}")
                                }
                            }
                        )

                        hookMethod(loadPkgParam, "android.webkit.WebView", "loadData",
                            String::class.java, String::class.java, String::class.java,
                            object : XC_MethodHook() {
                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    log(tag, "WebView loadUrl(String)")
                                }
                            }
                        )
                    }
                })
        }
    }


}