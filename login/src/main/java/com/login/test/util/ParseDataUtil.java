package com.login.test.util;

import com.safframework.log.L;

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
public class ParseDataUtil {

    public static String getPswFromLine(String line)
    {
        try {
            String[] splitArrays = line.split("----");
            L.i("psw : " + splitArrays[1]);
            return splitArrays[1];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getAccountFromLine(String line)
    {
        try {
            String[] splitArrays = line.split("----");
            L.i("account: " + splitArrays[0]);
            return splitArrays[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
