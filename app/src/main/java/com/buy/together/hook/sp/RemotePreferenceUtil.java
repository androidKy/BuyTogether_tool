package com.buy.together.hook.sp;

import android.content.Context;
import android.content.SharedPreferences;
import com.buy.together.utils.Constant;
import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.ref.WeakReference;

/**
 * description:
 * author: kyXiao
 * date: 2019/4/8
 */
public class RemotePreferenceUtil {
    private static WeakReference<SharedPreferences> instance = new WeakReference<>(null);

    public static SharedPreferences getInstance(Context context) {
        SharedPreferences lastUsedInstance = instance.get();
        if (lastUsedInstance != null) {
            return lastUsedInstance;
        }
        //CachedRemotePreferences newInstance = new CachedRemotePreferences(context, BuildConfig.APPLICATION_ID, Common.PREFS_FILE);
        SharedPreferences prefs = new RemotePreferences(context, "com.buy.together", Constant.SP_DEVICE_PARAMS);
        instance = new WeakReference<>(prefs);
        return prefs;
    }

}
