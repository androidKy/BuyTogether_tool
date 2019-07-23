package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
public class TaskServiceData {


    /**
     * code : 200
     * msg : 成功
     * task : {"task_id":1106,"task_status":1,"task_amount":20,"task_dispense":0,"task_complete":0,"task_fail":0,"time_limit":false,"talk_msg":"商品还有吗？","score_need":true,"comment_need":true,"comment_content":"东挑西选，头都晕了，还是这家吧，评价也不错","has_commented":false,"create_time":"2019-07-23T10:17:54.282","ip":{"city":"","content":"","mac_address":""},"account":{"id":23,"user":"2337630557","pwd":"xh19nyayrwb","type":0},"goods":{"id":5,"goods_id":1395698343,"cat_id":8464,"goods_name":"18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子","mall_name":"姿想旗舰店","keyword":"18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子","search_price":"28.24","choose_info":""},"device":{"id":3958,"bluetooth":"02:00:00:00:00:00","imei":"A00000719CDFA7","brand":"OPPO","android":"6.0.1","mac":"02:00:00:00:00:00","system":"A57_11_A.26_180329","sn":"aa65011","imsi":"460036041518319","model":"OPPO A57","useragent":"Dalvik/2.1.0 (Linux; U; Android 6.0.1; OPPO A57 Build/MMB29M)"},"delivery_address":{"name":"康念之","phone":"15513348714","province":"北京市","city":"北京市","district":"东城区","street":"北京市东城区东四北大街329号"},"stay_time":{"goods_time":3,"search_time":14}}
     */

    private int code;
    private String msg;
    private TaskBean task;

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

    public TaskBean getTask() {
        return task;
    }

    public void setTask(TaskBean task) {
        this.task = task;
    }

    public static class TaskBean {
        /**
         * task_id : 1106
         * task_status : 1
         * task_amount : 20
         * task_dispense : 0
         * task_complete : 0
         * task_fail : 0
         * time_limit : false
         * talk_msg : 商品还有吗？
         * score_need : true
         * comment_need : true
         * comment_content : 东挑西选，头都晕了，还是这家吧，评价也不错
         * has_commented : false
         * create_time : 2019-07-23T10:17:54.282
         * ip : {"city":"","content":"","mac_address":""}
         * account : {"id":23,"user":"2337630557","pwd":"xh19nyayrwb","type":0}
         * goods : {"id":5,"goods_id":1395698343,"cat_id":8464,"goods_name":"18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子","mall_name":"姿想旗舰店","keyword":"18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子","search_price":"28.24","choose_info":""}
         * device : {"id":3958,"bluetooth":"02:00:00:00:00:00","imei":"A00000719CDFA7","brand":"OPPO","android":"6.0.1","mac":"02:00:00:00:00:00","system":"A57_11_A.26_180329","sn":"aa65011","imsi":"460036041518319","model":"OPPO A57","useragent":"Dalvik/2.1.0 (Linux; U; Android 6.0.1; OPPO A57 Build/MMB29M)"}
         * delivery_address : {"name":"康念之","phone":"15513348714","province":"北京市","city":"北京市","district":"东城区","street":"北京市东城区东四北大街329号"}
         * stay_time : {"goods_time":3,"search_time":14}
         */

        private int task_id;
        private int task_status;
        private int task_type;
        private int task_amount;
        private int task_dispense;
        private int task_complete;
        private int task_fail;
        private boolean time_limit;
        private String talk_msg;
        private boolean score_need;
        private boolean comment_need;
        private String comment_content;
        private boolean has_commented;
        private String create_time;
        private IpBean ip;
        private AccountBean account;
        private GoodsBean goods;
        private DeviceBean device;
        private DeliveryAddressBean delivery_address;
        private StayTimeBean stay_time;


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

        public boolean isTime_limit() {
            return time_limit;
        }

        public void setTime_limit(boolean time_limit) {
            this.time_limit = time_limit;
        }

        public String getTalk_msg() {
            return talk_msg;
        }

        public void setTalk_msg(String talk_msg) {
            this.talk_msg = talk_msg;
        }

        public boolean isScore_need() {
            return score_need;
        }

        public void setScore_need(boolean score_need) {
            this.score_need = score_need;
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

        public boolean isHas_commented() {
            return has_commented;
        }

        public void setHas_commented(boolean has_commented) {
            this.has_commented = has_commented;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
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
             * id : 23
             * user : 2337630557
             * pwd : xh19nyayrwb
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
             * id : 5
             * goods_id : 1395698343
             * cat_id : 8464
             * goods_name : 18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子
             * mall_name : 姿想旗舰店
             * keyword : 18种花型雪纺碎花长裙夏季高腰宽松a字裙ins半身裙女夏中长款裙子
             * search_price : 28.24
             * choose_info :
             */

            private int id;
            private int goods_id;
            private int cat_id;
            private String goods_name;
            private String mall_name;
            private String keyword;
            private String search_price;
            private String choose_info;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(int goods_id) {
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
             * id : 3958
             * bluetooth : 02:00:00:00:00:00
             * imei : A00000719CDFA7
             * brand : OPPO
             * android : 6.0.1
             * mac : 02:00:00:00:00:00
             * system : A57_11_A.26_180329
             * sn : aa65011
             * imsi : 460036041518319
             * model : OPPO A57
             * useragent : Dalvik/2.1.0 (Linux; U; Android 6.0.1; OPPO A57 Build/MMB29M)
             */

            private int id;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
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
             * name : 康念之
             * phone : 15513348714
             * province : 北京市
             * city : 北京市
             * district : 东城区
             * street : 北京市东城区东四北大街329号
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
             * goods_time : 3
             * search_time : 14
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
