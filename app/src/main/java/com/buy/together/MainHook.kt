package com.buy.together

import android.text.TextUtils
import com.accessibility.service.util.Constant
import com.buy.together.hook.CloakHook
import com.buy.together.hook.DeviceParamsHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName

        if (TextUtils.isEmpty(packageName))
            return

        if(packageName.equals(Constant.ALI_PAY_PKG))
        {
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