package com.proxy.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.safframework.log.L;
import com.utils.common.ToastUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE;

/**
 * Description:VPN管理类
 * Created by Quinin on 2019-07-26.
 **/
public class LocalVpnManager {


    private LocalVpnManager() {

    }

    private static LocalVpnManager mInstance;

    public static LocalVpnManager getInstance() {
        if (mInstance == null) {
            synchronized (LocalVpnManager.class) {
                if (mInstance == null)
                    mInstance = new LocalVpnManager();
            }

        }
        return mInstance;
    }

    private boolean mInitProxyData = false;

    public void initData(Context context, String authUser, String authPsw, String domain, String port) {
        String proxyDataUrl = "http://(" + authUser + ":" + authPsw + ")@" + domain + ":" + port;
        L.i("save proxyUrl: " + proxyDataUrl);

        LocalVpnService.ProxyUrl = proxyDataUrl;

        context.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE)
                .edit()
                .putString("CONFIG_URL_KEY", proxyDataUrl)
                .apply();

        mInitProxyData = true;
    }

    public void startVpnService(Activity activity) {
        if (!mInitProxyData) {
            ToastUtils.Companion.showToast(activity, "未设置代理数据");
            return;
        }

        stopVpnService(activity);

        LocalVpnService.setAcitivity(activity);

        Intent intent = LocalVpnService.prepare(activity);
        if (intent == null) {
            L.i("LocalVpnService prepare result is null.");
            Intent localIntent = new Intent(activity, LocalVpnService.class);
            activity.startService(localIntent);
        } else {
            L.i("LocalVpnService prepare result is not null.");
            activity.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
        }
    }


    public void stopVpnService(Activity activity) {
        LocalVpnService localVpnService = LocalVpnService.mInstance;
        if (localVpnService != null)
            localVpnService.dispose();
    }
}
