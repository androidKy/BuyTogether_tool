package com.accessibility.service.data;

import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
public class TaskServiceData {

    /**
     * code : 200
     * task : {"task_id":1000,"task_type":1234,"task_status":0,"login_channel":0,"ip":{"ip_id":234,"city":"GuangZhou","content":"192.168.2.104","mac_address":"0000:0000:0001"},"account":{"we_chat":{"wechat_id":23,"name":"12345678901","psw":"wechat_psw"},"qq":{"qq_id":24,"name":"qq_name","psw":"qq_psw"},"buy_together":{"buttogether_id":53,"name":"userName","psw":"buyTogether_psw"}},"goods":[{"goods_id":2014825,"goods_type":12,"name":"商品全名称","keyword":"夏季新款悠闲男装中短裤","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":80,"stay_time":{"goods_time":30,"search_time":5}},{"goods_id":2014826,"goods_type":13,"name":"商品全名称","keyword":"夏季新款悠闲女装","delivery_address":"收货地址","talk_msg":"老板你好，这条裤子的尺码是怎么样的","tasked_count":0,"comment_need":true,"comment_content":"衣服很合适","score_need":true,"score_value":70,"stay_time":{"goods_time":30,"search_time":5}}]}
     * msg : tip_msg
     */

    private int code;
    private TaskBean task;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TaskBean getTask() {
        return task;
    }

    public void setTask(TaskBean task) {
        this.task = task;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class TaskBean {
        /**
         * task_id : 1000
         * task_type : 1234
         * task_status : 0
         * login_channel : 0
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

        public int getTask_status() {
            return task_status;
        }

        public void setTask_status(int task_status) {
            this.task_status = task_status;
        }

        public int getLogin_channel() {
            return login_channel;
        }

        public void setLogin_channel(int login_channel) {
            this.login_channel = login_channel;
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

        public static class IpBean {
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
        }

        public static class AccountBean {
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

            public static class WeChatBean {
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
            }

            public static class QqBean {
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
            }

            public static class BuyTogetherBean {
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
            }
        }

        public static class GoodsBean {
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
            private StayTimeBean stay_time;

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
        }
    }
}
