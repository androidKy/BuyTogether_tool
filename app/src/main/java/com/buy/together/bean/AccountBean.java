package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class AccountBean {
    /**
     * we_chat : {"wechat_id":23,"name":"12345678901","psw":"wechat_psw"}
     * qq : {"qq_id":24,"name":"qq_name","psw":"qq_psw"}
     * buy_together : {"buttogether_id":53,"name":"userName","psw":"buyTogether_psw"}
     */

    private WeChatBean we_chat;
    private QqBean qq;
    private BuyTogetherBean buy_together;

    public WeChatBean getWe_chat() {
        return we_chat;
    }

    public void setWe_chat(WeChatBean we_chat) {
        this.we_chat = we_chat;
    }

    public QqBean getQq() {
        return qq;
    }

    public void setQq(QqBean qq) {
        this.qq = qq;
    }

    public BuyTogetherBean getBuy_together() {
        return buy_together;
    }

    public void setBuy_together(BuyTogetherBean buy_together) {
        this.buy_together = buy_together;
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "we_chat=" + we_chat +
                ", qq=" + qq +
                ", buy_together=" + buy_together +
                '}';
    }
}
