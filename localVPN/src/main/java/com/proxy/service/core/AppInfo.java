package com.proxy.service.core;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private Drawable appIcon;
    private String appLabel;
    private String pkgName;

    public AppInfo() {
    }

    @Override
    public String toString() {
        return "AppInfo{" + '\n' +
                "appIcon=" + appIcon + '\n' +
                ", appLabel='" + appLabel + '\n' +
                ", pkgName='" + pkgName + '\n' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AppInfo) {
            AppInfo appInfo = (AppInfo) object;
            return appInfo.pkgName.equals(pkgName);
        }
        return false;
    }


    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public String getAppLabel() {
        return this.appLabel;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setAppIcon(Drawable var1) {
        this.appIcon = var1;
    }

    public void setAppLabel(String var1) {
        this.appLabel = var1;
    }

    public void setPkgName(String var1) {
        this.pkgName = var1;
    }
}
