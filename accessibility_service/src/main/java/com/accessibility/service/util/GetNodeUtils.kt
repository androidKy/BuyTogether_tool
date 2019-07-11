package com.accessibility.service.util

import com.accessibility.service.base.BaseAccessibilityService
import com.accessibility.service.listener.NodeFoundListener

/**
 * Description:
 * Created by Quinin on 2019-07-11.
 **/
class GetNodeUtils {

    companion object {
        fun getNodeByText(nodeService: BaseAccessibilityService, text: String, nodeFoundListener: NodeFoundListener) {
            NodeUtils()
                .setNodeFoundListener(nodeFoundListener)
                .getNodeByText(nodeService, text)
        }

        fun getNodeByText(
            timeOut: Int,
            nodeService: BaseAccessibilityService,
            text: String,
            nodeFoundListener: NodeFoundListener
        ) {
            NodeUtils()
                .setTimeOut(timeOut)
                .setNodeFoundListener(nodeFoundListener)
                .getNodeByText(nodeService, text)
        }

        fun getNodeByFullText(
            nodeService: BaseAccessibilityService,
            text: String,
            nodeFoundListener: NodeFoundListener
        ) {
            NodeUtils()
                .setNodeFoundListener(nodeFoundListener)
                .getNodeByFullText(nodeService, text)
        }

        fun getNodeByFullText(
            timeOut: Int,
            nodeService: BaseAccessibilityService,
            text: String,
            nodeFoundListener: NodeFoundListener
        ) {
            NodeUtils()
                .setTimeOut(timeOut)
                .setNodeFoundListener(nodeFoundListener)
                .getNodeByFullText(nodeService, text)
        }

        fun getNodeById(nodeService: BaseAccessibilityService, id: String, nodeFoundListener: NodeFoundListener) {
            NodeUtils()
                .setNodeFoundListener(nodeFoundListener)
                .getNodeById(nodeService, id)
        }

    }
}