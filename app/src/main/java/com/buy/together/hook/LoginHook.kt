package com.buy.together.hook

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class LoginHook : HookListener {
    private val tag: String = LoginHook::class.java.simpleName

    private val mLoginFragmentClassName = "com.xunmeng.pinduoduo.login.LoginFragment"
    //private val mTencentLogin = "com.tencent.mobileqq.activity.LoginActivity"

    private val mTencentLogin = "mqq.app.AppRuntime"

    override fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        /*  HookUtil.hookMethod(loadPkgParam, mTencentLogin, "onClick",
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
              })*/


        HookUtil.hookMethod(loadPkgParam, "com.tencent.qqconnect.wtlogin.Login", "onCreate",
            Bundle::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedBridge.log("$tag : Login hooked")
                    val SSOAccountObserver =
                        XposedHelpers.findClassIfExists("mqq.observer.SSOAccountObserver", loadPkgParam.classLoader)
                    if (SSOAccountObserver != null) {
                        XposedBridge.log("$tag: SSOAccountObserver exited")
                        HookUtil.hookMethod(loadPkgParam,
                            mTencentLogin,
                            "ssoLogin",
                            String::class.java,
                            String::class.java,
                            Int::class.java,
                            SSOAccountObserver,
                            object : XC_MethodHook() {
                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    val args = param?.args
                                    for (arg in args!!) {

                                        XposedBridge.log("$tag : ssoLogin arg = $arg")

                                    }

                                }
                            })

                        HookUtil.hookMethod(loadPkgParam,"mqq.app.AppRuntime","login",
                            String::class.java,ByteArray::class.java,Bundle::class.java,SSOAccountObserver,object:XC_MethodHook(){
                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    val args = param?.args
                                    for(arg in args!!)
                                    {
                                        if(arg is String)
                                        {
                                            XposedBridge.log("$tag : login arg string = $arg")
                                        }else if(arg is ByteArray)
                                        {
                                            XposedBridge.log("$tag : login arg byteArray = ${String(arg)}")
                                        }else if(arg is Bundle)
                                        {
                                            for(key in arg.keySet())
                                            {
                                                XposedBridge.log("$tag : login arg bundle key=$key value=${arg.get(key)}")
                                            }
                                        }
                                    }
                                }
                            })
                    }else{
                        XposedBridge.log("$tag: SSOAccountObserver not exited")
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