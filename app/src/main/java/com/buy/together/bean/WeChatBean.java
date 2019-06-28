package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class WeChatBean {
    /**
     * wechat_id : 23
     * name : 12345678901
     * psw : wechat_psw
     */

    private int wechat_id;
    private String name;
    private String psw;

    public int getWechat_id() {
        return wechat_id;
    }

    public void setWechat_id(int wechat_id) {
        this.wechat_id = wechat_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public String toString() {
        return "WeChatBean{" +
                "wechat_id=" + wechat_id +
                ", name='" + name + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }
}
