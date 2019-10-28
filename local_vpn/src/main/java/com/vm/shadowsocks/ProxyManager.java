package com.vm.shadowsocks;

import android.app.Activity;

import com.safframework.log.L;
import com.vm.shadowsocks.bean.ProxyIPBean;
import com.vm.shadowsocks.core.AppInfo;
import com.vm.shadowsocks.core.AppProxyManager;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.network.ProxyApiManager;
import com.vm.shadowsocks.network.ProxyDataListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Description:代理管理
 * Created by Quinin on 2019-08-16.
 **/
public class ProxyManager {
    public ProxyManager() {
        L.init(this.getClass().getSimpleName());
    }

    private ProxyDataListener mProxyDataListener;

    /**
     * 设置需要代理APP
     *
     * @param appInfoList
     * @return
     */
    public ProxyManager setProxyApps(List<AppInfo> appInfoList) {
        AppProxyManager.getInstance().setAppInfoList(appInfoList);
        return this;
    }

    /**
     * 设置代理是否连接成功的监听
     *
     * @param onStatusChangedListener
     * @return
     */
    public ProxyManager setProxyStatusListener(LocalVpnService.onStatusChangedListener onStatusChangedListener) {
        LocalVpnService.addOnStatusChangedListener(onStatusChangedListener);
        return this;
    }

    /**
     * 设置代理数据错误的监听
     *
     * @return
     */
    public ProxyManager setProxyDataListener(ProxyDataListener proxyDataListener) {
        mProxyDataListener = proxyDataListener;
        return this;
    }

    /**
     * 开启代理
     * 1、获取代理数据
     * 2、保存代理数据
     * 3、开始启动VPN
     */
    public void startProxy(final Activity activity, String cityName, String imei) {
        stopProxy(activity);
        new ProxyApiManager(cityName, imei)
                .requestPortByClosePort(new ProxyDataListener() {
                    @Override
                    public void onResponProxyData(@Nullable ProxyIPBean proxyIPBean) {
                        if (proxyIPBean == null) {
                            responError("请求获取代理数据失败");
                        } else {
                            responSucceed(proxyIPBean);
                            LocalVpnManager localVpnManager = LocalVpnManager.getInstance();
                            List<Integer> portList = proxyIPBean.getData().getPort();
                            int port = 0;
                            if (portList.size() > 0) {
                                port = portList.get(0);
                            }

                            localVpnManager.initData(activity, proxyIPBean.getData().getAuthuser(),
                                    proxyIPBean.getData().getAuthpass(), proxyIPBean.getData().getDomain(),
                                    String.valueOf(port)
                            );
                            localVpnManager.startVpnService(activity);
                        }
                    }

                    @Override
                    public void onFailed(@NotNull String failedMsg) {
                        responError(failedMsg);
                    }
                });
    }

    /**
     * 停止代理
     *
     * @param activity
     */
    public void stopProxy(Activity activity) {
        LocalVpnManager.getInstance().stopVpnService(activity);
    }


    private void responSucceed(ProxyIPBean proxyIPBean) {
        if (mProxyDataListener != null)
            mProxyDataListener.onResponProxyData(proxyIPBean);
    }

    private void responError(String errorMsg) {
        if (mProxyDataListener != null) {
            mProxyDataListener.onFailed(errorMsg);
        }
    }
}
