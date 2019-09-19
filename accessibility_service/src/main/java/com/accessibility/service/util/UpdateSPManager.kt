package com.accessibility.service.util

import android.content.Context

/**
 * Description:更新任务数据
 * Created by Quinin on 2019-07-30.
 **/
class UpdateSPManager(val context: Context) {

    /**
     * 更新任务是否完成，如果未完成，重新获取任务时，上报任务失败
     * 0:已开始，未完成 1：已完成 其它：未开始
     */
   /* fun updateTaskStatus(taskStatus: Int) {
        SPUtils.getInstance(context, Constant.SP_TASK_FILE_NAME).put(Constant.KEY_TASK_STATUS, taskStatus)
    }*/
}