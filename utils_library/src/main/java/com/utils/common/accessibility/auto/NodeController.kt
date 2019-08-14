package com.utils.common.accessibility.auto

import com.safframework.log.L
import com.utils.common.accessibility.base.BaseAccessibilityService
import com.utils.common.accessibility.listener.NodeFoundListener
import com.utils.common.accessibility.listener.TaskListener

/**
 * Description:
 * Created by Quinin on 2019-07-12.
 **/
class NodeController {

    @JvmSynthetic
    private var mLocked = false

    var nodeService: BaseAccessibilityService? = null
    var nodeTextList: ArrayList<String> = ArrayList()
    var nodeClickedList: ArrayList<Boolean> = ArrayList()
    var nodeFlagList: ArrayList<Int> = ArrayList()
    var nodeEditTextList: ArrayList<String> = ArrayList()
    var nodeTimeOutList: ArrayList<Int> = ArrayList()
    var nodeScrolledList: ArrayList<Boolean> = ArrayList()
    var nodeFindNextList: ArrayList<Boolean> = ArrayList()

    var taskListener: TaskListener? = null
    var filterText: String? = null

    class Builder {
        val DEFAULT_FOUND_TIME_OUT: Int = 15
        val DEFAULT_FIND_NEXT_FLAG: Boolean = false
        var nodeService: BaseAccessibilityService? = null
        var nodeTextList: ArrayList<String> = ArrayList()
        var nodeClickedList: ArrayList<Boolean> = ArrayList()
        var nodeScrolledList: ArrayList<Boolean> = ArrayList()
        var nodeFlagList: ArrayList<Int> = ArrayList()
        var nodeEditTextList: ArrayList<String> = ArrayList()
        var nodeTimeOutList: ArrayList<Int> = ArrayList()
        var nodeFindNextList: ArrayList<Boolean> = ArrayList()
        var nodeListener: ArrayList<NodeFoundListener> = ArrayList()

        var taskListener: TaskListener? = null

        var filterText: String? = null

        //var isClicked: Boolean = true   //默认是点击节点
        //var isFoundById: Boolean = false  //false表示是根据内容查找，true表示是根据ID查找


        fun setTaskListener(taskListener: TaskListener): Builder {
            this.taskListener = taskListener
            return this@Builder
        }

        fun setNodeService(nodeService: BaseAccessibilityService): Builder {
            this.nodeService = nodeService
            return this@Builder
        }


        fun setNodeParams(textList: List<String>, isScrolled: Boolean): Builder {
            for (text in textList)
                setNodeParams(text, 0, true, isScrolled, 5, true)

            return this@Builder
        }

        /**
         * 默认根据内容查找，默认是被点击
         */
        fun setNodeParams(text: String): Builder {
            setNodeParams(text, 0)
            return this@Builder
        }

        fun setNodeParams(text: String, findNextFlag: Boolean): Builder {
            setNodeParams(text, 0, true, false, "null", DEFAULT_FOUND_TIME_OUT, findNextFlag)
            return this@Builder
        }

        fun setNodeParams(text: String, nodeFlag: Int, timeout: Int): Builder {
            setNodeParams(text, nodeFlag, true, false, "null", timeout, DEFAULT_FIND_NEXT_FLAG)
            return this@Builder
        }


        fun setNodeParams(text: String, nodeFlag: Int, timeout: Int, findNextFlag: Boolean): Builder {
            setNodeParams(text, nodeFlag, true, false, "null", timeout, findNextFlag)
            return this@Builder
        }


        /**
         * 是否根据ID查找，默认是被点击
         */
        fun setNodeParams(text: String, nodeFlag: Int): Builder {
            setNodeParams(text, nodeFlag, true)
            return this@Builder
        }

        fun setNodeParams(text: String, nodeFlag: Int, isClicked: Boolean): Builder {
            setNodeParams(text, nodeFlag, isClicked, "null")
            return this@Builder
        }

        fun setNodeParams(text: String, nodeFlag: Int, isClicked: Boolean, timeout: Int): Builder {
            setNodeParams(text, nodeFlag, isClicked, false, "null", timeout, false)
            return this@Builder
        }

        fun setNodeParams(
            text: String,
            nodeFlag: Int,
            isClicked: Boolean,
            timeout: Int,
            findNextFlag: Boolean
        ): Builder {
            setNodeParams(text, nodeFlag, isClicked, false, "null", timeout, findNextFlag)
            return this@Builder
        }

        fun setNodeParams(
            text: String,
            nodeFlag: Int,
            isClicked: Boolean,
            isScrolled: Boolean,
            timeout: Int,
            findNextFlag: Boolean
        ): Builder {
            setNodeParams(text, nodeFlag, isClicked, isScrolled, "null", timeout, findNextFlag)
            return this@Builder
        }


        fun setNodeParams(
            text: String,
            nodeFlag: Int,
            isClicked: Boolean,
            isScrolled: Boolean,
            editorInputText: String
        ): Builder {
            setNodeParams(
                text,
                nodeFlag,
                isClicked,
                isScrolled,
                editorInputText,
                DEFAULT_FOUND_TIME_OUT,
                DEFAULT_FIND_NEXT_FLAG
            )
            return this@Builder
        }

        fun setNodeParams(text: String, nodeFlag: Int, isClicked: Boolean, editInputText: String): Builder {
            setNodeParams(
                text,
                nodeFlag,
                isClicked,
                false,
                editInputText,
                DEFAULT_FOUND_TIME_OUT,
                DEFAULT_FIND_NEXT_FLAG
            )
            return this@Builder
        }

        /**
         * textList:根据text来查找
         * nodeFlag:根据这个字段来判断是哪种方式查找，0：根据view text全查找，1：根据view text半查找，2：根据ID查找，3：根据className查找
         * isClicked:判断是否点击查找的节点
         * editInputText:是否是EditText节点输入内容
         * foundNodeTimeOut：节点查找超时时间 单位秒
         * findNextFlag:当前节点查找失败后，是否继续查找下一个节点，默认是false，不继续查找，true为继续查找
         * nodeListener:节点查找的结果监听
         *
         */
        fun setNodeParams(
            text: String,
            nodeFlag: Int,
            isClicked: Boolean,
            isScrolled: Boolean,
            editInputText: String,
            foundNodeTimeOut: Int,
            findNextFlag: Boolean
        ): Builder {
            nodeTextList.add(text)
            nodeClickedList.add(isClicked)
            nodeFlagList.add(nodeFlag)
            nodeEditTextList.add(editInputText)
            nodeTimeOutList.add(foundNodeTimeOut)
            nodeScrolledList.add(isScrolled)
            nodeFindNextList.add(findNextFlag)
            return this@Builder
        }


        /**
         * 如果找到该节点则过滤后面的节点
         */
        fun setNodeFilter(filterText: String): Builder {
            this.filterText = filterText
            return this@Builder
        }

        fun create(): NodeController {
            val nodeController = NodeController()
            nodeController.nodeService = nodeService
            nodeController.taskListener = taskListener
            nodeController.nodeTextList = nodeTextList
            nodeController.nodeClickedList = nodeClickedList
            nodeController.nodeScrolledList = nodeScrolledList
            nodeController.nodeFlagList = nodeFlagList
            nodeController.nodeEditTextList = nodeEditTextList
            nodeController.nodeTimeOutList = nodeTimeOutList
            nodeController.filterText = filterText
            nodeController.nodeFindNextList = nodeFindNextList

            return nodeController
        }
    }

    @Synchronized
    fun execute() {
        if (mLocked)
            return
        if (nodeService == null) {
            throw IllegalArgumentException("nodeService can not be null")
        }
        L.i("execute : nodeSize = ${nodeTextList.size}")
        nodeService?.apply {
            mLocked = true
            try {
                NodeExecute(
                    this, nodeTextList, nodeClickedList, nodeFlagList, nodeEditTextList,
                    nodeTimeOutList, taskListener!!, filterText, nodeScrolledList, nodeFindNextList
                ).startFindNodeList()
            } catch (e: Exception) {
                L.e(e.message, e)
                taskListener?.onTaskFailed(e.message!!)
            }
        }
    }
}