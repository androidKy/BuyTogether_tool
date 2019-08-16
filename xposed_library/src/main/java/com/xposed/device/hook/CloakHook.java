package com.xposed.device.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * description:防止第三方应用检测xposed
 * author: kyXiao
 * date: 2019/4/11
 */
public class CloakHook implements HookListener {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        hideXposedException(loadPackageParam);
        hideXposedClass(loadPackageParam);
        hideXposedShell(loadPackageParam);
        hideXposedNative(loadPackageParam);
        hideXposedSystemProperty(loadPackageParam);
    }

    /**
     * 防止通过已安装的包来检测xposed
     *
     * @param loadPackageParam
     */
    private void hideXposedInstalledInfo(XC_LoadPackage.LoadPackageParam loadPackageParam) {

      /*  XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader,
                "getInstalledPackages", Integer.TYPE, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam methodHookParam) {
                        List localList = (List) methodHookParam.getResult();
                        Iterator localIterator = localList.iterator();
                        while (localIterator.hasNext()) {
                            if (((PackageInfo) localIterator.next()).packageName.equals("de.robv.android.xposed.installer")) {
                                XposedBridge.log("CloakHook install was already hided by hooking getInstalledPackages");
                                localIterator.remove();
                            }
                        }
                        methodHookParam.setResult(localList);
                    }
                });

        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader,
                "getInstalledApplications", Integer.TYPE, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam methodHookParam) {
                        List localList = (List) methodHookParam.getResult();
                        Iterator localIterator = localList.iterator();
                        while (localIterator.hasNext()) {
                            if (((ApplicationInfo) localIterator.next()).packageName.equals("de.robv.android.xposed.installer")) {
                                XposedBridge.log("CloakHook install was already hided by hooking getInstalledApplications");
                                localIterator.remove();
                            }
                        }
                        methodHookParam.setResult(localList);
                    }
                });*/
    }

    /**
     * 防止通过异常来检测xposed
     *
     * @param loadPackageParam
     */
    private void hideXposedException(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        new HookUtil().hookMethod(loadPackageParam, HookClassName.JAVA_LANG_THROWABLE, "getStackTrace",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            StackTraceElement[] stackTraceElements = (StackTraceElement[]) param.getResult();
                            List<StackTraceElement> list = Arrays.asList(stackTraceElements);
                            List<StackTraceElement> resultList = new ArrayList<>(list); //不能直接用Arrays.asListf返回的list进行remove等操作，会抛java.lang.UnsupportedOperationException
                            Iterator<StackTraceElement> stackTraceIterator = resultList.iterator();
                            while (stackTraceIterator.hasNext()) {
                                StackTraceElement stackTraceElement = stackTraceIterator.next();
                                String className = stackTraceElement.getClassName();
                                if (className.contains("de.robv.android.xposed.") || "com.android.internal.os.ZygoteInit".equals(className)) {
                                    XposedBridge.log("CloakHook exception was already hided by hooking getStackTrace");
                                    stackTraceIterator.remove();
                                }
                            }
                            StackTraceElement[] changedDatas = new StackTraceElement[resultList.size()];
                            for (int i = 0; i < resultList.size(); i++) {
                                changedDatas[i] = resultList.get(i);
                            }
                            param.setResult(changedDatas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
       /* XposedHelpers.findAndHookMethod(StackTraceElement.class, "getClassName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = (String) param.getResult();
                if (result != null) {
                    if (result.contains("de.robv.android.xposed.") || result.contains("com.android.internal.os.ZygoteInit")) {
                        XposedBridge.log("CloakHook exception was already hided by hooking StackTraceElement getClassName");
                        param.setResult("");
                    }
                }
                super.beforeHookedMethod(param);
            }
        });*/
    }

    /**
     * 防止通过类加载来检测xposed
     *
     * @param loadPackageParam
     */
    private void hideXposedClass(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
       /* XposedBridge.hookAllMethods(ClassLoader.class, "loadClass", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String className = (String) param.args[0];
                if ("de.robv.android.xposed.XposedHelpers".equals(className) || "de.robv.android.xposed.XposedBridge".equals(className)) {

                    //throw new ClassCastException();

                }
                if (param.args != null && param.args[0] != null && param.args[0].toString().startsWith("de.robv.android.xposed.")) {
                    // 改成一个不存在的类
                    param.args[0] = "de.robv.android.xposed.hideTest";
                }
            }
        });*/
        new HookUtil().hookMethod(loadPackageParam, HookClassName.JAVA_LANG_CLASSLOADER, "loadClass", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args != null && param.args[0] != null && param.args[0].toString().contains("de.robv.android.xposed.")) {
                            XposedBridge.log("CloakHook class was already hided by hooking loadClass");
                            // 改成一个不存在的类
                            param.args[0] = "de.robv.android.xposed.hideTest";
                        }
                    }
                });
    }

    /**
     * 防止通过读取 shell 命令 /proc/pid（应用进程id）/maps 可以拿到当前上下文的so和jar列表，查找Xposed
     *
     * @param loadPackageParam
     */
    private void hideXposedShell(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        new HookUtil().hookMethod(loadPackageParam, HookClassName.JAVA_IO_BUFFEREDREADER, "readLine", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = (String) param.getResult();
                if (result != null) {
                    if (result.contains("XposedBridge")) {
                        XposedBridge.log("CloakHook shell was already hided by hooking BufferedReader readLine");
                        param.setResult("");
                        //new File("").lastModified();
                    }
                }
            }
        });
    }

    /**
     * 由于Xposed的hook，是通过so修改被hook的方法为native来实现的，所以检测方也可以通过检测方法是否变成了native来达到检测的目的
     * 处理方式
     */
    private void hideXposedNative(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        new HookUtil().hookMethod(loadPackageParam, HookClassName.JAVA_LANG_REFLECT_MODIFIER, "isNative", Integer.TYPE,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("CloakHook native was already hided by hooking Modifier isNative");
                        param.setResult(false);
                    }
                });
    }

    private void hideXposedSystemProperty(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
       new HookUtil().hookMethod(loadPackageParam, HookClassName.JAVA_LANG_SYSTEM, "getProperty", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String arg = (String) param.args[0];

                        if (arg != null && arg.contains("vxp")) {
                            param.args[0] = "";
                            XposedBridge.log("CloakHook systemProperty was already hided by hooking System getProperty");
                        }
                    }
                });
    }

    static void getStackTrace(XC_MethodHook.MethodHookParam param) {
        StackTraceElement[] stackTraceElements = (StackTraceElement[]) param.getResult();
        List<StackTraceElement> list = Arrays.asList(stackTraceElements);
        List<StackTraceElement> resultList = new ArrayList<>(list); //不能直接用Arrays.asListf返回的list进行remove等操作，会抛java.lang.UnsupportedOperationException
        Iterator<StackTraceElement> stackTraceIterator = resultList.iterator();
        while (stackTraceIterator.hasNext()) {
            StackTraceElement stackTraceElement = stackTraceIterator.next();
            String className = stackTraceElement.getClassName();
            if (className.contains("de.robv.android.xposed.") || "com.android.internal.os.ZygoteInit".equals(className)) {
                XposedBridge.log("CloakHook exception was already hided by hooking getStackTrace");
                stackTraceIterator.remove();
            }
        }
        StackTraceElement[] changedDatas = new StackTraceElement[resultList.size()];
        for (int i = 0; i < resultList.size(); i++) {
            changedDatas[i] = resultList.get(i);
        }
        param.setResult(changedDatas);
    }
}
