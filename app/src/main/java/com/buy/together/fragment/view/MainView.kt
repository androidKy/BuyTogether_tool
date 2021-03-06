package com.buy.together.fragment.view

import com.buy.together.base.BaseView
import com.buy.together.bean.CloseProxyBean
import com.accessibility.service.data.TaskBean

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
interface MainView : BaseView {
    fun onResponTask(taskBean: TaskBean)
    fun onParseDatas(taskData: ArrayList<ArrayList<String>>)
    fun onClearDataResult(result: String)
    fun onRequestPortsResult(result: String)
    fun onResponPortsFailed(errorMsg:String)
    fun onResponClosePort(closeProxyBean: CloseProxyBean?)
    fun onResponVpnResult(result:Boolean)
}