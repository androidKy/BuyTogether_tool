package com.accessibility.service.util

/**
 * Description:通过ADB命令控制滑动
 * Created by Quinin on 2019-07-27.
 **/
class AdbScrollUtils {
    companion object{
        const val SCROLL_TIMEOUT:Long = 60*1000  //滑动超时时间60秒
        const val SCROLL_DEFAULT_TIME:Long = 8*1000 //默认的滑动时间
    }
    private var mScrollTime: Long = SCROLL_DEFAULT_TIME   //滑动的时间
}