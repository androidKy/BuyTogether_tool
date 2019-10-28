package com.proxy.droid.bean;

import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-07-19.
 **/
public class ProxyIPBean {

    /**
     * code : 0
     * msg :
     * data : {"port":[28919],"left_time":999999999999999,"domain":"125.88.158.218","code":200,"authuser":"yAPwF","number":1,"left_ip":953,"authpass":"iehLSayl","getIpUrl":"http://200019.ip138.com/"}
     */

    private int code;
    private String msg;
    private ProxyIp data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ProxyIp getData() {
        return data;
    }

    public void setData(ProxyIp data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProxyIPBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ProxyIp {
        /**
         * port : [28919]
         * left_time : 999999999999999
         * domain : 125.88.158.218
         * code : 200
         * authuser : yAPwF
         * number : 1
         * left_ip : 953
         * authpass : iehLSayl
         * getIpUrl : http://200019.ip138.com/
         */

        private long left_time;
        private String domain;
        private int code;
        private String authuser;
        private int number;
        private int left_ip;
        private String authpass;
        private String getIpUrl;
        private List<Integer> port;
        private List<String> hid;
        private List<String> dip;



        public long getLeft_time() {
            return left_time;
        }

        public void setLeft_time(long left_time) {
            this.left_time = left_time;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getAuthuser() {
            return authuser;
        }

        public void setAuthuser(String authuser) {
            this.authuser = authuser;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getLeft_ip() {
            return left_ip;
        }

        public void setLeft_ip(int left_ip) {
            this.left_ip = left_ip;
        }

        public String getAuthpass() {
            return authpass;
        }

        public void setAuthpass(String authpass) {
            this.authpass = authpass;
        }

        public String getGetIpUrl() {
            return getIpUrl;
        }

        public void setGetIpUrl(String getIpUrl) {
            this.getIpUrl = getIpUrl;
        }

        public List<Integer> getPort() {
            return port;
        }

        public void setPort(List<Integer> port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "ProxyIp{" +
                    "left_time=" + left_time +
                    ", domain='" + domain + '\'' +
                    ", code=" + code +
                    ", authuser='" + authuser + '\'' +
                    ", number=" + number +
                    ", left_ip=" + left_ip +
                    ", authpass='" + authpass + '\'' +
                    ", getIpUrl='" + getIpUrl + '\'' +
                    ", port=" + port +
                    '}';
        }

        public List<String> getHid() {
            return hid;
        }

        public void setHid(List<String> hid) {
            this.hid = hid;
        }

        public List<String> getDip() {
            return dip;
        }

        public void setDip(List<String> dip) {
            this.dip = dip;
        }
    }
}
