package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class BuyTogetherBean {
    /**
     * buttogether_id : 53
     * name : userName
     * psw : buyTogether_psw
     */

    private int buttogether_id;
    private String name;
    private String psw;

    public int getButtogether_id() {
        return buttogether_id;
    }

    public void setButtogether_id(int buttogether_id) {
        this.buttogether_id = buttogether_id;
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
        return "BuyTogetherBean{" +
                "buttogether_id=" + buttogether_id +
                ", name='" + name + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }
}
