package com.proxy.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.safframework.log.L;
import com.utils.common.ToastUtils;

import java.lang.ref.WeakReference;

import static android.content.Context.MODE_PRIVATE;
import static com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE;

/**
 * Description:VPN管理类
 * Created by Quinin on 2019-07-26.
 **/
public class LocalVpnManager {

    private static boolean mInitProxyData = false;
    private static WeakReference<Intent> mLocalIntentRef = null;

    public static void initData(Context context, String authUser, String authPsw, String domain, String port) {
        String proxyDataUrl = "http://(" + authUser + ":" + authPsw + ")@" + domain + ":" + port;
        L.i("save proxyUrl: " + proxyDataUrl);

        LocalVpnService.ProxyUrl = proxyDataUrl;

        context.getSharedPreferences("shadowsocksProxyUrl", MODE_PRIVATE)
                .edit()
                .putString("CONFIG_URL_KEY", proxyDataUrl)
                .apply();

        mInitProxyData = true;
    }

    public static void startVpnService(Activity activity) {
        if (!mInitProxyData) {
            ToastUtils.Companion.showToast(activity, "未设置代理数据");
            return;
        }

        stopVpnService(activity);

        LocalVpnService.setAcitivity(activity);

        Intent intent = LocalVpnService.prepare(activity);
        if (intent == null) {
            Intent localIntent = new Intent(activity, LocalVpnService.class);
            activity.startService(localIntent);

            if (mLocalIntentRef != null)
                mLocalIntentRef.clear();
            mLocalIntentRef = new WeakReference<Intent>(localIntent);
        } else activity.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
    }


    public static void stopVpnService(Activity activity) {
        if (mLocalIntentRef != null && mLocalIntentRef.get() != null) {
            activity.stopService(mLocalIntentRef.get());
            mLocalIntentRef.clear();
        }
    }
}
