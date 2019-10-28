package com.proxy.droid.core;

import android.content.Context;
import android.preference.PreferenceManager;

import com.proxy.droid.bean.ProxyParamsBean;


import java.lang.ref.WeakReference;

/**
 * Description:代理管理类
 * Created by Quinin on 2019-10-25.
 **/
public class ProxyCore {

    public static final String KEY_PKG_NAME = "key_pkg_name";

    private ProxyParamsBean mProxyParamsBean;
    private WeakReference<Context> mRfContext;
    private ProxyStatusListener mProxyStatusListener;

    public ProxyCore(Context context) {
        mRfContext = new WeakReference<>(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ProxyCore.KEY_PKG_NAME, context.getPackageName()).commit();
    }

    public ProxyCore setProxyParams(ProxyParamsBean proxyParamsBean) {
        this.mProxyParamsBean = proxyParamsBean;
        return this;
    }

    public ProxyCore setProxyStatusListener(ProxyStatusListener proxyStatusListener) {
        mProxyStatusListener = proxyStatusListener;
        return this;
    }

    public void init() {
        ProxyCoreImpl.getInstance().initService(mRfContext.get());
    }

    public void startProxy() {
        ProxyCoreImpl.getInstance()
                .setProxyStatusListener(mProxyStatusListener)
                .startProxy(mRfContext.get(), mProxyParamsBean);
    }

    public void stopProxy() {
        ProxyCoreImpl.getInstance().stopProxy();
    }
}
