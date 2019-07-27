package com.buy.together

import android.text.TextUtils
import com.buy.together.hook.CloakHook
import com.buy.together.hook.DeviceParamsHook
import com.buy.together.hook.LoginHook
import com.buy.together.utils.Constant
import com.utils.common.Constants
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName

        if (TextUtils.isEmpty(packageName))
            return

        //只拦截拼多多这个应用
        if (packageName.equals(Constants.PKG_NAME) || packageName.equals(Constant.BUY_TOGETHER_PKG)
            || packageName.equals(Constant.QQ_TIM_PKG) || packageName.equals(Constant.QQ_LIATE_PKG)
            || packageName.equals(Constant.QQ_FULL_PKG)
        ) {
            lpparam?.apply {
                CloakHook().hook(this)
                DeviceParamsHook().hook(this)
                //HttpHook().hook(lpparam)
                LoginHook().hook(lpparam)
                // WebViewHook().hook(lpparam)
            }
        } else return
    }
}