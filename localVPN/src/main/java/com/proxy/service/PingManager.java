package com.proxy.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.proxy.service.bean.ConnectingBean;
import com.proxy.service.utils.ILoggerListener;
import com.proxy.service.utils.LoggerUtils;
import com.safframework.log.L;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * @author ：枕套
 * Created On ： 2019-05-22 23:30
 * Description ：判断是否无法链接网络，或者网络断开了
 * <p>
 * 问题：链接了VPN之后Ping都是返回错误的，原因是协议不支持（Ping 是ICMP协议 不是UDP/TCP协议）
 */
public class PingManager implements ILoggerListener {
    private static PingManager instance;

    public static PingManager getInstance() {
        synchronized (PingManager.class) {
            if (instance == null) {
                instance = new PingManager();
            }
        }
        return instance;
    }

    @Deprecated
    public String Ping(String ip) {
        log("Ping", "ip:" + ip);
        String result = "";
        Process p;
        try {
            String ping = "ping -c 3 -w 1 " + ip;
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec(ping);

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            log("Ping", "Result:" + buffer.toString());

            int status = p.waitFor();
            log("Ping", "status:" + status);
            if (status == 0) {
                result = "Success";
            } else {
                result = "Failed";
            }
        } catch (Exception e) {
            error(e, "Ping");
        }
        return result;
    }

    public ConnectingBean getConnecting(String requestUrl) {
        log("getConnecting", "requestUrl:" + requestUrl);
        ConnectingBean bean = new ConnectingBean("127.0.0.1");
        try {
            Document document = Jsoup.connect(requestUrl).get();
            log("getConnecting", "document:" + document);
            String title = document.title();
            log("getConnecting", "title:" + title);
            String ip = title.split(":")[1];
            log("getConnecting", "ip:" + ip);
            bean.setIp(ip);
        } catch (Exception e) {
            error(e, "getConnecting");
        }
        return bean;
    }

    /**
     * 获取外网链接信息，IP和城市区域
     */
    public ConnectingBean getConnectingInfo(String requestUrl) {
        log("getConnectingInfo", "requestUrl:" + requestUrl);
        ConnectingBean bean = new ConnectingBean("127.0.0.1");

        BufferedReader buff = null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10 * 1000);//读取超时
            urlConnection.setConnectTimeout(10 * 1000);//链接超时
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            int responseCode = urlConnection.getResponseCode();
            L.i("responseCode: " + responseCode);
            log("getConnectingInfo", "responseCode:" + responseCode);
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                is = urlConnection.getInputStream();
                buff = new BufferedReader(new InputStreamReader(is, "GBK"));//注意编码，会出现乱码
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = buff.readLine()) != null) {
                    builder.append(line);
                }
                log("getConnectingInfo", "builder:" + builder.toString());
                buff.close();//内部会关闭 InputStream
                urlConnection.disconnect();

                int bodyStart = builder.indexOf("<body");
                int bodyEnd = builder.indexOf("</body>");
                String body = builder.substring(bodyStart, bodyEnd);
                log("getConnectingInfo", "body:" + body);

                int start = builder.indexOf("<center>");
                int end = builder.indexOf("</center>");
                String content = builder.substring(start + "<center>".length(), end);
                log("getConnectingInfo", "content:" + content);

                start = content.indexOf("[");
                end = content.indexOf("]");
                String ip = content.substring(start + 1, end);
                log("getConnectingInfo", "ip:" + ip);
                bean.setIp(ip);

                start = content.indexOf("来");
                String area = content.substring(start + 3).split(" ")[0];
                log("getConnectingInfo", "area:" + area);
                JSONObject object = new JSONObject();
                String province = null, city = null;
                if (area.contains("省")) {
                    province = area.substring(0, area.indexOf("省") + 1);
                }
                if (area.contains("市")) {
                    city = area.substring(area.indexOf("省") + 1, area.indexOf("市") + 1);
                }
                object.put("province", province);
                object.put("city", city);
                log("getConnectingInfo", "object:" + object.toString());
                bean.setArea(area);
            }
        } catch (Exception e) {
            error(e, "getConnectingInfo");
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception e) {
                error(e, "getConnectingInfo--finally");
            }
        }
        return bean;
    }

    /**
     * 判断当前链接的网络是否可用
     */
    @Deprecated
    public boolean isConnecting(Context context) {
        boolean isConnecting = false;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://2019.ip138.com/ic.asp");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);//读取超时
            urlConnection.setConnectTimeout(5000);//链接超时
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            int code = urlConnection.getResponseCode();
            log("isConnecting", "code:" + code);
            if (code == HttpURLConnection.HTTP_OK && isNetworkAvailable(context)) {
                isConnecting = true;
            }
        } catch (Exception e) {
            error(e, "connect");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return isConnecting;
    }

    /**
     * 传入需要链接的IP，返回是否链接成功
     */
    public boolean isReachable(String remoteInetAddr) {
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(remoteInetAddr);
            reachable = address.isReachable(1500);
        } catch (Exception e) {
            error(e, "isReachable");
        }
        return reachable;
    }

    public boolean isConnecting() {
        boolean isConnecting = false;
        try {
            InetAddress address = InetAddress.getByName("baidu.com");
            isConnecting = address.isReachable(5000);
        } catch (Exception e) {
            error(e, "isConnecting");
        }
        return isConnecting;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        boolean isAvailable = true;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                isAvailable = false;
            } else {
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();
                if (networkinfo == null || !networkinfo.isAvailable()) {
                    isAvailable = false;
                }
            }
        } catch (Exception e) {
            error(e, "isNetworkAvailable");
        }
        return isAvailable;
    }

    /**
     * 获取外网IP
     */
    @Deprecated
    private String getConnectingInfo(Context context) {
        String ip = "";
        try {
            int index = 0;
            ip = getOutNetIP(context, index);
            while (ip.equals("") || ip.equals("127.0.0.1") && index < 3) {
                index++;
                ip = getOutNetIP(context, index);
                log("getConnectingInfo", "index:" + index + " , ip:" + ip);
            }
            log("getConnectingInfo", "ip:" + ip);
            if (TextUtils.isEmpty(ip) || ip.equals("127.0.0.1")) {
                String line = "";
                URL infoUrl = null;
                InputStream inStream = null;
                try {
                    infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
                    URLConnection connection = infoUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
                        StringBuilder strber = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            strber.append(line).append("\n");
                        }
                        inStream.close();
                        // 从反馈的结果中提取出IP地址
                        int start = strber.indexOf("{");
                        int end = strber.indexOf("}");
                        String json = strber.substring(start, end + 1);
                        if (json != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                line = jsonObject.optString("cip");
                            } catch (Exception e) {
                                error(e, "");
                            }
                        }
                    }
                } catch (Exception e) {
                    error(e, "");
                } finally {
                    if (line != null && !line.equals("") && !line.equals("127.0.0.1")) {
                        ip = line;
                    }
                }
            }
        } catch (Exception e) {
            error(e, "getConnectingInfo");
        }
        return ip;
    }

    @Deprecated
    private String[] platforms = {
            "http://2019.ip138.com/ic.asp",
            "http://pv.sohu.com/cityjson",
            "http://pv.sohu.com/cityjson?ie=utf-8",
            "http://ip.chinaz.com/getip.aspx"
    };

    @Deprecated
    public String getOutNetIP(Context context, int index) {
        if (index < platforms.length) {
            BufferedReader buff = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(platforms[index]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);//读取超时
                urlConnection.setConnectTimeout(5000);//链接超时
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                    InputStream is = urlConnection.getInputStream();
                    buff = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));//注意编码，会出现乱码
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        builder.append(line);
                    }
                    log("getOutNetIP", "builder:" + builder.toString());
                    buff.close();//内部会关闭 InputStream
                    urlConnection.disconnect();


                    if (index == 0 || index == 1) {
                        //截取字符串
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);//包含[satrtIndex,endIndex)
                        JSONObject jo = new JSONObject(json);
                        String ip = jo.getString("cip");

                        return ip;
                    } else if (index == 2) {
                        JSONObject jo = new JSONObject(builder.toString());
                        return jo.getString("ip");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return getInNetIp(context);
        }
        return getOutNetIP(context, ++index);
    }

    @Deprecated
    public String getInNetIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    /**
     * 这段是转换成点分式IP的码
     */
    @Deprecated
    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    @Override
    public void log(String method, Object object) {
        LoggerUtils.getInstance().setTag(getClass().getSimpleName()).log(method, object);
    }

    @Override
    public void warn(String method, String warningMsg) {
        LoggerUtils.getInstance().setTag(getClass().getSimpleName()).warn(method, warningMsg);
    }

    @Override
    public void error(Throwable throwable, String method) {
        LoggerUtils.getInstance().setTag(getClass().getSimpleName()).error(throwable, method);
    }
}
