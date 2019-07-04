package com.buy.together.hook

import android.os.Bundle
import com.buy.together.utils.Constant
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
        if (loadPkgParam.packageName == Constant.QQ_FULL_PKG) {

            val simpleAccountClass = XposedHelpers.findClassIfExists(
                "com.tencent.qphone.base.remote.SimpleAccount",
                loadPkgParam.classLoader
            )
            if (simpleAccountClass != null) {
                HookUtil.log(tag, "SimpleAccount was exited")
                HookUtil.hookMethod(loadPkgParam, "mqq.app.AppRuntime",
                    "login", simpleAccountClass, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            HookUtil.logParam("$tag,AppRuntime login(SimpleAccount) ", param)
                        }
                    })

                HookUtil.hookMethod(loadPkgParam, "com.tencent.qphone.base.remote.SimpleAccount",
                    "parseSimpleAccount", String::class.java, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            HookUtil.logParam("$tag,SimpleAccount,parseSimpleAccount(String)", param)
                        }
                    })

                HookUtil.hookMethod(loadPkgParam, "com.tencent.qphone.base.remote.SimpleAccount",
                    "setAttribute", String::class.java, String::class.java, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            HookUtil.logParam("$tag,SimpleAccount,setAttribute(String,String)", param)
                        }
                    })
            } else {
                HookUtil.log(tag, "SimpleAccount not existed")
            }

            val accountObserver =
                XposedHelpers.findClassIfExists("mqq.observer.AccountObserver", loadPkgParam.classLoader)

            if (accountObserver != null) {
                HookUtil.log(tag, "accountObserver != null")
                HookUtil.hookMethod(loadPkgParam, "mqq.app.AppRuntime", "login",
                    String::class.java, ByteArray::class.java, accountObserver, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            HookUtil.logParam("$tag,AppRuntime login(String,ByteArray,AccountObserver)", param)
                        }
                    })

            } else {
                HookUtil.log(tag, "accountObserver == null")
            }


            /* HookUtil.hookMethod(loadPkgParam, "com.tencent.qqconnect.wtlogin.Login", "onCreate",
                 Bundle::class.java, object : XC_MethodHook() {
                     override fun afterHookedMethod(param: MethodHookParam?) {
                         XposedBridge.log("$tag : Login hooked")
                         val ssoAccountObserverClass =
                             XposedHelpers.findClassIfExists("mqq.observer.SSOAccountObserver", loadPkgParam.classLoader)
                         if (ssoAccountObserverClass != null) {
                             XposedBridge.log("$tag: SSOAccountObserver exited")
                             HookUtil.hookMethod(loadPkgParam,
                                 mTencentLogin,
                                 "ssoLogin",
                                 String::class.java,
                                 String::class.java,
                                 Int::class.java,
                                 ssoAccountObserverClass,
                                 object : XC_MethodHook() {
                                     override fun afterHookedMethod(param: MethodHookParam?) {
                                         val args = param?.args

                                         for (i in 0 until args!!.size) {
                                             XposedBridge.log("$tag : ssoLogin arg[$i] = ${args[i]}")
                                         }
                                     }
                                 })

                             HookUtil.hookMethod(loadPkgParam, "mqq.app.ServletContainer", "getServlet",
                                 String::class.java, object : XC_MethodHook() {
                                     override fun afterHookedMethod(param: MethodHookParam?) {
                                         HookUtil.log(tag, "ServletContainer getServlet result: ${param?.args!![0]}")
                                     }
                                 })

                             val toServiceMsg = XposedHelpers.findClassIfExists(
                                 "com.tencent.qphone.base.remote.ToServiceMsg",
                                 loadPkgParam.classLoader
                             )
                             if (toServiceMsg != null) {
                                 HookUtil.log(tag, "ToServiceMsg was exited")
                                 HookUtil.hookMethod(loadPkgParam,
                                     "com.tencent.mobileqq.msf.sdk.k",
                                     "sendMsg",
                                     toServiceMsg,
                                     object : XC_MethodHook() {
                                         override fun afterHookedMethod(param: MethodHookParam?) {
                                             HookUtil.log(tag, "IMsfProxy sendMsg")
                                         }
                                     })
                             } else HookUtil.log(tag, "ToServiceMsg was not exit")

                         } else {
                             XposedBridge.log("$tag: SSOAccountObserver not exited")
                         }

                     }
                 })

             val errorMsgClass = XposedHelpers.findClassIfExists("oicq.wlogin_sdk.tools.ErrMsg",loadPkgParam.classLoader)
             if(errorMsgClass != null)
             {

                 HookUtil.hookMethod(loadPkgParam,"mqq.observer.WtloginObserver","OnVerifyCode",
                     String::class.java,ByteArray::class.java,Long::class.java,ArrayList::class.java,ByteArray::class.java,
                     Int::class.java,errorMsgClass,object:XC_MethodHook(){
                         override fun afterHookedMethod(param: MethodHookParam?) {
                             HookUtil.log(tag,"OnVerifyCode param size = ${param?.args!!.size}")


                         }
                     })

                 HookUtil.hookMethod(loadPkgParam,"mqq.observer.WtloginObserver","onReceive",
                     Int::class.java,Boolean::class.java,Bundle::class.java,object:XC_MethodHook(){
                         override fun afterHookedMethod(param: MethodHookParam?) {
                             HookUtil.log(tag,"onReceive param size = ${param?.args!!.size}")
                         }
                     })
             }*/

            hookHttp(loadPkgParam)
        }

    }

    private fun hookHttp(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        HookUtil.hookMethod(loadPkgParam, "com.squareup.okhttp.HttpUrl\$Builder", "toString",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.log(tag,"HttpUrl\$Builder url: ${param?.result}")
                }
            })

        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.HttpUrl\$Builder","password",
            String::class.java,object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.logParam("$tag,HttpUrl\$Builder,password(String)",param)
                }
            })

        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.HttpUrl\$Builder","username",
            String::class.java,object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.logParam("$tag,HttpUrl\$Builder,username(String)",param)
                }
            })
        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.HttpUrl\$Builder","addQueryParameter",
            String::class.java,String::class.java,object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.logParam("$tag,HttpUrl\$Builder,addQueryParameter(String,String)",param)
                }
            })

        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.ResponseBody","string",
           object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.log(tag,"ResponseBody,string() result: ${param?.result}")
                }
            })

        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.ResponseBody","bytes",
            object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.log(tag,"ResponseBody,bytes() result: ${String(param?.result as ByteArray)}")
                }
            })
        HookUtil.hookMethod(loadPkgParam,"com.squareup.okhttp.ResponseBody","byteStream",
            object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.log(tag,"ResponseBody,byteStream() result: InputStream")
                }
            })

        HookUtil.hookMethod(loadPkgParam,"com.tencent.component.network.utils.NetworkUtils","isNetworkUrl",
            String::class.java,object:XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    HookUtil.logParam("$tag,NetworkUtils,isNetworkUrl(String)",param)
                }
            })
    }
}