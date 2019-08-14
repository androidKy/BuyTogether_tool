package com.utils.common.accessibility.auto

/**
 * Description: adb点击的屏幕坐标
 * Created by Quinin on 2019-07-15.
 **/
class ADB_XY {


    class PAY_NOW {       //立即支付界面
        companion object {
            const val add_address: String = "480,270" //手动添加地址
            const val name: String = "300,540"   //收货地址的屏幕坐标
            const val phone: String = "750,540"  //联系方式
            const val detailed: String = "540,830"   //详细地址

            const val origin_swipe_up: String = "540,1400"   //向上滑动的起点
            const val target_swipe_up: String = "540,900"   //向上滑动的终点
            const val more_pay_channel = "540,1370"    //更多支付方式
            const val wechat_pay = "540,1240"  //微信支付
            const val qq_pay = "540,1455"  //QQ支付
            // const val pay_now_btn = "865,1700"    //立即支付,nexus5
            const val pay_now_btn = "865,1800"    //立即支付，小米4
            // const val ali_pay = "540,1300" //支付宝支付
            const val ali_pay = "540,1400" //支付宝支付，小米4

            const val pay_now_qq_btn = "540,1015"   //跳转到QQ界面的立即支付按钮
            const val pay_by_other = "540,1370"     //找好友代付
        }
    }

}