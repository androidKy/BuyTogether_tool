package com.buy.together.hook;

import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
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

    static void log(String tag, String msg) {
        XposedBridge.log(tag + ": " + msg);
    }

    static void hookMethod(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String methodName,
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

    public static void hookField(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String filedName,
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

    public static void hookField(XC_LoadPackage.LoadPackageParam loadPackageParam, String className, String filedName,
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

    public static void hookConstructor(XC_LoadPackage.LoadPackageParam loadPackageParam, XC_MethodHook xcMethodHook, Class<?> classZz,
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
