package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-08-10.
 **/
public class CommentBean {

    /**
     * code : 200
     * msg : 成功
     * task : {"task_id":1797,"task_type":"4","comment_content":"真的不好意思，由于我的粗心大意让卖家麻烦了","can_comment_time":1.565377123E9,"order_id":"123456","goods":{"id":19,"goods_id":13639176298,"goods_name":"一次性碗塑料圆形碗家用吃饭小碗外卖打包餐盒带盖汤碗结婚酒席","mall_name":"淘淘家居百货","search_price":"8.8","choose_info":"团购价:8.8,正常价:9.8 规格:360碗型50只【不带盖】"},"account":{"id":432,"type":0,"user":"2309596258","pwd":"sp87v7ypu7x"},"device":{"id":2816,"bluetooth":"02:00:00:00:00:00","imei":"a0000059bbcfba","brand":"HUAWEI","android":"7.0","mac":"98:e7:f5:df:e7:cd","system":"NXT-CL00C92B596","sn":"QVM7N16108007612","imsi":"460031336817364","model":"HUAWEI NXT-CL00","useragent":"Dalvik/2.1.0 (Linux; U; Android 7.0; HUAWEI NXT-CL00 Build/HUAWEINXT-CL00)"},"ip":{"city":"","content":"","mac_address":""}}
     */

    private int code;
    private String msg;
    private CommentTask task;

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

    public CommentTask getTask() {
        return task;
    }

    public void setTask(CommentTask task) {
        this.task = task;
    }

    public static class CommentTask {
        /**
         * task_id : 1797
         * task_type : 4
         * comment_content : 真的不好意思，由于我的粗心大意让卖家麻烦了
         * can_comment_time : 1.565377123E9
         * order_id : 123456
         * goods : {"id":19,"goods_id":13639176298,"goods_name":"一次性碗塑料圆形碗家用吃饭小碗外卖打包餐盒带盖汤碗结婚酒席","mall_name":"淘淘家居百货","search_price":"8.8","choose_info":"团购价:8.8,正常价:9.8 规格:360碗型50只【不带盖】"}
         * account : {"id":432,"type":0,"user":"2309596258","pwd":"sp87v7ypu7x"}
         * device : {"id":2816,"bluetooth":"02:00:00:00:00:00","imei":"a0000059bbcfba","brand":"HUAWEI","android":"7.0","mac":"98:e7:f5:df:e7:cd","system":"NXT-CL00C92B596","sn":"QVM7N16108007612","imsi":"460031336817364","model":"HUAWEI NXT-CL00","useragent":"Dalvik/2.1.0 (Linux; U; Android 7.0; HUAWEI NXT-CL00 Build/HUAWEINXT-CL00)"}
         * ip : {"city":"","content":"","mac_address":""}
         */

        private int task_id;
        private String task_type;
        private String comment_content;
        private double can_comment_time;
        private String order_id;
        private GoodsBean goods;
        private AccountBean account;
        private DeviceBean device;
        private DeliveryAddressBean delivery_address;
        private IpBean ip;

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public String getTask_type() {
            return task_type;
        }

        public void setTask_type(String task_type) {
            this.task_type = task_type;
        }

        public String getComment_content() {
            return comment_content;
        }

        public void setComment_content(String comment_content) {
            this.comment_content = comment_content;
        }

        public double getCan_comment_time() {
            return can_comment_time;
        }

        public void setCan_comment_time(double can_comment_time) {
            this.can_comment_time = can_comment_time;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
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

        public IpBean getIp() {
            return ip;
        }

        public void setIp(IpBean ip) {
            this.ip = ip;
        }

        public DeliveryAddressBean getDelivery_address() {
            return delivery_address;
        }

        public void setDelivery_address(DeliveryAddressBean delivery_address) {
            this.delivery_address = delivery_address;
        }

        public static class GoodsBean {
            /**
             * id : 19
             * goods_id : 13639176298
             * goods_name : 一次性碗塑料圆形碗家用吃饭小碗外卖打包餐盒带盖汤碗结婚酒席
             * mall_name : 淘淘家居百货
             * search_price : 8.8
             * choose_info : 团购价:8.8,正常价:9.8 规格:360碗型50只【不带盖】
             */

            private long id;
            private long goods_id;
            private String goods_name;
            private String mall_name;
            private String search_price;
            private String choose_info;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(long goods_id) {
                this.goods_id = goods_id;
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
             * id : 432
             * type : 0
             * user : 2309596258
             * pwd : sp87v7ypu7x
             */

            private int id;
            private int type;
            private String user;
            private String pwd;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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
        }

        public static class DeviceBean {
            /**
             * id : 2816
             * bluetooth : 02:00:00:00:00:00
             * imei : a0000059bbcfba
             * brand : HUAWEI
             * android : 7.0
             * mac : 98:e7:f5:df:e7:cd
             * system : NXT-CL00C92B596
             * sn : QVM7N16108007612
             * imsi : 460031336817364
             * model : HUAWEI NXT-CL00
             * useragent : Dalvik/2.1.0 (Linux; U; Android 7.0; HUAWEI NXT-CL00 Build/HUAWEINXT-CL00)
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

        public static class DeliveryAddressBean {
            /**
             * name : 宿韵梅
             * phone : 17156908545
             * province : 北京市
             * city : 北京市
             * district : 东城区
             * street : 北京市东城区仓南胡同58
             */

            private String name;
            private String phone;
            private String province;
            private String city;
            private String district;
            private String street;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }
        }


        public static class IpBean {
            /**
             * city :
             * content :
             * mac_address :
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
    }
}
