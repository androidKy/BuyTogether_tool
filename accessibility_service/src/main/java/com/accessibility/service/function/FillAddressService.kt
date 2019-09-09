package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.ADB_XY
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.TaskDataUtil
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-08-02.
 **/
class FillAddressService constructor(private val nodeService: MyAccessibilityService) {
    /* companion object :
         com.utils.common.SingletonHolder<FillAddressService, MyAccessibilityService>(::FillAddressService)*/
    val taskDataUtil = TaskDataUtil.instance
    var buyerName = taskDataUtil.getBuyer_name()
    val buyerPhone = taskDataUtil.getBuyer_phone()
    var streetName = taskDataUtil.getStreet()

    // 超出配送范围时，填入一个浙江省，杭州市，西湖区，保证可以配送
    var fakeProvince: String = "浙江省"
    var fakeCity: String = "杭州市"
    var fakeDistrict: String = "西湖区"


    var mTaskFinishedListener: TaskListener? = null
    fun setTaskFinishedListener(taskFinishedListener: TaskListener): FillAddressService {
        this.mTaskFinishedListener = taskFinishedListener
        return this
    }

    fun doOnEvent() {
        isAddressExist()
    }

    /**
     * 校验地址是否存在
     */
    private fun isAddressExist() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("手动添加收货地址", 0, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("地址不存在,新增地址")
                    addAddress()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("地址已存在")
                    //chooseExistAddress()
//                    responSuccess()
                    // todo 地址已存在，先判断是否在配送范围，再验证是否相同收件人
                    if (buyerName != null) {
                        verifySameAddressee()
                    }
                }
            })
            .create()
            .execute()

        /* AdbScriptController.Builder()
             .setXY(ADB_XY.PAY_NOW.add_address, 3000L)
             .setNodeFoundListener(object : TaskListener {
                 override fun onTaskFinished() {
                     NodeController.Builder()
                         .setNodeService(nodeService)
                         .setNodeParams("收货地址", 0, false, 3)
                         .setNodeFoundListener(object : TaskListener {
                             override fun onTaskFinished() {

                             }

                             override fun onTaskFailed(failedMsg: String) {

                             }
                         })
                         .create()
                         .execute()
                 }

                 override fun onTaskFailed(failedMsg: String) {
                     //支付失败
                     L.i("$failedMsg was not found.")
                     responFailed("输入收货地址时，应用未获得root权限")
                     //payByOther()
                 }
             })
             .create()
             .execute()*/
    }

    /**
     *  检验与服务器传入的收件人 是否一致。
     */
    private fun verifySameAddressee() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(buyerName!!, 1, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("收件人与服务器一致，直接进行下一步支付")
                    // 再判断是否再配送范围，不支持则充填
                    verifyAvailableArea()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("收件人不一致，需要重新填写。")
                    // 可以不需要用 ADB命令查找，利用半查找。
                    reFillAddress()
                }

            })
            .create()
            .execute()
    }

    /**
     *  检测地址是否再配送范围，不在则
     */
    private fun verifyAvailableArea() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("不支持", 1, true, 5)
            .setNodeParams("编辑", 0, true, 10)
            .setNodeParams("市", 1, true, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    fillFakeAddressInfo()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responSuccess()
                }

            })
            .create()
            .execute()
    }

    private fun fillFakeAddressInfo() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(fakeProvince, 0, true, true, 10, true)
            .setNodeParams(fakeCity, 0, true, true, 3, true)
            .setNodeParams(fakeDistrict, 0, true, true, 3, true)
            .setNodeParams("保存", 0)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    nodeService.performBackClick(2, object : AfterClickedListener {
                        override fun onClicked() {
                            responSuccess()
                        }
                    })
                }

                override fun onTaskFailed(failedMsg: String) {
                }
            })
            .create()
            .execute()
    }

    private fun reFillAddress() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("市", 1, true, 10, true)  //todo 再次判断是否有相同名字的收货人，如果有直接选择
            .setNodeParams("添加收货地址", 0, true, 10)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("准备更换收货地址")
                    fillAddress()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("更换地址失效")
                }
            })
            .create()
            .execute()
    }

    /**
     * 选择已存在的地址
     */
    private fun chooseExistAddress() {
        AdbScriptController.Builder()
            .setXY("540,310")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("选择地址时，应用未获得root权限")
                }
            })
            .create()
            .execute()
    }

    /**
     * 新增地址
     */
    private fun addAddress() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("添加收货地址", 0, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    fillAddress()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("点击了商家回复弹出了聊天框")
                    back2address()
                }
            })
            .create()
            .execute()
    }

    /**
     * 从聊天界面返回输入地址的界面
     */
    private fun back2address() {
        nodeService.performBackClick(2, object : AfterClickedListener {
            override fun onClicked() {
                isAddressExist()
            }
        })
    }

    /**
     * 填入地址
     */
    private fun fillAddress() {

        if (buyerName.isNullOrEmpty() || streetName.isNullOrEmpty()) {
            responFailed("收货信息为空")
            return
        }
        if (buyerName != null) {
            buyerName = buyerName!!.substring(0, buyerName!!.length - 1) + "*"
        }


        L.i("收货人：$buyerName 电话：$buyerPhone streetName:$streetName")
        /*if (streetName.contains("市")) {
            streetName = streetName.split("市")[1]
        }*/

        AdbScriptController.Builder()
            .setXY(ADB_XY.PAY_NOW.name)
            .setText(buyerName!!)
            .setXY(ADB_XY.PAY_NOW.phone)
            .setXY(ADB_XY.PAY_NOW.phone)
            .setText(buyerPhone!!)
            .setXY(ADB_XY.PAY_NOW.detailed)
            .setText(streetName!!)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseProvince()
                }

                override fun onTaskFailed(failedMsg: String) {
                    //支付失败
                    responFailed("应用未获得root权限")
                }

            })
            .create()
            .execute()
    }

    /**
     *  初始化填入地址 需要的信息。
     */


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

                override fun onTaskFailed(failedMsg: String) {
                    L.i("$failedMsg was not found.")
                    responFailed("选择${provinceName}失败")
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
                .setXY("540,920")
                .setTaskListener(object : TaskListener {
                    override fun onTaskFinished() {
                        chooseDistrict(districtName)
                    }

                    override fun onTaskFailed(failedMsg: String) {
                        responFailed("选择${cityName}失败")
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

                    override fun onTaskFailed(failedMsg: String) {
                        //responFailed("选择${cityName}失败")
                        chooseCityFailed(cityName, districtName)
                    }
                })
                .create()
                .execute()
        }
    }

    private fun chooseCityFailed(cityName: String, districtName: String) {
        AdbScriptController.Builder()
            .setXY("540,920")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    chooseDistrict(districtName)
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("选择${cityName}失败")
                }
            })
            .create()
            .execute()
    }

    /**
     * 选择区
     */
    private fun chooseDistrict(districtName: String) {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams(districtName, 0, true, true, 3, true)         //市
            .setNodeParams("保存")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    // responFailed("选择${districtName}失败") //选择区失败，随便选择一个区
//                    chooseDistrictFailed(districtName)
                    chooseOtherDistrict()
                }
            })
            .create()
            .execute()
    }

    private fun chooseOtherDistrict() {
        NodeController.Builder()
            .setNodeService(nodeService)
            .setNodeParams("其他区", 0, true, true, 3, true)
            .setNodeParams("保存", 0, true)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("填写地址出错")
                }

            })
            .create()
            .execute()

    }

    private fun chooseDistrictFailed(districtName: String) {
        AdbScriptController.Builder()
            .setXY("540,920")
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    responSuccess()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed("选择${districtName}失败")
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
