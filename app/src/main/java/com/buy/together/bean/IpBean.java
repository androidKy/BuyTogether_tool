package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class IpBean {
    /**
     * ip_id : 234
     * city : GuangZhou
     * content : 192.168.2.104
     * mac_address : 0000:0000:0001
     */

    private int ip_id;
    private String city;
    private String content;
    private String mac_address;

    public int getIp_id() {
        return ip_id;
    }

    public void setIp_id(int ip_id) {
        this.ip_id = ip_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    @Override
    public String toString() {
        return "IpBean{" +
                "ip_id=" + ip_id +
                ", city='" + city + '\'' +
                ", content='" + content + '\'' +
                ", mac_address='" + mac_address + '\'' +
                '}';
    }
}
