package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-09-25.
 **/
public class ConfirmSignedBean {

    /**
     * code : 200
     * msg : 成功
     * task : {"id":611,"order_id":"190905-136434484093225","ip":{"city":"盘锦市","content":"14.18.242.34","mac_address":"1c:15:1f:ae:10:27"},"goods":{"id":80,"goods_url":"https://mobile.yangkeduo.com/goods.html?goods_id=36849535783&refer_page_name=goods_detail&refer_page_id=10014_1567672399464_B1SDp1HTo3&refer_page_sn=10014&_x_share_id=b1902fc19f444c22b7f30afa6efaf574","goods_id":36849535783,"cat_id":6556,"goods_name":"坚果胚芽燕麦片即食冲饮水果谷物早餐懒人食品营养早餐","mall_name":"维施大健康品牌店","keyword":"坚果胚芽燕麦片即食冲饮","search_price":"37.9","choose_info":"团购价:37.9,正常价:39.9 规格:一盒（内含16袋）"},"account":{"id":2041,"user":"209017350","pwd":"8xd97jjxcy","type":0},"device":{"id":448,"bluetooth":"02:00:00:00:00:00","imei":"867008037092264","brand":"HUAWEI","android":"7.0","mac":"1c:15:1f:ae:10:27","system":"BAC-AL00C00B200","sn":"B7EDU17A21001310","imsi":"460021052755719","model":"BAC-AL00","useragent":"Dalvik/2.1.0 (Linux; U; Android 7.0; BAC-AL00 Build/HUAWEIBAC-AL00)"}}
     */

    private int code;
    private String msg;
    private ConfirmBean task;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ConfirmBean getTask() {
        return task;
    }

    public void setTask(ConfirmBean task) {
        this.task = task;
    }

    public static class ConfirmBean {
        /**
         * id : 611
         * order_id : 190905-136434484093225
         * ip : {"city":"盘锦市","content":"14.18.242.34","mac_address":"1c:15:1f:ae:10:27"}
         * goods : {"id":80,"goods_url":"https://mobile.yangkeduo.com/goods.html?goods_id=36849535783&refer_page_name=goods_detail&refer_page_id=10014_1567672399464_B1SDp1HTo3&refer_page_sn=10014&_x_share_id=b1902fc19f444c22b7f30afa6efaf574","goods_id":36849535783,"cat_id":6556,"goods_name":"坚果胚芽燕麦片即食冲饮水果谷物早餐懒人食品营养早餐","mall_name":"维施大健康品牌店","keyword":"坚果胚芽燕麦片即食冲饮","search_price":"37.9","choose_info":"团购价:37.9,正常价:39.9 规格:一盒（内含16袋）"}
         * account : {"id":2041,"user":"209017350","pwd":"8xd97jjxcy","type":0}
         * device : {"id":448,"bluetooth":"02:00:00:00:00:00","imei":"867008037092264","brand":"HUAWEI","android":"7.0","mac":"1c:15:1f:ae:10:27","system":"BAC-AL00C00B200","sn":"B7EDU17A21001310","imsi":"460021052755719","model":"BAC-AL00","useragent":"Dalvik/2.1.0 (Linux; U; Android 7.0; BAC-AL00 Build/HUAWEIBAC-AL00)"}
         */

        private int id;
        private String order_id;
        private IpBean ip;
        private GoodsBean goods;
        private AccountBean account;
        private DeviceBean device;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public IpBean getIp() {
            return ip;
        }

        public void setIp(IpBean ip) {
            this.ip = ip;
        }

        public GoodsBean getGoods() {
            return goods;
        }

        public void setGoods(GoodsBean goods) {
            this.goods = goods;
        }

        public AccountBean getAccount() {
            return account;
        }

        public void setAccount(AccountBean account) {
            this.account = account;
        }

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public static class IpBean {
            /**
             * city : 盘锦市
             * content : 14.18.242.34
             * mac_address : 1c:15:1f:ae:10:27
             */

            private String city;
            private String content;
            private String mac_address;

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

        public static class GoodsBean {
            /**
             * id : 80
             * goods_url : https://mobile.yangkeduo.com/goods.html?goods_id=36849535783&refer_page_name=goods_detail&refer_page_id=10014_1567672399464_B1SDp1HTo3&refer_page_sn=10014&_x_share_id=b1902fc19f444c22b7f30afa6efaf574
             * goods_id : 36849535783
             * cat_id : 6556
             * goods_name : 坚果胚芽燕麦片即食冲饮水果谷物早餐懒人食品营养早餐
             * mall_name : 维施大健康品牌店
             * keyword : 坚果胚芽燕麦片即食冲饮
             * search_price : 37.9
             * choose_info : 团购价:37.9,正常价:39.9 规格:一盒（内含16袋）
             */

            private long id;
            private String goods_url;
            private long goods_id;
            private int cat_id;
            private String goods_name;
            private String mall_name;
            private String keyword;
            private String search_price;
            private String choose_info;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getGoods_url() {
                return goods_url;
            }

            public void setGoods_url(String goods_url) {
                this.goods_url = goods_url;
            }

            public long getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(long goods_id) {
                this.goods_id = goods_id;
            }

            public int getCat_id() {
                return cat_id;
            }

            public void setCat_id(int cat_id) {
                this.cat_id = cat_id;
            }

            public String getGoods_name() {
                return goods_name;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public String getMall_name() {
                return mall_name;
            }

            public void setMall_name(String mall_name) {
                this.mall_name = mall_name;
            }

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
            }

            public String getSearch_price() {
                return search_price;
            }

            public void setSearch_price(String search_price) {
                this.search_price = search_price;
            }

            public String getChoose_info() {
                return choose_info;
            }

            public void setChoose_info(String choose_info) {
                this.choose_info = choose_info;
            }
        }

        public static class AccountBean {
            /**
             * id : 2041
             * user : 209017350
             * pwd : 8xd97jjxcy
             * type : 0
             */

            private int id;
            private String user;
            private String pwd;
            private int type;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPwd() {
                return pwd;
            }

            public void setPwd(String pwd) {
                this.pwd = pwd;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }

        public static class DeviceBean {
            /**
             * id : 448
             * bluetooth : 02:00:00:00:00:00
             * imei : 867008037092264
             * brand : HUAWEI
             * android : 7.0
             * mac : 1c:15:1f:ae:10:27
             * system : BAC-AL00C00B200
             * sn : B7EDU17A21001310
             * imsi : 460021052755719
             * model : BAC-AL00
             * useragent : Dalvik/2.1.0 (Linux; U; Android 7.0; BAC-AL00 Build/HUAWEIBAC-AL00)
             */

            private long id;
            private String bluetooth;
            private String imei;
            private String brand;
            private String android;
            private String mac;
            private String system;
            private String sn;
            private String imsi;
            private String model;
            private String useragent;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getBluetooth() {
                return bluetooth;
            }

            public void setBluetooth(String bluetooth) {
                this.bluetooth = bluetooth;
            }

            public String getImei() {
                return imei;
            }

            public void setImei(String imei) {
                this.imei = imei;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getAndroid() {
                return android;
            }

            public void setAndroid(String android) {
                this.android = android;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public String getSystem() {
                return system;
            }

            public void setSystem(String system) {
                this.system = system;
            }

            public String getSn() {
                return sn;
            }

            public void setSn(String sn) {
                this.sn = sn;
            }

            public String getImsi() {
                return imsi;
            }

            public void setImsi(String imsi) {
                this.imsi = imsi;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getUseragent() {
                return useragent;
            }

            public void setUseragent(String useragent) {
                this.useragent = useragent;
            }
        }
    }
}
