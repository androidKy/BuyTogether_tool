package com.github.otherlogin.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public abstract class BaseWXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    /**
     * 微信登录相关
     */
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 通过WXAPIFactory工厂获取IWXApI的示例
         */
        api = WXAPIFactory.createWXAPI(this, WeChatUtil.getAppId(), true);
        /**
         * 将应用的app_id注册到微信
         */
        api.registerApp(WeChatUtil.getAppId());
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                Toast.makeText(this, "参数不合法，未被SDK处理", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        SendAuth.Resp data = (SendAuth.Resp) baseResp;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                success(baseResp, data);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                cancel(baseResp, data);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                denied(baseResp, data);
                break;
            default:
                back(baseResp, data);
                break;
        }
    }

    /**
     * 登录成功
     *
     * @param baseResp
     * @param resp
     */
    public abstract void success(BaseResp baseResp, SendAuth.Resp resp);

    /**
     * 登录取消
     *
     * @param baseResp
     * @param resp
     */
    public abstract void cancel(BaseResp baseResp, SendAuth.Resp resp);

    /**
     * 登录拒绝
     *
     * @param baseResp
     * @param resp
     */
    public abstract void denied(BaseResp baseResp, SendAuth.Resp resp);

    /**
     * 登录返回
     *
     * @param baseResp
     * @param resp
     */
    public void back(BaseResp baseResp, SendAuth.Resp resp) {

    }


}
