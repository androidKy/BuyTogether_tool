package com.buy.together.bean;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class GoodsBean {

    /**
     * goods_id : 2014825
     * goods_type : 12
     * name : 商品全名称
     * keyword : 夏季新款悠闲男装中短裤
     * delivery_address : 收货地址
     * talk_msg : 老板你好，这条裤子的尺码是怎么样的
     * tasked_count : 0
     * comment_need : true
     * comment_content : 衣服很合适
     * score_need : true
     * score_value : 80
     * stay_time : {"goods_time":30,"search_time":5}
     */

    private int goods_id;
    private int goods_type;
    private String name;
    private String keyword;
    private String delivery_address;
    private String talk_msg;
    private int tasked_count;
    private boolean comment_need;
    private String comment_content;
    private boolean score_need;
    private int score_value;
    private String choose_info;
    private StayTimeBean stay_time;

    public String getChoose_info() {
        return choose_info;
    }

    public void setChoose_info(String choose_info) {
        this.choose_info = choose_info;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(int goods_type) {
        this.goods_type = goods_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getTalk_msg() {
        return talk_msg;
    }

    public void setTalk_msg(String talk_msg) {
        this.talk_msg = talk_msg;
    }

    public int getTasked_count() {
        return tasked_count;
    }

    public void setTasked_count(int tasked_count) {
        this.tasked_count = tasked_count;
    }

    public boolean isComment_need() {
        return comment_need;
    }

    public void setComment_need(boolean comment_need) {
        this.comment_need = comment_need;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public boolean isScore_need() {
        return score_need;
    }

    public void setScore_need(boolean score_need) {
        this.score_need = score_need;
    }

    public int getScore_value() {
        return score_value;
    }

    public void setScore_value(int score_value) {
        this.score_value = score_value;
    }

    public StayTimeBean getStay_time() {
        return stay_time;
    }

    public void setStay_time(StayTimeBean stay_time) {
        this.stay_time = stay_time;
    }

    public static class StayTimeBean {
        /**
         * goods_time : 30
         * search_time : 5
         */

        private int goods_time;
        private int search_time;

        public int getGoods_time() {
            return goods_time;
        }

        public void setGoods_time(int goods_time) {
            this.goods_time = goods_time;
        }

        public int getSearch_time() {
            return search_time;
        }

        public void setSearch_time(int search_time) {
            this.search_time = search_time;
        }
    }

    @Override
    public String toString() {
        return "GoodsBean{" +
                "goods_id=" + goods_id +
                ", goods_type=" + goods_type +
                ", name='" + name + '\'' +
                ", keyword='" + keyword + '\'' +
                ", delivery_address='" + delivery_address + '\'' +
                ", talk_msg='" + talk_msg + '\'' +
                ", tasked_count=" + tasked_count +
                ", comment_need=" + comment_need +
                ", comment_content='" + comment_content + '\'' +
                ", score_need=" + score_need +
                ", score_value=" + score_value +
                ", stay_time=" + stay_time +
                '}';
    }
}
