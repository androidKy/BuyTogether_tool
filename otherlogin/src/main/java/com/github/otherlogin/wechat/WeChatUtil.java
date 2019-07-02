package com.github.otherlogin.wechat;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 调用微信登录工具类
 *
 * @author lsm
 */
public class WeChatUtil {

    /**
     * 微信的APP_ID
     */
    private static String APP_ID;

    /**
     * 注册微信APP_ID
     *
     * @param context
     * @param app_Id
     * @return
     */
    public static IWXAPI register(Context context, String app_Id) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, app_Id, true);
        iwxapi.registerApp(app_Id);
        APP_ID = app_Id;
        return iwxapi;
    }

    /**
     * 打开微信
     *
     * @param iwxapi
     * @return 是否打开成功
     */
    public static boolean openWeChat(IWXAPI iwxapi) {
        return openWeChat(iwxapi, "snsapi_userinfo");
    }

    /**
     * 打开微信
     *
     * @param iwxapi
     * @param scope
     * @return 是否打开成功，失败：false（可能是没有安装微信等原因导致打开微信失败）
     */
    public static boolean openWeChat(IWXAPI iwxapi, String scope) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = scope;
        req.state = "微信登录";
        boolean b = iwxapi.sendReq(req);
        return b;
    }

    /**
     * 获取微信app_id
     *
     * @return APP_ID
     */
    public static String getAppId() {
        return APP_ID;
    }

    /**
     * 微信是否安装
     *
     * @param iwxapi
     * @return
     */
    public static boolean isWXAppInstalled(IWXAPI iwxapi) {
        return iwxapi.isWXAppInstalled();
    }

}
