package com.accessibility.service.page

/**
 * Description:
 * Created by Quinin on 2019-07-02.
 **/
enum class PageEnum(pageDesc: String) {
    START_PAGE("启动界面"),CHOOSING_LOGIN_PAGE("正处于选择登录界面"),QQ_LOGINING_PAGE("正处于QQ登录界面"),
    INDEX_PAGE("首页"), PERSONAL_PAGE("个人中心"),SERARCHING_PAGE("正处于搜索状态"),
    CHOOSE_LOGIN_PAGE("选择登录方式"), QQ_LOGIN_PAGE("QQ登录"), WX_LOGIN_PAGE("微信登录"),
    PHONE_LOGIN_PAGE("手机登录"), AUTH_LOGIN_PAGE("授权登录"), SEARCH_PAGE("搜索界面"),
    SEARCH_RESULT_PAGE("搜索结果"), GOODS_INFO_PAGE("商品详情"), TALK_PAGE("聊天界面"),
    CHOOSE_GOOD_PAGE("选择商品"), WAIT_PAY_PAGE("等待支付"), ADDRESS_PAGE("收货地址"),
    PAYING_PAGE("选择支付方式"), PAY_CONFIRM_PAGE("订单确认支付"),PAY_SUCCEED("支付成功")
}