package com.buy.together.hook.sp;

import android.content.Context;
import android.content.SharedPreferences;
import com.accessibility.service.util.Constant;
import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.ref.WeakReference;

/**
 * description:
 * author: kyXiao
 * date: 2019/4/8
 */
class RemotePreferenceUtil {
    private static WeakReference<SharedPreferences> instance = new WeakReference<>(null);

    static SharedPreferences getInstance(Context context) {
        SharedPreferences lastUsedInstance = instance.get();
        if (lastUsedInstance != null) {
            return lastUsedInstance;
        }
        //CachedRemotePreferences newInstance = new CachedRemotePreferences(context, BuildConfig.APPLICATION_ID, Common.PREFS_FILE);
        SharedPreferences prefs = new RemotePreferences(context, Constant.PKG_NAME, Constant.SP_DEVICE_PARAMS);
        instance = new WeakReference<>(prefs);
        return prefs;
    }

}
