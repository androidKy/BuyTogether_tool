package com.accessibility.service.auto

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.AfterClickedListener
import com.accessibility.service.listener.NodeFoundListener
import com.accessibility.service.listener.TaskListener
import com.accessibility.service.util.ScrollUtils
import com.accessibility.service.util.WidgetConstant
import com.safframework.log.L

/**
 * Description:
 * Created by Quinin on 2019-07-12.
 **/
class NodeExecute(
    val nodeService: BaseAccessibilityService, val nodeTextList: ArrayList<String>,
    val nodeClickedList: ArrayList<Boolean>, val nodeFlagList: ArrayList<Int>,
    val nodeEditTextList: ArrayList<String>, val nodeTimeOutList: ArrayList<Int>,
    val taskListener: TaskListener, val filterText: String?, val nodeScrolledList: ArrayList<Boolean>,
    val nodeFindList: ArrayList<Boolean>
) {

    private var mStartTime: Int = 0

    init {
        L.init(NodeExecute::class.java.simpleName)
    }

    companion object {
        const val MSG_NOT_FOUND = 404
        const val MSG_WAS_FOUND = 200
    }


    private val mHandler: Handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MSG_NOT_FOUND -> {
                findNode(it.arg1)
            }
        }
        false
    }

    fun startFindNodeList() {
        if (nodeTextList.size > 0)
            findNode(0)
        else L.i("查找的节点集合为0")
    }

    fun findNode(index: Int) {
        if (index > nodeTextList.size - 1) {
            L.i("index = $index 超过数组范围.")
            return
        }
        val textOrId = nodeTextList[index]
        val isClicked = nodeClickedList[index]
        val nodeFlag = nodeFlagList[index]
        val editInputText = nodeEditTextList[index]
        val timeout = nodeTimeOutList[index]
        val isScrolled = nodeScrolledList[index]

        L.i("node index = $index")
        L.i(
            "开始查找节点：textOrId: $textOrId ; isFoundById: $nodeFlag ; isClicked: $isClicked ; " +
                    "editInputText: $editInputText ; timeout: $timeout ; isScrolled: $isScrolled"
        )

        val nodeResult = findNode(textOrId, nodeFlag)
        if (nodeResult == null && mStartTime <= timeout) {
            mStartTime++
            val message = mHandler.obtainMessage()
            message.arg1 = index
            message.what = MSG_NOT_FOUND
            mHandler.sendMessageDelayed(message, 1000)
        } else if (nodeResult == null) {    //找不到时是否需要下滑查找
            dealNodeFailed(index, textOrId, editInputText, isClicked, isScrolled)
        } else {
            dealNodeSucceed(index, textOrId, editInputText, isClicked, nodeResult)
        }
    }

    fun findNode(textOrId: String, nodeFlag: Int): AccessibilityNodeInfo? {
        var nodeInfo: AccessibilityNodeInfo? = null
        nodeService.apply {
            when (nodeFlag) {
                0 -> nodeInfo = findViewByFullText(textOrId)
                1 -> nodeInfo = findViewByText(textOrId)
                2 -> nodeInfo = findViewById(textOrId)
                3 -> this.rootInActiveWindow?.apply {
                    findViewByClassName(this, textOrId, object : NodeFoundListener {
                        override fun onNodeFound(nodeResult: AccessibilityNodeInfo?) {
                            nodeInfo = nodeResult
                        }
                    })
                }
            }
        }

        return nodeInfo
    }

    fun dealNodeFailed(index: Int, textOrId: String, editInputText: String, isClicked: Boolean, isScrolled: Boolean) {
        mStartTime = 0
        L.i("$textOrId node was not found ")

        if (isScrolled) {
            nodeService.findViewByClassName(
                nodeService.rootInActiveWindow,
                WidgetConstant.RECYCLERVIEW,
                object : NodeFoundListener {
                    override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                        nodeInfo?.apply {
                            ScrollUtils(nodeService, this)
                                .setForwardTotalTime(5)
                                .setNodeText(textOrId)
                                .setScrollListener(object : ScrollUtils.ScrollListener {
                                    override fun onScrollFinished(nodeInfo: AccessibilityNodeInfo) {
                                        L.i("下滑完成")
                                    }
                                })
                                .setNodeFoundListener(object : NodeFoundListener {
                                    override fun onNodeFound(nodeInfo: AccessibilityNodeInfo?) {
                                        if (nodeInfo == null) taskListener.onTaskFailed(textOrId)
                                        L.i("下滑找到节点：${nodeInfo?.text}")
                                        nodeInfo?.apply {
                                            dealNodeSucceed(index, textOrId, editInputText, isClicked, this)
                                        }
                                    }
                                })
                                .scrollForward()
                        }
                    }
                })
            return
        }

        if (index < nodeTextList.size - 1) {   //当查找一个节点通过多种方法时
            if (TextUtils.isEmpty(filterText)) {
                if (nodeFindList[index])    //
                    findNode(index + 1)
                else taskListener.onTaskFailed(textOrId)
            } else {  //如果filterText存在，过滤后面的节点
                findNode(filterText!!, 0)?.apply {
                    taskListener.onTaskFinished()
                }
            }
        } else {
            taskListener.onTaskFailed(textOrId)
        }
    }

    fun dealNodeSucceed(
        index: Int,
        textOrId: String,
        editInputText: String,
        isClicked: Boolean,
        nodeResult: AccessibilityNodeInfo
    ) {
        mStartTime = 0
        L.i("nodeResult: ${nodeResult.text}")
        nodeResult.apply {
            if (editInputText != "null") {
                WidgetConstant.setEditText(editInputText, this)
            }

            if (isClicked)  //点击
                nodeService.performViewClick(this, 1, object : AfterClickedListener {
                    override fun onClicked() {
                        L.i("$textOrId was clicked")
                        if (index == nodeTextList.size - 1) {
                            taskListener.onTaskFinished()
                        } else
                            findNode(index + 1)
                    }
                })
            else {  //不点击，直接找下一个节点
                if (index == nodeTextList.size - 1)
                    taskListener.onTaskFinished()
                else
                    findNode(index + 1)
            }
        }
    }
}