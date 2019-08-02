package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
public class AccountBean {


    /**
     * code : 200
     * msg : 成功
     * data : {"account":{"id":71,"type":0,"user":"2752726897","pwd":"qqqq8888"},"delivery_address":{"province":"河北省","city":"秦皇岛市","district":"海港区","street":"迎宾路139号","name":"贺瀚海","phone":"17375987785"},"device_info":{"id":11931,"bluetooth":"E4:58:B8:2F:B6:62","imei":"355309074279080","brand":"samsung","android":"6.0.1","mac":"E4:58:B8:2F:B6:63","system":"MMB29M.A9000ZCU1BQC1","sn":"7e37a0a3","imsi":"460001258040474","model":"SM-A9000","useragent":"Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-A9000 Build/MMB29M)"}}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * account : {"id":71,"type":0,"user":"2752726897","pwd":"qqqq8888"}
         * delivery_address : {"province":"河北省","city":"秦皇岛市","district":"海港区","street":"迎宾路139号","name":"贺瀚海","phone":"17375987785"}
         * device_info : {"id":11931,"bluetooth":"E4:58:B8:2F:B6:62","imei":"355309074279080","brand":"samsung","android":"6.0.1","mac":"E4:58:B8:2F:B6:63","system":"MMB29M.A9000ZCU1BQC1","sn":"7e37a0a3","imsi":"460001258040474","model":"SM-A9000","useragent":"Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-A9000 Build/MMB29M)"}
         */

        private Account account;
        private DeliveryAddressBean delivery_address;
        private DeviceInfoBean device_info;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public DeliveryAddressBean getDelivery_address() {
            return delivery_address;
        }

        public void setDelivery_address(DeliveryAddressBean delivery_address) {
            this.delivery_address = delivery_address;
        }

        public DeviceInfoBean getDevice_info() {
            return device_info;
        }

        public void setDevice_info(DeviceInfoBean device_info) {
            this.device_info = device_info;
        }

        public static class Account {
            /**
             * id : 71
             * type : 0
             * user : 2752726897
             * pwd : qqqq8888
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

        public static class DeliveryAddressBean {
            /**
             * province : 河北省
             * city : 秦皇岛市
             * district : 海港区
             * street : 迎宾路139号
             * name : 贺瀚海
             * phone : 17375987785
             */

            private String province;
            private String city;
            private String district;
            private String street;
            private String name;
            private String phone;

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
        }

        public static class DeviceInfoBean {
            /**
             * id : 11931
             * bluetooth : E4:58:B8:2F:B6:62
             * imei : 355309074279080
             * brand : samsung
             * android : 6.0.1
             * mac : E4:58:B8:2F:B6:63
             * system : MMB29M.A9000ZCU1BQC1
             * sn : 7e37a0a3
             * imsi : 460001258040474
             * model : SM-A9000
             * useragent : Dalvik/2.1.0 (Linux; U; Android 6.0.1; SM-A9000 Build/MMB29M)
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
    }
}
