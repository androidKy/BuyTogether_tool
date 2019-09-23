package com.accessibility.service.function

import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.auto.AdbScriptController
import com.accessibility.service.auto.NodeController
import com.accessibility.service.base.BaseAcService
import com.accessibility.service.listener.TaskListener
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-09-23.
 **/
class ChoosePictureService(val myAccessibilityService: MyAccessibilityService) :
    BaseAcService(myAccessibilityService) {

    private var mPictureList: ArrayList<String> = ArrayList<String>()

    fun setPictureList(pictureList: List<String>): ChoosePictureService {
        mPictureList.clear()
        mPictureList.addAll(pictureList)

        return this
    }

    override fun startService() {
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("上传图片",1,2)
            .setNodeParams("从相册选取",1,4)
            .setNodeParams("允许",0,2,true)
            .setNodeParams("允许",0,2,true)
            .setNodeParams("相机", 1, false, 5)
            .setTaskListener(object : TaskListener {
                override fun onTaskFinished() {
                    L.i("已跳转到选择照片的界面")
                    startChoose()
                }

                override fun onTaskFailed(failedMsg: String) {
                    L.i("跳转到选择照片界面失败: $failedMsg")
                    responFailed(failedMsg)
                }
            })
            .create()
            .execute()
    }

    /**
     * 开始选择照片
     */
    private fun startChoose() {
        val xyList = picture6position()
        AdbScriptController.Builder()
            .setXY(xyList)
            .setTaskListener(object:TaskListener{
                override fun onTaskFinished() {
                    finishChoosed()
                }

                override fun onTaskFailed(failedMsg: String) {

                }

            })
            .create()
            .execute()
    }

    /**
     * 选择完成
     */
    private fun finishChoosed(){
        NodeController.Builder()
            .setNodeService(myAccessibilityService)
            .setNodeParams("完成",1,2)
            .setNodeParams("上传",1,4)
            .setTaskListener(object:TaskListener{
                override fun onTaskFinished() {
                    responSucceed()
                }

                override fun onTaskFailed(failedMsg: String) {
                    responFailed(failedMsg)
                }
            })
            .create()
            .execute()
    }


    private fun picture6position(): ArrayList<String> {
        val pictureSize = mPictureList.size
        L.i("pictureSize:$pictureSize")
        val picture6positions = ArrayList<String>()
        val startXposition = 290
        val startYposition= 305
        for (i in 0 until pictureSize) {
            if (i<=2)
            {
                picture6positions.add("${startXposition+i*360},$startYposition")
            }else{
                picture6positions.add("${startXposition+(i-3)*360},${startYposition+360}")
            }
        }

        return picture6positions
    }
}