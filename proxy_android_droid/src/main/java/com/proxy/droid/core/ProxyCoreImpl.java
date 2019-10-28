package com.proxy.droid.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.proxy.droid.bean.ProxyParamsBean;
import com.safframework.log.L;
import com.utils.common.ThreadUtils;

import org.proxydroid.Profile;
import org.proxydroid.ProxyDroidService;
import org.proxydroid.db.DNSResponse;
import org.proxydroid.db.DatabaseHelper;
import org.proxydroid.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-10-25.
 **/
public class ProxyCoreImpl {
    private static final String TAG = "ProxyCoreImpl";

    public static final String ACTION_PROXY_START = "action.proxy.start";
    public static final String ACTION_PROXY_STOP = "action.proxy.stop";

    private static ProxyCoreImpl mInstance;
    private ProxyParamsBean mProxyParamsBean;
    private WeakReference<Context> mRfContext;
    // private Context mContext;
    private Profile mProfile = new Profile();
    private String mPackageName = "";
    private Intent mProxyServiceIntent;
    private boolean mInited = false;

    private BroadcastReceiver mProxyReceiver;

    public static ProxyCoreImpl getInstance() {
        if (mInstance == null) {
            synchronized (ProxyCoreImpl.class) {
                if (mInstance == null)
                    mInstance = new ProxyCoreImpl();
            }
        }

        return mInstance;
    }

    /**
     * 初始化配置
     *
     * @param context
     */
    void initService(Context context) {
        //mContext = context;
        mRfContext = new WeakReference<>(context);
        mPackageName = PreferenceManager.getDefaultSharedPreferences(context).getString(ProxyCore.KEY_PKG_NAME, "");

        initReceiver();
        initFile();
    }

    /**
     * 开始代理
     *
     * @param context
     * @param proxyParamsBean
     */
    void startProxy(Context context, ProxyParamsBean proxyParamsBean) {
        if (context == null || proxyParamsBean == null) {
            throw new IllegalArgumentException("One of Params is null!");
        }
        if (!mInited) {
            throw new IllegalStateException("proxy is not initFile!");
        }

        //mContext = context;
        mRfContext = new WeakReference<>(context);
        mProxyParamsBean = proxyParamsBean;

        saveConfig(context);

        startService();
    }


    /**
     * 停止代理
     */
    void stopProxy() {
        //if (!Utils.isWorking()) return;
        try {
            if (mProxyServiceIntent != null) {
                getContext().stopService(mProxyServiceIntent);
                deleteDNS();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReceiver() {
        if (mProxyReceiver != null) return;
        mProxyReceiver = new ProxyReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PROXY_START);
        intentFilter.addAction(ACTION_PROXY_STOP);
        getContext().registerReceiver(mProxyReceiver,intentFilter);
    }

    private void saveConfig(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        mProfile.setHost(mProxyParamsBean.getHost());
        mProfile.setPort(Integer.valueOf(mProxyParamsBean.getPort()));
        mProfile.setProxyType(mProxyParamsBean.getProxyType());
        mProfile.setAutoConnect(mProxyParamsBean.isAutoConnect());
        mProfile.setSsid(mProxyParamsBean.getSsid());
        mProfile.setExcludedSsid(mProxyParamsBean.getExcludedSsid());
        mProfile.setBypassAddrs(mProxyParamsBean.getBypassAddrs());
        mProfile.setAuth(mProxyParamsBean.isAuth());
        mProfile.setUser(mProxyParamsBean.getUser());
        mProfile.setPassword(mProxyParamsBean.getPassword());
        mProfile.setNTLM(mProxyParamsBean.isNTLM());
        mProfile.setCertificate(mProxyParamsBean.getCertificate());
        mProfile.setAutoSetProxy(mProxyParamsBean.isAutoSetProxy());
        mProfile.setBypassApps(mProxyParamsBean.isBypassApps());
        mProfile.setDNSProxy(mProxyParamsBean.isDNSProxy());
        mProfile.setPAC(mProxyParamsBean.isPAC());

        mProfile.setProfile(settings);
    }

    private void startService() {
        //开启服务之前需要初始化
        mProxyServiceIntent = new Intent(getContext(), ProxyDroidService.class);
        Bundle bundle = new Bundle();
        bundle.putString("host", mProfile.getHost());
        bundle.putString("user", mProfile.getUser());
        bundle.putString("bypassAddrs", mProfile.getBypassAddrs());
        bundle.putString("password", mProfile.getPassword());
        bundle.putString("domain", mProfile.getDomain());
        bundle.putString("certificate", mProfile.getCertificate());

        bundle.putString("proxyType", mProfile.getProxyType());
        bundle.putBoolean("isAutoSetProxy", mProfile.isAutoSetProxy());
        bundle.putBoolean("isBypassApps", mProfile.isBypassApps());
        bundle.putBoolean("isAuth", mProfile.isAuth());
        bundle.putBoolean("isNTLM", mProfile.isNTLM());
        bundle.putBoolean("isDNSProxy", mProfile.isDNSProxy());
        bundle.putBoolean("isPAC", mProfile.isPAC());

        bundle.putInt("port", mProfile.getPort());

        mProxyServiceIntent.putExtras(bundle);
        getContext().startService(mProxyServiceIntent);
    }

    private void initFile() {
        ThreadUtils.executeByCached(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                /*try {
                    // Try not to block activity
                    Thread.sleep(2000);
                } catch (InterruptedException ignore) {
                    // Nothing
                }*/
                //1、检查设备是否root
                if (!Utils.isRoot()) {
                    throw new IllegalStateException("Phone is not rooted!");
                }

                reset();

                return true;
            }

            @Override
            public void onSuccess(Boolean result) {
                mInited = true;
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }


    private void reset() {
        try {
            getContext().stopService(new Intent(getContext(), ProxyDroidService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyAssets();

        deleteDNS();

        Utils.runRootCommand(Utils.getIptables()
                + " -t nat -F OUTPUT\n"
                + "/data/data/" + mPackageName + "/"
                + "proxy.sh stop\n"
                + "kill -9 `cat /data/data/" + mPackageName + "/stunnel.pid`\n"
                + "kill -9 `cat /data/data/" + mPackageName + "/shrpx.pid`\n"
                + "kill -9 `cat /data/data/" + mPackageName + "/cntlm.pid`\n");

        Utils.runRootCommand("chmod 700 /data/data/" + mPackageName + "/iptables\n"
                + "chmod 700 /data/data/" + mPackageName + "/redsocks\n"
                + "chmod 700 /data/data/" + mPackageName + "/proxy.sh\n"
                + "chmod 700 /data/data/" + mPackageName + "/cntlm\n"
                + "chmod 700 /data/data/" + mPackageName + "/stunnel\n"
                + "chmod 700 /data/data/" + mPackageName + "/shrpx\n");
    }

    private void deleteDNS() {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
            Dao<DNSResponse, String> dnsCacheDao = helper.getDNSCacheDao();
            List<DNSResponse> list = dnsCacheDao.queryForAll();
            for (DNSResponse resp : list) {
                dnsCacheDao.delete(resp);
            }
        } catch (Exception e) {
            // Nothing
            L.e(e.getMessage(), e);
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getContext().getAssets();
        String[] files = null;
        try {
            if (Build.VERSION.SDK_INT >= 21)
                files = assetManager.list("api-16");
            else
                files = assetManager.list("");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        if (files != null) {
            for (String file : files) {
                InputStream in = null;
                OutputStream out = null;
                try {

                    if (Build.VERSION.SDK_INT >= 21)
                        in = assetManager.open("api-16/" + file);
                    else
                        in = assetManager.open(file);
                    out = new FileOutputStream("/data/data/" + mPackageName + "/" + file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    private Context getContext() {
        Context context = mRfContext.get();
        if (context == null) {
            throw new IllegalArgumentException("Context is null!");
        }
        return context;
    }

    private ProxyStatusListener mProxyStatusListener;

    ProxyCoreImpl setProxyStatusListener(ProxyStatusListener proxyStatusListener) {
        mProxyStatusListener = proxyStatusListener;

        return this;
    }

    private ProxyStatusListener getProxyStatusListener() {
        return mProxyStatusListener;
    }


    /**
     * 接收代理变化的广播
     */
    private class ProxyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null)
            {
                String action = intent.getAction();
                String msg = intent.getStringExtra("msg");
                if(action != null && action.equals(ACTION_PROXY_START))
                {
                    if(getProxyStatusListener() != null)
                    {
                        getProxyStatusListener().onProxyStatus(true,msg);
                    }
                }else if(action != null && action.equals(ACTION_PROXY_STOP))
                {
                    if(getProxyStatusListener() != null)
                    {
                        getProxyStatusListener().onProxyStatus(false,msg);
                    }
                }
            }
        }
    }
}
