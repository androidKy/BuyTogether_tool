package com.buy.together

import android.text.TextUtils
import com.accessibility.service.util.Constant
import com.buy.together.hook.CloakHook
import com.buy.together.hook.DeviceParamsHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
   /* override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        try {
            XposedBridge.hookAllMethods(
                XposedHelpers.findClass(
                    "com.android.internal.policy.impl.PhoneWindowManager",
                    this::class.java.classLoader
                ), "checkAddPermission", object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        param?.result = 0
                        *//*if (param?.args?.get(0) != null && param.args[0] is WindowManager.LayoutParams) {
                            val params = param.args[0] as WindowManager.LayoutParams

                            if (params.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR) {
                                param.result = 0  //当检测到是系统错误对话框时，返回0，即ok！
                            }
                        }*//*
                    }
                })
        } catch (e: Exception) {

        }
    }*/


    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName

        if (TextUtils.isEmpty(packageName))
            return

        if (packageName.equals(Constant.ALI_PAY_PKG)) {
            lpparam?.apply {
                CloakHook().hook(this)
            }
            return
        }

        //只拦截拼多多这个应用
        if (packageName.equals(Constant.PKG_NAME) || packageName.equals(Constant.BUY_TOGETHER_PKG)
            || packageName.equals(Constant.QQ_TIM_PKG) || packageName.equals(Constant.QQ_LIATE_PKG)
            || packageName.equals(Constant.QQ_FULL_PKG)
        ) {
            lpparam?.apply {
                CloakHook().hook(this)
                DeviceParamsHook().hook(this)
                //HttpHook().hook(lpparam)
                //LoginHook().hook(lpparam)

                /*if (packageName.equals(Constant.BUY_TOGETHER_PKG)) {
                    LocationHook().hook(lpparam)
                }*/
                // WebViewHook().hook(lpparam)
            }
        } else return
    }
}