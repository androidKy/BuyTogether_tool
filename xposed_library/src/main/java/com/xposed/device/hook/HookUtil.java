package com.xposed.device.hook;

import android.content.Context;
import android.text.TextUtils;
import com.xposed.device.hook.sp.SharedPref;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Constructor;

/**
 * description:
 * author: kyXiao
 * date: 2019/4/26
 */
public class HookUtil {

    public void logParam(String tag, MethodHookParam methodHookParam) {
        if (methodHookParam != null) {
            Object[] args = methodHookParam.args;
            if (args != null) {
                log(tag, "params size = " + args.length);
                for (int i = 0; i < args.length; i++) {
                    Object obj = args[i];
                    if (obj == null) continue;

                    if (obj instanceof String) {
                        log(tag, "args[" + i + "] = " + obj.toString());
                    } else if (obj instanceof byte[]) {
                        byte[] bytes = (byte[]) obj;
                        log(tag, "args[" + i + "] = " + new String(bytes));
                    } else if (obj instanceof Integer) {
                        log(tag, "args[" + i + "] = " + obj);
                    } else if (obj instanceof Long) {
                        log(tag, "args[" + i + "] = " + obj);
                    } else {
                        log(tag, "object : " + obj.toString());
                    }
                }
            } else {
                log(tag, "args is null");
            }

        } else {
            log(tag, "MethodHookParam is null");
        }
    }

    public void log(String tag, String msg) {
        XposedBridge.log(tag + ": " + msg);
    }

    public static void logStatic(String tag, String msg) {
        XposedBridge.log(tag + ": " + msg);
    }

    public static String getValueFromSP(Context context, String key) {
        return SharedPref.getXValue(context, key);
    }

    public void hookMethod(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String methodName,
                           Object... parameterTypesAndCallback) {
        Class<?> classObj = XposedHelpers.findClassIfExists(className, loadPackageParam.classLoader);
        if (classObj != null) {
            try {
                XposedBridge.log("hookMethod packageName: " + loadPackageParam.packageName + " className: " + className
                        + " methodName: " + methodName);
                XposedHelpers.findAndHookMethod(classObj, methodName, parameterTypesAndCallback);
            } catch (Exception e) {
                XposedBridge.log(e);
            }
        } else {
            XposedBridge.log(className + " was not found in package: " + loadPackageParam.packageName);
        }
    }

    public void hookField(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String filedName,
                          String value) {
        Class<?> classObj = XposedHelpers.findClassIfExists(className, loadPackageParam.classLoader);
        if (classObj != null) {
            try {
                XposedBridge.log("hookField packageName: " + loadPackageParam.packageName + " className : " + className
                        + " filedName: " + filedName + " value: " + value);
                if (!TextUtils.isEmpty(value))
                    XposedHelpers.findField(classObj, filedName).set(null, value);
            } catch (Exception e) {
                XposedBridge.log(e);
            }
        } else {
            XposedBridge.log(className + " was not found in package: " + loadPackageParam.packageName);
        }
    }

    public void hookField(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String filedName,
                          long value) {
        Class<?> classObj = XposedHelpers.findClassIfExists(className, loadPackageParam.classLoader);
        if (classObj != null) {
            try {
                XposedBridge.log("hookField packageName: " + loadPackageParam.packageName + " className : " + className
                        + " filedName: " + filedName + " value: " + value);
                XposedHelpers.findField(classObj, filedName).set(null, value);
            } catch (Exception e) {
                XposedBridge.log(e);
            }
        } else {
            XposedBridge.log(className + " was not found in package: " + loadPackageParam.packageName);
        }
    }

    public void hookConstructor(XC_LoadPackage.LoadPackageParam loadPackageParam, XC_MethodHook xcMethodHook, Class<?> classZz,
                                Class<?>... parameterTypes) {
        try {
            XposedBridge.log("hookConstructor packageName: " + loadPackageParam.packageName + " className : " + classZz.getSimpleName());
            Constructor<?> constructor = XposedHelpers.findConstructorExact(classZz, parameterTypes);
            if (constructor != null)
                XposedBridge.hookMethod(constructor, xcMethodHook);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
