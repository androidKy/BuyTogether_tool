package com.accessibility.service.auto

import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.CMDUtil
import com.accessibility.service.util.ThreadUtils
import com.safframework.log.L


/**
 * Description: adb命令脚本控制
 * Created by Quinin on 2019-07-13.
 **/
class AdbScriptController private constructor() {

    companion object {
        val instance: AdbScriptController by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AdbScriptController()
        }

        const val DEFAULT_NEXT_DELAY_TIME = 1000L
    }

    var cmdList: ArrayList<String> = ArrayList()     //执行的命令
    var nextDelayTime: ArrayList<Long> = ArrayList()    //执行下一次操作延迟的时间 默认是2秒
    var taskListener: TaskListener? = null

    class Builder {
        var cmdList: ArrayList<String> = ArrayList()     //执行的命令
        var nextDelayTimeList: ArrayList<Long> = ArrayList()    //执行下一次操作延迟的时间 默认是2秒
        var taskListener: TaskListener? = null

        fun setXY(xy: String): Builder {
            setXY(xy, DEFAULT_NEXT_DELAY_TIME)
            return this@Builder
        }

        fun setXY(xy: String, nextDelayTime: Long): Builder {
            val xyList = xy.split(",")
            this.cmdList.add("input tap ${xyList[0]} ${xyList[1]}")
            this.nextDelayTimeList.add(nextDelayTime)
            return this@Builder
        }

        fun setText(text: String): Builder {
            setText(text, DEFAULT_NEXT_DELAY_TIME)
            return this@Builder
        }

        fun setText(text: String, nextDelayTime: Long): Builder {
            this.nextDelayTimeList.add(nextDelayTime)
            this.cmdList.add("am broadcast -a ADB_INPUT_TEXT --es msg '$text'")
            return this@Builder
        }

        fun setTaskListener(taskListener: TaskListener): Builder {
            this.taskListener = taskListener
            return this@Builder
        }

        fun create(): AdbScriptController {
            val adbScriptController = instance
            adbScriptController.cmdList = cmdList
            adbScriptController.nextDelayTime = nextDelayTimeList
            adbScriptController.taskListener = taskListener

            return adbScriptController
        }
    }

    fun execute() {
        L.i("cmdList size = ${cmdList.size}")
        if (cmdList.size == 0)
            return

        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                var result: Boolean = false

                try {
                    for (i in 0 until cmdList.size) {
                        L.i("执行命令：${cmdList[i]}")
                        Thread.sleep(nextDelayTime[i])
                        CMDUtil().execCmd(cmdList[i])

                        if (i == cmdList.size - 1)
                            result = true
                    }
                } catch (e: Exception) {
                    L.e(e.message, e)
                }

                return result
            }

            override fun onSuccess(result: Boolean?) {
                L.i("执行命令结果： $result")
                taskListener?.onTaskFinished()
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