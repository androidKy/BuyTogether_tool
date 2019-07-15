package com.accessibility.service.auto

/**
 * Description: adb点击的屏幕坐标
 * Created by Quinin on 2019-07-15.
 **/
class ADB_XY {


    class PAY_NOW {       //立即支付界面
        companion object {
            const val add_address: String = "480,270" //手动添加地址
            const val name: String = "300,710"   //收货地址的屏幕坐标
            const val phone: String = "750,710"  //联系方式
            const val choose_local = "510,830"    //选择省,市，区
            const val province: String = "510,855" //省    Y:755 - 1775 / 8
            const val city: String = "510,855"   //城市
            const val region: String = "510,855" //区域
            const val detailed: String = "510,960"   //详细地址
            const val save_address: String = "510,1200"//保存

            const val origin_swipe_up: String = "510,1400"   //向上滑动的起点
            const val target_swipe_up: String = "510,900"   //向上滑动的终点
            const val more_pay_channel = "510,1470"    //更多支付方式
            const val wechat_pay = "510,1240"  //微信支付
            const val ali_pay = "510,1300" //支付宝支付
            const val qq_pay = "510,1455"  //QQ支付
            const val pay_now_btn = "865,1700"    //立即支付

            const val pay_now_qq_btn = "510,1015"   //跳转到QQ界面的立即支付按钮
            const val pay_by_other = "510,1370"     //找好友代付

        }
    }

}