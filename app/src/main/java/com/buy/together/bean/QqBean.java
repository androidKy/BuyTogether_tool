package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class QqBean {
    /**
     * qq_id : 24
     * name : qq_name
     * psw : qq_psw
     */

    private int qq_id;
    private String name;
    private String psw;

    public int getQq_id() {
        return qq_id;
    }

    public void setQq_id(int qq_id) {
        this.qq_id = qq_id;
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
        return "QqBean{" +
                "qq_id=" + qq_id +
                ", name='" + name + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }
}
