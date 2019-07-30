package com.accessibility.service.data;

/**
 * Description:
 * Created by Quinin on 2019-07-30.
 **/
public class AccountBean {

    /**
     * code : 200
     * msg : 成功
     * account : {"id":70,"type":0,"user":"2757860234","pwd":"qqqq8888"}
     */

    private int code;
    private String msg;
    private Account account;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public static class Account {
        /**
         * id : 70
         * type : 0
         * user : 2757860234
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
}
