package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
public class TaskBean {


    /**
     * code : 200
     * msg : 成功
     * task : {"task_id":133,"task_type":1,"task_status":1,"task_amount":20,"task_dispense":1,"task_complete":0,"task_fail":0,"talk_msg":"","comment_content":"","buy_behavior":0,"create_time":1.564390811833056E9,"ip":{"city":"","content":"","mac_address":""},"account":{"id":269,"user":"2356727178","pwd":"qqqq8888","type":0},"goods":{"id":8,"goods_id":8183196997,"cat_id":6441,"goods_name":" 酸甜杨梅干九制梅子果蜜饯办公室户外休闲随身独立小包装零食","mall_name":"益C果城恒康美专卖店","keyword":"酸甜杨梅干九制梅子果蜜饯","search_price":"7.9","choose_info":"团购价:15.9,正常价:16.9%%%300g"},"device":{"id":1846,"bluetooth":"B8:37:65:19:2A:90","imei":"864297033467033","brand":"OPPO","android":"5.1","mac":"b8:37:65:19:2a:91","system":"A59s_11_A.12_180302","sn":"GQNFCUS499999999","imsi":"460005912308999","model":"OPPO A59s","useragent":"Dalvik/2.1.0 (Linux; U; Android 5.1; OPPO A59s Build/LMY47I)"},"delivery_address":{"name":"宿韵梅","phone":"17156908545","province":"北京市","city":"北京市","district":"东城区","street":"北京市东城区仓南胡同58"},"stay_time":{"goods_time":10,"search_time":18},"pay_account":{"id":"","type":"","username":"","pwd":""}}
     */

    private int code;
    private String msg;
    private TaskData task;

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

    public TaskData getTask() {
        return task;
    }

    public void setTask(TaskData task) {
        this.task = task;
    }

    public static class TaskData {
        /**
         * task_id : 133
         * task_type : 1
         * task_status : 1
         * task_amount : 20
         * task_dispense : 1
         * task_complete : 0
         * task_fail : 0
         * talk_msg :
         * comment_content :
         * buy_behavior : 0
         * create_time : 1.564390811833056E9
         * ip : {"city":"","content":"","mac_address":""}
         * account : {"id":269,"user":"2356727178","pwd":"qqqq8888","type":0}
         * goods : {"id":8,"goods_id":8183196997,"cat_id":6441,"goods_name":" 酸甜杨梅干九制梅子果蜜饯办公室户外休闲随身独立小包装零食","mall_name":"益C果城恒康美专卖店","keyword":"酸甜杨梅干九制梅子果蜜饯","search_price":"7.9","choose_info":"团购价:15.9,正常价:16.9%%%300g"}
         * device : {"id":1846,"bluetooth":"B8:37:65:19:2A:90","imei":"864297033467033","brand":"OPPO","android":"5.1","mac":"b8:37:65:19:2a:91","system":"A59s_11_A.12_180302","sn":"GQNFCUS499999999","imsi":"460005912308999","model":"OPPO A59s","useragent":"Dalvik/2.1.0 (Linux; U; Android 5.1; OPPO A59s Build/LMY47I)"}
         * delivery_address : {"name":"宿韵梅","phone":"17156908545","province":"北京市","city":"北京市","district":"东城区","street":"北京市东城区仓南胡同58"}
         * stay_time : {"goods_time":10,"search_time":18}
         * pay_account : {"id":"","type":"","username":"","pwd":""}
         */

        private int task_id;
        private int task_type;
        private int task_status;
        private int task_amount;
        private int task_dispense;
        private int task_complete;
        private int task_fail;
        private String talk_msg;
        private String comment_content;
        private int buy_behavior;
        private double create_time;
        private IpBean ip;
        private AccountBean account;
        private GoodsBean goods;
        private DeviceBean device;
        private DeliveryAddressBean delivery_address;
        private StayTimeBean stay_time;
        private PayAccountBean pay_account;

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

        public int getTask_amount() {
            return task_amount;
        }

        public void setTask_amount(int task_amount) {
            this.task_amount = task_amount;
        }

        public int getTask_dispense() {
            return task_dispense;
        }

        public void setTask_dispense(int task_dispense) {
            this.task_dispense = task_dispense;
        }

        public int getTask_complete() {
            return task_complete;
        }

        public void setTask_complete(int task_complete) {
            this.task_complete = task_complete;
        }

        public int getTask_fail() {
            return task_fail;
        }

        public void setTask_fail(int task_fail) {
            this.task_fail = task_fail;
        }

        public String getTalk_msg() {
            return talk_msg;
        }

        public void setTalk_msg(String talk_msg) {
            this.talk_msg = talk_msg;
        }

        public String getComment_content() {
            return comment_content;
        }

        public void setComment_content(String comment_content) {
            this.comment_content = comment_content;
        }

        public int getBuy_behavior() {
            return buy_behavior;
        }

        public void setBuy_behavior(int buy_behavior) {
            this.buy_behavior = buy_behavior;
        }

        public double getCreate_time() {
            return create_time;
        }

        public void setCreate_time(double create_time) {
            this.create_time = create_time;
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

        public GoodsBean getGoods() {
            return goods;
        }

        public void setGoods(GoodsBean goods) {
            this.goods = goods;
        }

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public DeliveryAddressBean getDelivery_address() {
            return delivery_address;
        }

        public void setDelivery_address(DeliveryAddressBean delivery_address) {
            this.delivery_address = delivery_address;
        }

        public StayTimeBean getStay_time() {
            return stay_time;
        }

        public void setStay_time(StayTimeBean stay_time) {
            this.stay_time = stay_time;
        }

        public PayAccountBean getPay_account() {
            return pay_account;
        }

        public void setPay_account(PayAccountBean pay_account) {
            this.pay_account = pay_account;
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

        public static class AccountBean {
            /**
             * id : 269
             * user : 2356727178
             * pwd : qqqq8888
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

        public static class GoodsBean {
            /**
             * id : 8
             * goods_id : 8183196997
             * cat_id : 6441
             * goods_name :  酸甜杨梅干九制梅子果蜜饯办公室户外休闲随身独立小包装零食
             * mall_name : 益C果城恒康美专卖店
             * keyword : 酸甜杨梅干九制梅子果蜜饯
             * search_price : 7.9
             * choose_info : 团购价:15.9,正常价:16.9%%%300g
             */

            private long id;
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

        public static class DeviceBean {
            /**
             * id : 1846
             * bluetooth : B8:37:65:19:2A:90
             * imei : 864297033467033
             * brand : OPPO
             * android : 5.1
             * mac : b8:37:65:19:2a:91
             * system : A59s_11_A.12_180302
             * sn : GQNFCUS499999999
             * imsi : 460005912308999
             * model : OPPO A59s
             * useragent : Dalvik/2.1.0 (Linux; U; Android 5.1; OPPO A59s Build/LMY47I)
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

        public static class StayTimeBean {
            /**
             * goods_time : 10
             * search_time : 18
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

        public static class PayAccountBean {
            /**
             * id :
             * type :
             * username :
             * pwd :
             */

            private String id;
            private String type;
            private String username;
            private String pwd;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPwd() {
                return pwd;
            }

            public void setPwd(String pwd) {
                this.pwd = pwd;
            }
        }
    }
}
