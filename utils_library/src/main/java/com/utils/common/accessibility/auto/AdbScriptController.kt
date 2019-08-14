package com.utils.common.accessibility.auto

import com.safframework.log.L
import com.utils.common.CmdListUtil
import com.utils.common.accessibility.listener.TaskListener


/**
 * Description: adb命令脚本控制
 * Created by Quinin on 2019-07-13.
 **/
class AdbScriptController private constructor() {

    companion object {
        val instance: AdbScriptController by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AdbScriptController()
        }

        const val DEFAULT_DELAY_TIME = 1000L
    }

    var cmdList: ArrayList<String> = ArrayList()     //执行的命令
    var delayTime: ArrayList<Long> = ArrayList()    //执行下一次操作延迟的时间 默认是2秒
    var taskListener: TaskListener? = null

    class Builder {
        var cmdList: ArrayList<String> = ArrayList()     //执行的命令
        var delayTimeList: ArrayList<Long> = ArrayList()    //执行下一次操作延迟的时间 默认是2秒
        var taskListener: TaskListener? = null

        fun setXY(xyList: List<String>): Builder {
            for (i in 0 until xyList.size) {
                setXY(xyList[i], DEFAULT_DELAY_TIME)
            }

            return this@Builder
        }

        fun setXY(xyList: List<String>, delayTime: Long): Builder {
            for (i in 0 until xyList.size) {
                setXY(xyList[i], delayTime)
            }

            return this@Builder
        }

        fun setXY(xy: String): Builder {
            setXY(xy, DEFAULT_DELAY_TIME)
            return this@Builder
        }

        fun setXY(xy: String, delayTime: Long): Builder {
            val xyList = xy.split(",")
            this.cmdList.add("input tap ${xyList[0]} ${xyList[1]}")
            this.delayTimeList.add(delayTime)
            return this@Builder
        }

        fun setText(text: String): Builder {
            setText(text, DEFAULT_DELAY_TIME)
            return this@Builder
        }

        fun setText(text: String, delayTime: Long): Builder {
            this.delayTimeList.add(delayTime)
            this.cmdList.add("am broadcast -a ADB_INPUT_TEXT --es msg '$text'")
            return this@Builder
        }

        fun setSwipeXY(originXY: String, targetXY: String): Builder {
            setSwipeXY(originXY, targetXY, DEFAULT_DELAY_TIME)
            return this@Builder
        }

        fun setSwipeXY(originXY: String, targetXY: String, delayTime: Long): Builder {
            val origin_xy = originXY.split(",")
            val target_xy = targetXY.split(",")
            this.delayTimeList.add(delayTime)
            this.cmdList.add("input swipe ${origin_xy[0]} ${origin_xy[1]} ${target_xy[0]} ${target_xy[1]}")

            return this@Builder
        }

        fun setTaskListener(taskListener: TaskListener): Builder {
            this.taskListener = taskListener
            return this@Builder
        }

        fun create(): AdbScriptController {
            val adbScriptController = instance
            adbScriptController.cmdList = cmdList
            adbScriptController.delayTime = delayTimeList
            adbScriptController.taskListener = taskListener

            return adbScriptController
        }
    }

    fun execute() {
        //L.i("cmdList size = ${cmdList.size}")
        if (cmdList.size == 0) {
            taskListener?.onTaskFailed("CMD命令为空")
            return
        }

        com.utils.common.ThreadUtils.executeByCached(object : com.utils.common.ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                try {
                    var cmdStr = ""
                    for (i in 0 until cmdList.size) {
                        //L.i("执行命令：${cmdList[i]}")
                        cmdStr += cmdList[i] + ";"
                        Thread.sleep(delayTime[i])
                        /*com.utils.common.CMDUtil().execCmd(cmdList[i])

                        if (i == cmdList.size - 1)
                            result = true*/
                    }
                    L.i("执行adb命令：$cmdStr")
                    CmdListUtil.getInstance().execCmd(cmdStr)
                    /*if (cmdResult.contains("completed"))
                        result = true*/
                } catch (e: Exception) {
                    L.e(e.message, e)
                }

                return true
            }

            override fun onSuccess(result: Boolean?) {
                //L.i("执行命令结果： $result")
                if (result!!)
                    taskListener?.onTaskFinished()
                else taskListener?.onTaskFailed("adb命令执行错误")
            }

            override fun onCancel() {
                L.i("命令执行取消")
                taskListener?.onTaskFailed("命令执行取消")
            }

            override fun onFail(t: Throwable?) {
                L.e(t?.message, t!!)
                taskListener?.onTaskFailed(t.message!!)
            }
        })
    }
}