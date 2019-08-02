package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.ADB_XY
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-08-02.
 **/
class FillAddressService private constructor(private val nodeService: MyAccessibilityService) {
    companion object :
        com.utils.common.SingletonHolder<FillAddressService, MyAccessibilityService>(::FillAddressService)

    var mTaskFinishedListener: TaskListener? = null
    fun setTaskFinishedListener(taskFinishedListener: TaskListener): FillAddressService {
        this.mTaskFinishedListener = taskFinishedListener
        return this
    }

    fun doOnEvent() {
        addAddress()
    }

    private fun addAddress() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("添加收货地址", 0, false)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("收货地址不是H5页面")
                    fillAddress()
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("收货地址是H5页面")
                }
            })
            .create()
            .execute()
    }


    private fun fillAddress() {
        val taskDataUtil = TaskDataUtil.instance
        val buyerName = taskDataUtil.getBuyer_name()
        val buyerPhone = taskDataUtil.getBuyer_phone()
        var streetName = taskDataUtil.getStreet()


        if (buyerName.isNullOrEmpty() || streetName.isNullOrEmpty()) {
            responFailed("收货信息为空")
            return
        }

        /*if (streetName.contains("市")) {
            streetName = streetName.split("市")[1]
        }*/

        AdbScriptController.Builder()
            .setXY(ADB_XY.PAY_NOW.name)
            .setText(buyerName)
            .setXY(ADB_XY.PAY_NOW.phone)
            .setText(buyerPhone!!)
            .setXY(ADB_XY.PAY_NOW.detailed)
            .setText(streetName)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseProvince()
                }

                override fun onTaskFailed(failedText: String) {
                    //支付失败
                    responFailed("应用未获得root权限")
                }

            })
            .create()
            .execute()
    }

    /**
     * 选择省
     */
    private fun chooseProvince() {
        val taskDataUtil = TaskDataUtil.instance
        var provinceName = taskDataUtil.getProvince()
        var cityName = taskDataUtil.getCity()
        var districtName = taskDataUtil.getDistrict()

        L.i("省：$provinceName 市：$cityName 区：$districtName")
      /*  provinceName = "北京市"
        cityName = "北京市"
        districtName = "朝阳区"*/

        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("选择地区")
            .setNodeParams(provinceName!!, 0, true, true, 3, false)         //省
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseCity(cityName!!, districtName!!)
                }

                override fun onTaskFailed(failedText: String) {
                    L.i("$failedText was not found.")
                    responFailed("选择$provinceName 省失败")
                }

            })
            .create()
            .execute()
    }

    /**
     * 选择市
     * 如果是直辖市，通过点击：北京市，天津市，上海市，重庆市
     */
    private fun chooseCity(cityName: String, districtName: String) {
        if (cityName == "北京市" || cityName == "天津市" || cityName == "上海市" || cityName == "重庆市") {
            AdbScriptController.Builder()
                .setXY("540,1005")
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        chooseDistrict(districtName)
                    }

                    override fun onTaskFailed(failedText: String) {
                        responFailed("选择$cityName 城市失败")
                    }
                })
                .create()
                .execute()
        } else {
            NodeController.Builder()
                .setNodeService(nodeService)
                .setNodeParams(cityName, 0, true, true, 3, false)         //市
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        chooseDistrict(districtName)
                    }

                    override fun onTaskFailed(failedText: String) {
                        responFailed("选择$cityName 城市失败")
                    }
                })
                .create()
                .execute()
        }
    }

    /**
     * 选择区
     */
    private fun chooseDistrict(districtName: String) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(districtName, 0, true, true, 3, false)         //市
            .setNodeParams("保存")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedText: String) {
                    responFailed("选择$districtName 区失败")
                }
            })
            .create()
            .execute()
    }


    fun responFailed(failedMsg: String) {
        mTaskFinishedListener?.onTaskFailed(failedMsg)
    }

    private fun responSuccess() {
        mTaskFinishedListener?.onTaskFinished()
    }
}
