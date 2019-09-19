package com.accessibility.service.base

import android.util.SparseIntArray
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.util.Constant
import com.safframework.log.L
import com.utils.common.SPUtils

/**
 * Description:支付的基类
 * Created by Quinin on 2019-09-18.
 **/
abstract class BasePayService(val myAccessibilityService: MyAccessibilityService):BaseAcService(myAccessibilityService) {
    /**
     * 根据密码找到对应的xy坐标
     */
    fun regularPsw(payPsw: String): ArrayList<String> {
        val payPswXYList: ArrayList<String> = ArrayList<String>()
        try {
            val itemWidth =
                SPUtils.getInstance(myAccessibilityService, Constant.SP_DEVICE_PARAMS).getInt(
                    Constant.KEY_SCREEN_WIDTH,
                    1080
                ) / 3
            //val itemHeight = 140
            //val itemStartY = 1135
            val itemHeight = 150
            val itemStartY = 1280


            val xNumberKey = SparseIntArray()
            xNumberKey.put(1, itemWidth / 2)
            xNumberKey.put(2, itemWidth + itemWidth / 2)
            xNumberKey.put(3, itemWidth * 2 + itemWidth / 2)
            xNumberKey.put(4, xNumberKey[1])
            xNumberKey.put(5, xNumberKey[2])
            xNumberKey.put(6, xNumberKey[3])
            xNumberKey.put(7, xNumberKey[1])
            xNumberKey.put(8, xNumberKey[2])
            xNumberKey.put(9, xNumberKey[3])
            xNumberKey.put(0, xNumberKey[2])

            val yNumberKey = SparseIntArray()
            yNumberKey.put(1, itemStartY + itemHeight / 2)
            yNumberKey.put(2, itemStartY + itemHeight / 2)
            yNumberKey.put(3, itemStartY + itemHeight / 2)
            yNumberKey.put(4, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(5, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(6, itemStartY + itemHeight + itemHeight / 2)
            yNumberKey.put(7, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(8, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(9, itemStartY + itemHeight * 2 + itemHeight / 2)
            yNumberKey.put(0, itemStartY + itemHeight * 3 + itemHeight / 2)

            val payPswCharArray = payPsw.toCharArray()

            for (i in 0 until payPswCharArray.size) {
                val pswInt = payPswCharArray[i].toString().toInt()
                val pswXY = "${xNumberKey[pswInt]},${yNumberKey[pswInt]}"
                L.i("$pswInt 坐标：$pswXY")
                payPswXYList.add(pswXY)
            }
        } catch (e: Exception) {
            L.e(e.message, e)
        }

        return payPswXYList
    }

    /**
     * 匹配公司子账号的密码
     */
    fun regularPswCompany(): ArrayList<String> {
        val payPswXYList: ArrayList<String> = ArrayList<String>()
        try {
            val lowerBigSwitch = "80,1685"  //切换大小写字母的坐标
            val xyAa = "110,1520"           //字母A的坐标
            val xyNumberSwitch = "80,1840"  //切换为数字的坐标
            payPswXYList.add(lowerBigSwitch)
            payPswXYList.add(xyAa)      //大写字母a
            payPswXYList.add(lowerBigSwitch)
            payPswXYList.add(xyAa)  //小写字母A
            payPswXYList.add(xyNumberSwitch)

            val itemWidth =
                SPUtils.getInstance(myAccessibilityService, Constant.SP_DEVICE_PARAMS).getInt(
                    Constant.KEY_SCREEN_WIDTH,
                    1080
                ) / 10

            val payPsw = "870843"
            val xNumberKey = SparseIntArray()
            xNumberKey.put(1, itemWidth / 2)
            xNumberKey.put(2, itemWidth + itemWidth / 2)
            xNumberKey.put(3, itemWidth * 2 + itemWidth / 2)
            xNumberKey.put(4, itemWidth * 3 + itemWidth / 2)
            xNumberKey.put(5, itemWidth * 4 + itemWidth / 2)
            xNumberKey.put(6, itemWidth * 5 + itemWidth / 2)
            xNumberKey.put(7, itemWidth * 6 + itemWidth / 2)
            xNumberKey.put(8, itemWidth * 7 + itemWidth / 2)
            xNumberKey.put(9, itemWidth * 8 + itemWidth / 2)
            xNumberKey.put(0, itemWidth * 9 + itemWidth / 2)

            val payPswCharArray = payPsw.toCharArray()

            for (i in 0 until payPswCharArray.size) {
                val pswInt = payPswCharArray[i].toString().toInt()
                val pswXY = "${xNumberKey[pswInt]},1360"
               // L.i("$pswInt 坐标：$pswXY")
                payPswXYList.add(pswXY)
            }
            payPswXYList.add("920,980")     //付款
        } catch (e: Exception) {
            L.e(e.message,e)
        }

        return payPswXYList
    }
}