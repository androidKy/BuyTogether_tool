package com.proxy.droid;


import android.content.Context;

import com.proxy.droid.bean.ProxyIPBean;
import com.proxy.droid.bean.ProxyParamsBean;
import com.proxy.droid.core.ProxyCore;
import com.proxy.droid.core.ProxyStatusListener;
import com.proxy.droid.network.ProxyApiManager;
import com.proxy.droid.network.ProxyDataListener;
import com.safframework.log.L;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Description:代理管理类
 * Created by Quinin on 2019-10-26.
 **/
public class ProxyManager {
    private String imei;
    private String cityName;
    private List<String> proxyPkgNames;
    private ProxyStatusListener proxyStatusListener;
    private Context context;

    public static class Builder {
        private String imei;
        private String cityName;
        private List<String> proxyPkgNames;
        private ProxyStatusListener proxyStatusListener;
        private Context context;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setProxyStatusListener(ProxyStatusListener proxyStatusListener) {
            this.proxyStatusListener = proxyStatusListener;

            return this;
        }

        public String getImei() {
            return imei;
        }

        public Builder setImei(String imei) {
            this.imei = imei;
            return this;
        }

        public String getCityName() {
            return cityName;
        }

        public Builder setCityName(String cityName) {
            this.cityName = cityName;
            return this;
        }


        public Builder setProxyPkgNames(List<String> proxyPkgNames) {
            this.proxyPkgNames = proxyPkgNames;
            return this;
        }

        public ProxyManager build() {
            ProxyManager proxyManager = new ProxyManager();
            proxyManager.imei = imei;
            proxyManager.cityName = cityName;
            proxyManager.proxyPkgNames = proxyPkgNames;
            proxyManager.proxyStatusListener = proxyStatusListener;
            proxyManager.context = context;

            return proxyManager;
        }
    }

    /**
     * 必须先初始化
     */
    public void init() {
        new ProxyCore(context).init();
    }

    /**
     * 开启代理
     */
    public void startProxy() {
        new ProxyApiManager(cityName, imei).requestPortByClosePort(new ProxyDataListener() {
            @Override
            public void onFailed(@NotNull String failedMsg) {
                L.i("请求代理数据失败：" + failedMsg);
                proxyStatusListener.onProxyStatus(false,failedMsg);
            }

            @Override
            public void onResponProxyData(@Nullable ProxyIPBean proxyIPBean) {
                if (proxyIPBean != null && proxyIPBean.getData() != null) {
                    ProxyIPBean.ProxyIp proxyIp = proxyIPBean.getData();
                    ProxyParamsBean proxyParamsBean = new ProxyParamsBean();
                    proxyParamsBean.setAuth(true);
                    proxyParamsBean.setUser(proxyIp.getAuthuser());
                    proxyParamsBean.setPassword(proxyIp.getAuthpass());
                    proxyParamsBean.setPort(String.valueOf(proxyIp.getPort().get(0)));
                    proxyParamsBean.setHost(proxyIp.getDomain());
                    proxyParamsBean.setDNSProxy(true);  //默认开启DNS代理
                    proxyParamsBean.setProxyType("socks5");
                    proxyParamsBean.setAutoConnect(false);  //默认不自动连接

                    //是否开启全局代理
                    if (proxyParamsBean.getProxyedApps() != null && proxyParamsBean.getProxyedApps().size() > 0)
                        proxyParamsBean.setAutoSetProxy(false);
                    else proxyParamsBean.setAutoSetProxy(true);
                    //是否开启不代理的APP
                    if (proxyParamsBean.getIgnoreProxyApps() != null && proxyParamsBean.getIgnoreProxyApps().size() > 0)
                        proxyParamsBean.setBypassApps(true);
                    else proxyParamsBean.setBypassApps(false);


                    new ProxyCore(context).setProxyParams(proxyParamsBean)
                            .setProxyStatusListener(proxyStatusListener)
                            .startProxy();
                }

            }
        });
    }

    /**
     * 停止代理
     */
    public void stopProxy() {
        new ProxyCore(context)
                .setProxyStatusListener(proxyStatusListener)
                .stopProxy();
    }
}
