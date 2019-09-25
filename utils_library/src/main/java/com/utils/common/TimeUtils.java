package com.utils.common;

import com.safframework.log.L;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Description:
 * Created by Quinin on 2019-07-23.
 **/
public class TimeUtils {
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
            L.i(e.getMessage(), e);
        }
        if (date == null || mydate == null)
            return 0;
        return (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
    }

    public static long getMinutes(String date1,String date2){
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
            L.i(e.getMessage(), e);
        }
        if (date == null || mydate == null)
            return 0;
        return (date.getTime() - mydate.getTime()) / (60 * 1000);
    }
}
