package com.vm.shadowsocks.bean;

/**
 * @author ：枕套
 * Created On ： 2019-05-27 11:03
 * Description ：
 */
public class ConnectingBean {
    String ip;
    String area;

    public ConnectingBean() {
    }

    public ConnectingBean(String ip) {
        this.ip = ip;
    }

    public ConnectingBean(String ip, String area) {
        this.ip = ip;
        this.area = area;
    }

    @Override
    public String toString() {
        return "ConnectingBean{" +
                "ip='" + ip + '\'' +
                ", area='" + area + '\'' +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
