package com.buy.together.receiver;

import com.buy.together.utils.NetUtils;

/**
 * Description:
 * Created by Quinin on 2019-09-16.
 **/
public interface NetChangeObserver {
    /**
     * 网络连接回调 type为网络类型
     */
    void onNetConnected(NetUtils.NetType type);

    /**
     * 没有网络
     */
    void onNetDisConnect();
}
