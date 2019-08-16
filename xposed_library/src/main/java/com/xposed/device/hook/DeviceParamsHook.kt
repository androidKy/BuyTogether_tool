package com.xposed.device.hook

import android.app.AndroidAppHelper
import android.text.TextUtils
import com.buy.together.hook.HookListener
import com.buy.together.hook.sp.DeviceParams
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Description:
 * Created by Quinin on 2019-07-22.
 **/
class DeviceParamsHook : HookListener {
    private val tag: String = DeviceParamsHook::class.java.simpleName

    override fun hook(loadPkgParam: XC_LoadPackage.LoadPackageParam) {
        HookUtil().run {
            hookMethod(
                loadPkgParam, HookClassName.ANDROID_TELEPHONY_TELEPHONYMANAGER, "getImei",
                PhoneCallBack(DeviceParams.IMEI_KEY, loadPkgParam.packageName)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_TELEPHONY_TELEPHONYMANAGER, "getDeviceId",
                PhoneCallBack(DeviceParams.IMEI_KEY, loadPkgParam.packageName)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_TELEPHONY_TELEPHONYMANAGER, "getSubscriberId",
                PhoneCallBack(DeviceParams.IMSI_KEY, loadPkgParam.packageName)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_BLUETOOTH_BLUETOOTHADAPTER, "getAddress",
                PhoneCallBack(DeviceParams.BLUTOOTH_KEY, loadPkgParam.packageName)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_BLUETOOTH_BLUETOOTHDEVICE, "getAddress",
                PhoneCallBack(DeviceParams.BLUTOOTH_KEY, loadPkgParam.packageName)
            )

            hookField(
                loadPkgParam,
                HookClassName.ANDROID_OS_BUILD,
                "BRAND",
                //HookUtil.getValueFromSP(AndroidAppHelper.currentApplication(), DeviceParams.BRAND_KEY)
                getValueFromSP(DeviceParams.BRAND_KEY)
            )
            hookField(
                loadPkgParam,
                HookClassName.ANDROID_OS_BUILD,
                "MODEL",
                //HookUtil.getValueFromSP(AndroidAppHelper.currentApplication(),DeviceParams.MODEL_KEY)
                getValueFromSP(DeviceParams.MODEL_KEY)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_OS_SYSTEMPROPERTIES, "get", String::class.java,
                SystemPropertiesCallBack(loadPkgParam.packageName)
            )

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_OS_SYSTEMPROPERTIES, "get", String::class.java, String::class.java,
                SystemPropertiesCallBack(loadPkgParam.packageName)
            )

            hookMethod(loadPkgParam, HookClassName.JAVA_LANG_SYSTEM, "getProperty", String::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        param?.apply {
                            args?.run {
                                if (this@run[0] == "http.agent") {
                                    //val value = HookUtil.getValueFromSP(AndroidAppHelper.currentApplication(),DeviceParams.USER_AGENT_KEY)
                                    val value = getValueFromSP(DeviceParams.USER_AGENT_KEY)
                                    if (!TextUtils.isEmpty(value))
                                        result = value
                                }
                            }
                        }
                    }
                })

            hookMethod(
                loadPkgParam, HookClassName.ANDROID_NET_WIFI_WIFIINFO, "getMacAddress",
                PhoneCallBack(DeviceParams.MAC_KEY, loadPkgParam.packageName)
            )
        }
    }


    inner class PhoneCallBack(private val key: String, private val pkgName: String) : XC_MethodHook() {

        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.run {
                val value = getValueFromSP(key)
                HookUtil().log("$pkgName 修改设备参数", "key: $key value: $value")
                if (!TextUtils.isEmpty(value)) {
                    result = value
                }
            }
        }
    }

    inner class SystemPropertiesCallBack(val pkgName: String) : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.run {
                val strArg0 = args?.get(0) as String
                strArg0.apply {
                    when (this) {
                        "ro.product.brand" -> setResult(DeviceParams.BRAND_KEY, this@run)
                        "ro.product.mode" -> setResult(DeviceParams.MODEL_KEY, this@run)
                    }
                }
            }
        }

        fun setResult(key: String, param: MethodHookParam) {
            val value = getValueFromSP(key)
            HookUtil().log("$pkgName 修改设备参数", "key: $key value: $value")
            if (!TextUtils.isEmpty(value))
                param.result = value
        }
    }

    private fun getValueFromSP(key: String): String {
        return HookUtil.getValueFromSP(AndroidAppHelper.currentApplication(), key)
    }
}