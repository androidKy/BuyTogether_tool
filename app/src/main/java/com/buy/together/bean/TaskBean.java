package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class TaskBean {

    /**
     * code : 200
     * task : {"task_id":1000,"task_type":1234,"ip":{"ip_id":234,"city":"GuangZhou","content":"192.168.2.104","mac_address":"0000:0000:0001"},"account":{"we_chat":{"wechat_id":23,"name":"12345678901","psw":"wechat_psw"},"qq":{"qq_id":24,"name":"qq_name","psw":"qq_psw"},"buy_together":{"buttogether_id":53,"name":"userName","psw":"buyTogether_psw"}},"goods":[{"goods_id":2014825,"goods_type":12,"name":"商品全名称","keyword":"夏季新款悠闲男装中短裤","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":80,"stay_time":{"goods_time":30,"search_time":5}},{"goods_id":2014826,"goods_type":13,"name":"商品全名称","keyword":"夏季新款悠闲女装","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":70,"stay_time":{"goods_time":30,"search_time":5}}]}
     * msg : tip_msg
     */

    private int code;
    private TaskData task;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TaskData getTask() {
        return task;
    }

    public void setTask(TaskData task) {
        this.task = task;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
