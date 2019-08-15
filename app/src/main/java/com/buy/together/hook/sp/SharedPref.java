package com.buy.together.hook.sp;

import android.content.Context;
import com.accessibility.service.util.Constant;
import de.robv.android.xposed.XSharedPreferences;

public class SharedPref {
    private static XSharedPreferences myXsharedPref;

    private static XSharedPreferences getMyXSharedPref() {
        if (myXsharedPref != null) {
            myXsharedPref.reload();
            return myXsharedPref;
        }
        myXsharedPref = new XSharedPreferences(Constant.PKG_NAME, Constant.SP_DEVICE_PARAMS);
        myXsharedPref.makeWorldReadable();
        return myXsharedPref;
    }

    public static String getXValue(Context context,String key) {
       /* String value = "";
        try {
            value = getMyXSharedPref().getString(key, "");
        } catch (Exception e) {
            L.i(e.getMessage(), e);
        }
        return value;*/

       return RemotePreferenceUtil.getInstance(context).getString(key,"");

/*
        final String finalKey = key;
        final String[] value = {""};
        final Context finalContext = context;


        VersionUtil.Companion.checkVersion(new VersionUtil.VersionListener() {
            @Override
            public void onSDK23less() {
                try {
                    value[0] = getMyXSharedPref().getString(finalKey, "");
                } catch (Exception e) {
                    // System.out.println("getSharedPref ERROR: " + e.getMessage());
                    L.i(e.getMessage(), e);
                }
            }

            @Override
            public void onSDK26less() {
                if (finalContext == null) {
                    //L.i("context == null key = " + key);
                    return;
                }
                value[0] = RemotePreferenceUtil.getInstance(finalContext).getString(finalKey, "");
            }
        });

        return value[0];*/
    }

    public static void putXValue(String key, String value) {

    }
}
