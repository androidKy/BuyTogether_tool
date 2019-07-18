package com.utils.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * description: 包管理工具类
 * author:kyXiao
 * date:2019/3/15
 */
public class PackageManagerUtils {
    private volatile static PackageManagerUtils mInstance;

    private PackageManagerUtils() {
    }

    public static PackageManagerUtils getInstance() {
        if (mInstance == null) {
            synchronized (PackageManagerUtils.class) {
                if (mInstance == null) {
                    mInstance = new PackageManagerUtils();
                }
            }
        }
        return mInstance;
    }

    public void restartApplication(Context context) {
        // 关闭辅助点击
        // CMDUtil.execCmd("settings put secure accessibility_enabled 0");
        //CMDUtil.execCmd("am force-stop com.ky.ad;am start -n com.ky.ad/com.ky.ad.MainActivity");

//        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(Common.PACKAGE_NAME);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

//    public void forceStopApplication() {
//        // 关闭辅助点击
//        CMDUtil.execCmd("settings put secure accessibility_enabled 0");
//        CMDUtil.execCmd("am force-stop com.aiman.hardwarecode");
//    }

    public boolean isPackageAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName != null && pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isApkAvilible(Context context, String filePath) {
        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
