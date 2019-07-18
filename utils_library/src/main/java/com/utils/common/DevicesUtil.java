package com.utils.common;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.LocaleList;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * description:App相关参数获取工具类
 * author: kyXiao
 * date: 2019/2/27
 */
public final class DevicesUtil {

    // 串口序列号
    /*public static String getSerialNumber() {
        return SystemProperties.get("ro.serialno");
    }

    // 基带版本
    public static String getGetBaseband_Ver() {
        return SystemProperties.get("gsm.version.baseband");
    }*/

    // baseband 对应 getRadioVersion
    public static String getBaseBand() {
        return Build.getRadioVersion();
    }

    // 主板
    public static String getBoard() {
        return Build.BOARD;
    }

    // 设备品牌
    public static String getBrand() {
        return Build.BRAND;
    }

    // ABI
    public static String getABI() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Build.CPU_ABI;
        } else {
            return Build.SUPPORTED_ABIS[0];
        }
    }

    // ABI2
    public static String getABI2() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Build.CPU_ABI2;
        } else {
            return Build.SUPPORTED_ABIS[1];
        }
    }

    // device
    public static String getDevice() {
        return Build.DEVICE;
    }

    // display
    public static String getDisplay() {
        return Build.DISPLAY;
    }

    // fingerprint
    public static String getFingerprint() {
        return Build.FINGERPRINT;
    }

    // 硬件NAME
    public static String getNAME() {
        return Build.HARDWARE;
    }

    // 硬件id
    public static String getID() {
        return Build.ID;
    }

    // Manufacture
    public static String getManufacture() {
        return Build.MANUFACTURER;
    }

    // model
    public static String getModel() {
        return Build.MODEL;
    }

    // product
    public static String getProduct() {
        return Build.PRODUCT;
    }

    // bootloader
    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    // host
    public static String getHost() {
        return Build.HOST;
    }

    // build_tags
    public static String getBuildTags() {
        return Build.TAGS;
    }

    // shebei_type
    public static String getDeviceType() {
        return Build.TYPE;
    }

    // incremental
    public static String getIncremental() {
        return VERSION.INCREMENTAL;
    }

    // AndroidVer
    public static String getAndroidVer() {
        return VERSION.RELEASE;
    }

    // API
    public static String getSDKAPI() {
        return String.valueOf(VERSION.SDK_INT);
    }

    // band time
    public static int getBandTime() {
        return (int) Build.TIME;
    }

    // 获取android id
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return filterNull(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    // DESCRIPTION
   /* public static String getDESCRIPTION() {
        try {
            return filterNull(SystemProperties.get("ro.buildDeviceParams.description"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }*/

    // hhtpUA
    public static String getHttpUA() {
        return filterNull(System.getProperty("http.agent"));
    }

    // webUA
    public static String getWebUA(Context context) {
        WebView webView = new WebView(context);
        WebSettings webSettings = webView.getSettings();
        if (webSettings != null)
            return filterNull(webSettings.getUserAgentString());
        return "";
    }

    // BLUETOOTH_MAC
    public static String getLYMAC(Context context) {
        return filterNull(Settings.Secure.getString(context.getContentResolver(), "bluetooth_address"));
    }

    // wifi mac
    public static String getWifiMacAddr(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String macAddress = wifiManager.getConnectionInfo().getMacAddress();
        return filterNull(macAddress);
    }

    // wifi name
    public static String getWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiName = info != null ? info.getSSID() : null;
        return filterNull(wifiName);
    }

    // BSSID
    public static String getBSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String bssid = info != null ? info.getBSSID() : null;
        return filterNull(bssid);
    }

    // imsi
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String imsi = mTelephonyMgr.getSubscriberId();
        return TextUtils.isEmpty(imsi) ? "" : imsi;
    }

    // PhoneNumber
    public static String getPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String number = mTelephonyMgr.getLine1Number();
        return filterNull(number);
    }

    // SimSerial
    public static String getSimSerial(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getSimSerialNumber());
    }

    // operator
    public static String getOperator(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getSimOperator());
    }

    // operator_name
    public static String getOperName(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getSimOperatorName());
    }

    // operator_iso
    public static String getOperISO(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getSimCountryIso());
    }

    // carrier
    public static String getCarrier(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getNetworkOperator());
    }

    // carrier_name
    public static String getCarrierName(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getNetworkOperatorName());
    }

    // carrier_iso
    public static String getCarrierISO(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getNetworkCountryIso());
    }

    // deviceversion
    public static String getDeviceVersion(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getDeviceSoftwareVersion());
    }

    // getType
    public static int getGetType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info == null) {
                return -1;
            }
            return info.getType();
        }
        return -1;
    }

    // networkType
    public static int getNetworkType(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        return mTelephonyMgr.getNetworkType();
    }

    // phonetype
    public static int getPhoneType(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        return mTelephonyMgr.getPhoneType();
    }

    // SimState
    public static int getSimState(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        return mTelephonyMgr.getSimState();
    }

    // width
    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    // height
    public static int getHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    // getIP
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }


            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "";
    }

    // DPI
    public static int getDPI(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        return metrics.densityDpi;
    }

    // density
    public static String getDensity(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.density);
    }

    // xdpi
    public static String getXDPI(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.xdpi);
    }

    // ydpi
    public static String getYDPI(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.ydpi);
    }

    // scaledDensity
    public static String getScaledDensity(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return String.valueOf(metrics.scaledDensity);
    }

    // lat
    public static String getLatitude(Context context) {
        Location location = getLastKnownLocation(context);
        if (location != null) {
            return String.valueOf(location.getLatitude());
        } else {
            return "";
        }
    }

    // log
    public static String getLongitude(Context context) {
        Location location = getLastKnownLocation(context);
        if (location != null) {
            return String.valueOf(location.getLongitude());
        } else {
            return "";
        }
    }

    // fakeApps
   /* public static String getFakeApps(Context context) {
        SPUtil spUtil = new SPUtil(context.getApplicationContext());
        return filterNull(spUtil.getString(Common.Constants.FAKEAPPS));
    }

    // GAID
    public static String getGAID(Context context) {
        try {
            return filterNull(AdvertisingIdClient.getAdvertisingIdInfo(context).getId());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }*/


    public static Pattern compileEmailAddress() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "("
                + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    }

    // debug
    public static String getDebug(Context context) {
        boolean enableAdb = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
        return String.valueOf(enableAdb);
    }

    // lang
    public static String getLanguage() {
        Locale locale;
        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return filterNull(locale.getDisplayLanguage());
    }

    public static String getLangCode() {
        Locale locale;
        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return filterNull(locale.getLanguage());
    }

    // local
    public static String getLocal(Context context) {
        return filterNull(Locale.getDefault().getDisplayCountry());
    }

    public static String getLocalCode(Context context) {
        String ret = "";
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(ret)) {
            ret = Locale.getDefault().getCountry().toUpperCase();
        }
        return ret;
    }


    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr == null || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return filterNull(mTelephonyMgr.getDeviceId());
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 获取地理位置
     *
     * @param context
     * @return
     */
    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private static String filterNull(String value) {
        return TextUtils.isEmpty(value) ? "" : value;
    }
}
