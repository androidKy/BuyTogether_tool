package com.buy.together.fragment.view

import com.accessibility.service.data.TaskBean
import com.buy.together.base.BaseView

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
interface MainView : BaseView {
    fun onResponVersionUpdate()
    fun onResponTask(taskBean: TaskBean)
    fun onParseDatas(taskData: ArrayList<ArrayList<String>>)
    fun onClearDataResult(result: String)
    fun onResponVpnResult(result:Boolean)
}