package com.proxy.droid.bean;

import org.proxydroid.ProxyedApp;

import java.util.List;

/**
 * Description:代理配置参数
 * Created by Quinin on 2019-10-25.
 **/
public class ProxyParamsBean {
    /**
     * ed.putString("profileName", name);
     * ed.putString("host", host);
     * ed.putString("port", Integer.toString(port));
     * ed.putString("bypassAddrs", bypassAddrs);
     * ed.putString("Proxyed", proxyedApps);
     * ed.putString("user", user);
     * ed.putString("password", password);
     * ed.putBoolean("isAuth", isAuth);
     * ed.putBoolean("isNTLM", isNTLM);
     * ed.putString("domain", domain);
     * ed.putString("proxyType", proxyType);
     * ed.putString("certificate", certificate);
     * ed.putBoolean("isAutoConnect", isAutoConnect);
     * ed.putBoolean("isAutoSetProxy", isAutoSetProxy);
     * ed.putBoolean("isBypassApps", isBypassApps);
     * ed.putBoolean("isPAC", isPAC);
     * ed.putBoolean("isDNSProxy", isDNSProxy);
     * ed.putString("ssid", ssid);
     * ed.putString("excludedSsid", excludedSsid);
     */
    private String profileName; //配置文件名

    private String host;    //代理服务器的host，必须配置

    private String port;    //代理服务器的端口，必须配置

    private String Proxyed; //需要代理的应用，默认为空

    private String proxyType;   //代理协议类型：http,https,http-tunnel,socks4,socks5

    private boolean isAuth; //是否需要输入用户名和密码进行校验,当为true时需要输入用户名和密码

    private String user;    //用于和代理服务器校验的用户名，可选

    private String password;    //用于和代理服务器校验的密码，可选

    private boolean isDNSProxy; //是否勾选DNS代理

    private String bypassAddrs; //过滤的地址,默认为空

    private boolean isNTLM; //是否需要NTML/NTML2校验，默认为false

    private String domain;  //当isNTLM为true时需要输入域名

    private String certificate; //当isNTLM为true时需要输入

    private boolean isAutoConnect;   //是否通过Pac自动设置代理

    private boolean isAutoSetProxy; //是否设置全局代理

    private boolean isBypassApps;   //是否过滤某些应用不走代理

    private boolean isPAC;  //是否通过Pac配置代理,默认为false

    private String ssid;    //当isAutoConnect为true时需要输入

    private String excludedSsid;    //当isAutoConnect为true时需要输入

    private List<ProxyedApp> proxyedApps; //代理APP

    private List<ProxyedApp> ignoreProxyApps;   //不需要代理的APP


    public List<ProxyedApp> getProxyedApps() {
        return proxyedApps;
    }

    public void setProxyedApps(List<ProxyedApp> proxyedApps) {
        this.proxyedApps = proxyedApps;
    }


    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProxyed() {
        return Proxyed;
    }

    public void setProxyed(String proxyed) {
        Proxyed = proxyed;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDNSProxy() {
        return isDNSProxy;
    }

    public void setDNSProxy(boolean DNSProxy) {
        isDNSProxy = DNSProxy;
    }

    public String getBypassAddrs() {
        return bypassAddrs;
    }

    public void setBypassAddrs(String bypassAddrs) {
        this.bypassAddrs = bypassAddrs;
    }

    public boolean isNTLM() {
        return isNTLM;
    }

    public void setNTLM(boolean NTLM) {
        isNTLM = NTLM;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean isAutoConnect() {
        return isAutoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        isAutoConnect = autoConnect;
    }

    public boolean isAutoSetProxy() {
        return isAutoSetProxy;
    }

    public void setAutoSetProxy(boolean autoSetProxy) {
        isAutoSetProxy = autoSetProxy;
    }

    public boolean isBypassApps() {
        return isBypassApps;
    }

    public void setBypassApps(boolean bypassApps) {
        isBypassApps = bypassApps;
    }

    public boolean isPAC() {
        return isPAC;
    }

    public void setPAC(boolean PAC) {
        isPAC = PAC;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getExcludedSsid() {
        return excludedSsid;
    }

    public void setExcludedSsid(String excludedSsid) {
        this.excludedSsid = excludedSsid;
    }

    @Override
    public String toString() {
        return "ProxyParamsBean{" +
                "profileName='" + profileName + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", Proxyed='" + Proxyed + '\'' +
                ", proxyType='" + proxyType + '\'' +
                ", isAuth=" + isAuth +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", isDNSProxy=" + isDNSProxy +
                ", bypassAddrs='" + bypassAddrs + '\'' +
                ", isNTLM=" + isNTLM +
                ", domain='" + domain + '\'' +
                ", certificate='" + certificate + '\'' +
                ", isAutoConnect=" + isAutoConnect +
                ", isAutoSetProxy=" + isAutoSetProxy +
                ", isBypassApps=" + isBypassApps +
                ", isPAC=" + isPAC +
                ", ssid='" + ssid + '\'' +
                ", excludedSsid='" + excludedSsid + '\'' +
                '}';
    }

    public List<ProxyedApp> getIgnoreProxyApps() {
        return ignoreProxyApps;
    }

    public void setIgnoreProxyApps(List<ProxyedApp> ignoreProxyApps) {
        this.ignoreProxyApps = ignoreProxyApps;
    }
}
