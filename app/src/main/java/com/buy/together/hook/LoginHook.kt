package com.buy.together.hook

import android.view.View
import android.widget.Button
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class LoginHook : HookListener {
    private val tag: String = LoginHook::class.java.simpleName

    private val mLoginFragmentClassName = "com.xunmeng.pinduoduo.login.LoginFragment"
    private val mTencentLogin = "com.tencent.mobileqq.activity.LoginActivity"

    override fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        HookUtil.hookMethod(loadPkgParam, mTencentLogin, "onClick",
            View::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    try {
                        val view = param?.args?.get(0) as View
                        XposedBridge.log("$tag : view id = ${view.id}")

                        if (view is TextView) {
                            XposedBridge.log("$tag : TextView text = ${view.text}")
                        } else if (view is Button) {
                            XposedBridge.log("$tag : Button text = ${view.text}")
                        }
                    } catch (e: Exception) {
                        XposedBridge.log("$tag : ${e.message}")
                    }
                }
            })


       /* HookUtil.hookMethod(loadPkgParam,mLoginFragmentClassName,"onCreate", Bundle::class.java,
            object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    try {
                        val fragment = param?.thisObject is Fragment
                        val field = XposedHelpers.findFieldIfExists(fragment.javaClass, "a")
                    } catch (e: Exception) {
                        XposedBridge.log("$tag : exception:${e.message}")
                    }
                }
            })*/
    }
}