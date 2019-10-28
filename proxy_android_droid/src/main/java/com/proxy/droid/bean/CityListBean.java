package com.proxy.droid.bean;

import java.util.List;

/**
 * Description:
 * Created by Quinin on 2019-07-19.
 **/
public class CityListBean {

    /**
     * code : 0
     * msg :
     * data : {"count":24,"cityList":[{"name":"安徽","data":[{"value":15613,"name":"宿州市","cityid":"341300"},{"value":8486,"name":"阜阳市","cityid":"341200"}]},{"name":"广东","data":[{"value":17495,"name":"佛山市","cityid":"440600"},{"value":7426,"name":"汕头市","cityid":"440500"}]}]}
     */

    private int code;
    private String msg;
    private CityListTop data;

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

    public CityListTop getData() {
        return data;
    }

    public void setData(CityListTop data) {
        this.data = data;
    }

    public static class CityListTop {
        /**
         * count : 24
         * cityList : [{"name":"安徽","data":[{"value":15613,"name":"宿州市","cityid":"341300"},{"value":8486,"name":"阜阳市","cityid":"341200"}]},{"name":"广东","data":[{"value":17495,"name":"佛山市","cityid":"440600"},{"value":7426,"name":"汕头市","cityid":"440500"}]}]
         */

        private int count;
        private List<CityList> cityList;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<CityList> getCityList() {
            return cityList;
        }

        public void setCityList(List<CityList> cityList) {
            this.cityList = cityList;
        }

        public static class CityList {
            /**
             * name : 安徽
             * data : [{"value":15613,"name":"宿州市","cityid":"341300"},{"value":8486,"name":"阜阳市","cityid":"341200"}]
             */

            private String name;
            private List<City> data;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<City> getData() {
                return data;
            }

            public void setData(List<City> data) {
                this.data = data;
            }

            public static class City {
                /**
                 * value : 15613
                 * name : 宿州市
                 * cityid : 341300
                 */

                private int value;
                private String name;
                private String cityid;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getCityid() {
                    return cityid;
                }

                public void setCityid(String cityid) {
                    this.cityid = cityid;
                }
            }
        }
    }
}
