package com.vm.shadowsocks.bean;

import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-07-22.
 **/
public class CloseProxyBean {

    /**
     * code : 0
     * msg :
     * data : {"usable":1013,"code":200,"inuse":187,"domain":"125.88.158.218","port":["29150"],"left_ip":1013}
     */

    private int code;
    private String msg;
    private ResultBean data;

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

    public ResultBean getData() {
        return data;
    }

    public void setData(ResultBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CloseProxyBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ResultBean {
        /**
         * usable : 1013
         * code : 200
         * inuse : 187
         * domain : 125.88.158.218
         * port : ["29150"]
         * left_ip : 1013
         */

        private int usable;
        private int code;
        private int inuse;
        private String domain;
        private int left_ip;
        private List<String> port;

        public int getUsable() {
            return usable;
        }

        public void setUsable(int usable) {
            this.usable = usable;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getInuse() {
            return inuse;
        }

        public void setInuse(int inuse) {
            this.inuse = inuse;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public int getLeft_ip() {
            return left_ip;
        }

        public void setLeft_ip(int left_ip) {
            this.left_ip = left_ip;
        }

        public List<String> getPort() {
            return port;
        }

        public void setPort(List<String> port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "usable=" + usable +
                    ", code=" + code +
                    ", inuse=" + inuse +
                    ", domain='" + domain + '\'' +
                    ", left_ip=" + left_ip +
                    ", port=" + port +
                    '}';
        }
    }
}
