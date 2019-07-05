package com.buy.together.bean;


import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class TaskData {
    /**
     * task_id : 1000
     * task_type : 1234
     * task_status:0
     * ip : {"ip_id":234,"city":"GuangZhou","content":"192.168.2.104","mac_address":"0000:0000:0001"}
     * account : {"we_chat":{"wechat_id":23,"name":"12345678901","psw":"wechat_psw"},"qq":{"qq_id":24,"name":"qq_name","psw":"qq_psw"},"buy_together":{"buttogether_id":53,"name":"userName","psw":"buyTogether_psw"}}
     * goods : [{"goods_id":2014825,"goods_type":12,"name":"商品全名称","keyword":"夏季新款悠闲男装中短裤","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":80,"stay_time":{"goods_time":30,"search_time":5}},{"goods_id":2014826,"goods_type":13,"name":"商品全名称","keyword":"夏季新款悠闲女装","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":70,"stay_time":{"goods_time":30,"search_time":5}}]
     */

    private int task_id;
    private int task_type;
    private int task_status;
    private int login_channel;
    private IpBean ip;
    private AccountBean account;
    private List<GoodsBean> goods;

    public int getLogin_channel(){
        return login_channel;
    }

    public void setLogin_channel(int login_channel)
    {
        this.login_channel = login_channel;
    }

    public int getTask_status(){
        return task_status;
    }

    public void setTask_status(int task_status)
    {
        this.task_status = task_status;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getTask_type() {
        return task_type;
    }

    public void setTask_type(int task_type) {
        this.task_type = task_type;
    }

    public IpBean getIp() {
        return ip;
    }

    public void setIp(IpBean ip) {
        this.ip = ip;
    }

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

    public List<GoodsBean> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsBean> goods) {
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "task_id=" + task_id +
                ", task_type=" + task_type +
                ", ip=" + ip +
                ", account=" + account +
                ", goods=" + goods +
                '}';
    }
}
