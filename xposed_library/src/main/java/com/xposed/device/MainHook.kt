package com.xposed.device

import android.text.TextUtils
import com.xposed.device.hook.CloakHook
import com.xposed.device.hook.DeviceParamsHook
import com.xposed.device.hook.PkgConstant
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName

        if (TextUtils.isEmpty(packageName))
            return

        //只拦截拼多多这个应用
        if (packageName.equals(PkgConstant.PKG_NAME) || packageName.equals(PkgConstant.BUY_TOGETHER_PKG)
            || packageName.equals(PkgConstant.QQ_TIM_PKG) || packageName.equals(PkgConstant.QQ_LIATE_PKG)
            || packageName.equals(PkgConstant.QQ_FULL_PKG)
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