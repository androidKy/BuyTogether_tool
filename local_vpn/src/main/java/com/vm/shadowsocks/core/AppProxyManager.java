package com.vm.shadowsocks.core;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class AppProxyManager {
    public static boolean isLollipopOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private static AppProxyManager mInstance;
    //private static final String PROXY_APPS = "PROXY_APPS";
    //private Context mContext;

    // private List<AppInfo> mlistAppInfo = new ArrayList<AppInfo>();
    private List<AppInfo> mProxyAppInfo = new ArrayList<AppInfo>();

    private AppProxyManager() {
        //this.mContext = context;
        // Instance = this;
        // readProxyAppsList();
    }

    public static AppProxyManager getInstance() {
        if (mInstance == null) {
            synchronized (AppProxyManager.class) {
                if (mInstance == null)
                    mInstance = new AppProxyManager();
            }
        }

        return mInstance;
    }

    public void setAppInfo(AppInfo appInfo) {
        if (mProxyAppInfo.contains(appInfo))
            return;
        mProxyAppInfo.add(appInfo);
    }

    /**
     * 设置需要代理的App集合
     *
     * @param appInfoList
     */
    public void setAppInfoList(List<AppInfo> appInfoList) {
        if (mProxyAppInfo.size() > 0)
            mProxyAppInfo.clear();
        mProxyAppInfo.addAll(appInfoList);
    }

    /**
     * 获取需要代理的APP集合
     *
     * @return
     */
    public List<AppInfo> getProxyAppList() {
        return mProxyAppInfo;
    }


   /* public void removeProxyApp(String pkg) {
        for (AppInfo app : this.mProxyAppInfo) {
            if (app.getPkgName().equals(pkg)) {
                mProxyAppInfo.remove(app);
                break;
            }
        }
        writeProxyAppsList();
    }

    public void addProxyApp(String pkg) {
        for (AppInfo app : this.mlistAppInfo) {
            if (app.getPkgName().equals(pkg)) {
                mProxyAppInfo.add(app);
                break;
            }
        }
        writeProxyAppsList();
    }

    public void addProxyApp(AppInfo appInfo) {
        mProxyAppInfo.add(appInfo);
    }

    public void saveProxyAppList() {
        writeProxyAppsList();
    }


    public boolean isAppProxy(String pkg) {
        for (AppInfo app : this.mProxyAppInfo) {
            if (app.getPkgName().equals(pkg)) {
                return true;
            }
        }
        return false;
    }

    private void readProxyAppsList() {
        SharedPreferences preferences = mContext.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        String tmpString = preferences.getString(PROXY_APPS, "");
        try {
            if (mProxyAppInfo != null) {
                mProxyAppInfo.clear();
            }
            if (tmpString.isEmpty()) {
                return;
            }
            JSONArray jsonArray = new JSONArray(tmpString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(object.getString("label"));
                appInfo.setPkgName(object.getString("pkg"));
                mProxyAppInfo.add(appInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeProxyAppsList() {
        SharedPreferences preferences = mContext.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE);
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < mProxyAppInfo.size(); i++) {
                JSONObject object = new JSONObject();
                AppInfo appInfo = mProxyAppInfo.get(i);
                object.put("label", appInfo.getAppLabel());
                object.put("pkg", appInfo.getPkgName());
                jsonArray.put(object);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PROXY_APPS, jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
