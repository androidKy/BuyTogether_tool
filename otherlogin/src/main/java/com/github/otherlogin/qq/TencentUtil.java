package com.github.otherlogin.qq;

import android.app.Activity;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * 调用手机QQ登录工具类
 *
 * @author lsm
 */
public class TencentUtil {

    public static boolean openQQ(Activity activity, String appId, IUiListener baseUiListener) {
        Tencent mTencent = Tencent.createInstance(appId, activity);
        boolean sessionValid = mTencent.isSessionValid();
        if (!sessionValid) {
            mTencent.login(activity, "all", baseUiListener);
        }
        return !sessionValid;
    }

}
