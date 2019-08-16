package com.proxy.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.safframework.log.L;
import com.utils.common.ToastUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.proxy.service.LocalVpnService.START_VPN_SERVICE_REQUEST_CODE;

/**
 * Description:VPN管理类
 * Created by Quinin on 2019-07-26.
 **/
public class LocalVpnManager {


    private Intent mLocalIntent;

    private LocalVpnManager() {
        L.i(this.getClass().getSimpleName());
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
            L.i("未初始化VPN数据");
            ToastUtils.Companion.showToast(activity, "未设置代理数据");
            return;
        }
        L.i("开始启动VPN服务");

        LocalVpnService.setAcitivity(activity);

        //通过广播发送事件
        Intent intent = LocalVpnService.prepare(activity);
        if (intent == null) {
            L.i("LocalVpnService prepare result is null.");
            mLocalIntent = new Intent(activity, LocalVpnService.class);
            activity.startService(mLocalIntent);
        } else {
            L.i("LocalVpnService prepare result is not null.");
            activity.startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
        }
    }

    public void stopVpnService(final Activity activity) {
        if (activity != null && mLocalIntent != null) {
            activity.sendBroadcast(new Intent(LocalVpnService.BROADCAST_STOP_VPN));

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.stopService(mLocalIntent);
                }
            }, 500);

        }
    }
}
