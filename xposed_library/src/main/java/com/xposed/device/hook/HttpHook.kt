package com.xposed.device.hook

import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class HttpHook : HookListener {

    private val tag: String = HttpHook::class.java.simpleName

    override fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        hookStringValue(loadPkgParam)
        hookHttpUrlConnection(loadPkgParam)
        hookOutputStream(loadPkgParam)
        hookGetStream(loadPkgParam)
    }

    private fun hookStringValue(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        val stringClass = XposedHelpers.findClassIfExists(HookClassName.JAVA_LANG_STRING, loadPkgParam.classLoader)
        XposedBridge.hookAllMethods(stringClass, "format", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val paramSize = param?.args?.size

                if (paramSize != null) {
                    if (paramSize > 1) (0 until paramSize).forEach { i ->
                        //TODO when param is object
                        // XposedBridge.log("$tag string format param[$i]=${param.args[i]}")
                    }
                }

            }
        })
    }

    private fun hookHttpUrlConnection(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        val httpURLConnection = XposedHelpers.findClassIfExists("java.net.HttpURLConnection", loadPkgParam.classLoader)

        XposedBridge.hookAllConstructors(httpURLConnection, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                if (param?.args?.size != 1 || param.args[0]?.javaClass != URL::class.java)
                    return
                XposedBridge.log("$tag HttpURLConnection: args[0]= " + param.args[0])
            }
        })
    }

    private fun hookOutputStream(loadPkgParam: XC_LoadPackage.LoadPackageParam) {

        HookUtil().run {
            hookMethod(loadPkgParam, HookClassName.JAVA_IO_OUTPUTSTREAMWRITER, "write",
                ByteArray::class.java, Integer.TYPE, Integer.TYPE, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val os = param?.thisObject as OutputStream
                        if (!os.toString().contains("internal.http"))
                            return
                        val print = String(param.args[0] as ByteArray)
                        val pt = Pattern.compile("(\\w+=.*)")
                        val match = pt.matcher(print)
                        if (match.matches()) {
                            XposedBridge.log("$tag outputStream POST DATA: $print")
                        }
                    }
                })
        }

    }

    private fun hookGetStream(loadPkgParam: XC_LoadPackage.LoadPackageParam) {


        try {

            val requestHook = object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {
                    val urlConn = param.thisObject as HttpURLConnection

                    val sb = StringBuilder()
                    val isconnected = getObjectField(param.thisObject, "connected") as Boolean
                    if (!isconnected) {   //header của http request
                        val properties = urlConn.requestProperties

                        if (properties != null && properties.size > 0) {
                            for ((key, value) in properties) {
                                sb.append("$key:$value\n")
                            }
                        }
                        XposedBridge.log(
                            "$tag HttpRequest REQUEST: \nmethod=" + urlConn.requestMethod + " \nURL=" + urlConn.url.toString()
                                    + "\nHeader : " + sb.toString()
                        )
                    }
                }

            }

            val responseHook = object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {
                    val urlconn = param.thisObject as HttpURLConnection

                    val code = urlconn.responseCode
                    if (code == 200) {//nếu reponse code =200 (status :ok) thì in ra thông tin url,method,header
                        val properties = urlconn.headerFields

                        val sb = StringBuilder()
                        if (properties != null && properties.size > 0) {
                            for ((key, value) in properties) {
                                sb.append("$key:$value\n")
                            }
                            XposedBridge.log(
                                "$tag httpResponse RESPONSE: method :" + urlconn.requestMethod + "\nURL:" + urlconn.url.toString() + "\nheader: " + sb.toString()
                            )
                        }
                    }
                }
            }


            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                HookUtil().run {
                    hookMethod(
                        loadPkgParam, "libcore.net.http.HttpURLConnectionImpl",
                        "getOutputStream", requestHook
                    )

                    hookMethod(
                        loadPkgParam, "libcore.net.http.HttpURLConnectionImpl",
                        "getInputStream", responseHook
                    )
                }


            } else {
                HookUtil().run {
                    hookMethod(
                        loadPkgParam, "com.android.okhttp.internal.http.HttpURLConnectionImpl",
                        "getOutputStream", requestHook
                    )

                    hookMethod(
                        loadPkgParam, "com.android.okhttp.internal.http.HttpURLConnectionImpl",
                        "getInputStream", responseHook
                    )
                }


            }
        } catch (e: Error) {
        }

    }
}