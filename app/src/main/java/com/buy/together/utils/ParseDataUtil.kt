package com.buy.together.utils

import com.accessibility.service.data.CommentBean
import com.accessibility.service.data.TaskBean

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
class ParseDataUtil {
    companion object {
        val task_id = "task_id"
        val task_status = "task_status"
        val task_type = "task_type"
        val task_amount = "task_amount"
        val task_completed = "task_completed"
        val task_failed = "task_failed"
        val talk_msg = "talk_msg"
        val can_comment_time = "can_comment_time"
        val order_id = "order_id"
        val isCommentTask = "isCommentTask"

        val comment_content = "comment_content"
        val ip_address = "ip_address"
        val ip_city = "ip_city"
        val account_name = "account_name"
        val account_psw = "account_psw"
        val goods_id = "goods_id"
        val mall_name = "mall_name"
        val goods_name = "goods_name"
        val goods_keyword = "goods_keyword"
        val choose_info = "choose_info"
        val search_price = "search_price"

        val delivery_address = "delivery_address"


        fun parseTaskBean2HashMap(taskBean: TaskBean): HashMap<String, String> {

            val taskData = taskBean.task
            val hashMap = HashMap<String, String>()
            hashMap[task_id] = taskData.task_id.toString()
            hashMap[task_type] = taskData.task_type.toString()
            hashMap[task_status] = taskData.task_status.toString()
            hashMap[task_amount] = taskData.task_amount.toString()
            hashMap[task_completed] = taskData.task_complete.toString()
            hashMap[task_failed] = taskData.task_fail.toString()
            hashMap[talk_msg] = taskData.talk_msg
            hashMap[comment_content] = taskData.comment_content.toString()

            hashMap[ip_address] = taskData.ip.content
            hashMap[account_name] = taskData.account.user
            hashMap[account_psw] = taskData.account.pwd

            hashMap[goods_id] = taskData.goods.id.toString()
            hashMap[goods_keyword] = taskData.goods.keyword
            hashMap[goods_name] = taskData.goods.goods_name
            hashMap[mall_name] = taskData.goods.mall_name
            hashMap[choose_info] = taskData.goods.choose_info
            hashMap[search_price] = taskData.goods.search_price

            hashMap[delivery_address] =
                "name:${taskData.delivery_address.name} phone:${taskData.delivery_address.phone} " +
                        "\nprovince:${taskData.delivery_address.province} city:${taskData.delivery_address.city} " +
                        "district:${taskData.delivery_address.district} \nstreet:${taskData.delivery_address.street}"
            // val goods = taskData.goods
            /*hashMap[goods_size] = goods.size.toString()
            for (i in 0 until goods.size) {
                hashMap["$goods_id[$i]"] = goods[i].goods_id.toString()
                hashMap["$goods_type[$i]"] = goods[i].goods_type.toString()
                hashMap["$goods_name[$i]"] = goods[i].name
                hashMap["$goods_keyword[$i]"] = goods[i].keyword
                hashMap["$delivery_address[$i]"] = goods[i].delivery_address
                hashMap["$talk_msg[$i]"] = goods[i].talk_msg
                hashMap["$tasked_count[$i]"] = goods[i].tasked_count.toString()
                hashMap["$comment_need[$i]"] = goods[i].isComment_need.toString()
                hashMap["$comment_content[$i]"] = goods[i].comment_content
                hashMap["$score_need[$i]"] = goods[i].isScore_need.toString()
                hashMap["$score_value[$i]"] = goods[i].score_value.toString()
            }*/

            return hashMap
        }

        fun parseCommentTask2HashMap(taskBean: TaskBean): HashMap<String, String> {
            val taskData = taskBean.task
            val hashMap = HashMap<String, String>()
            hashMap[task_id] = taskData.task_id.toString()
            hashMap[task_type] = taskData.task_type.toString()
            hashMap[comment_content] = taskData.comment_content.toString()
            hashMap[can_comment_time] = taskData.can_comment_time.toString()
            hashMap[order_id] = taskData.order_id
            hashMap[isCommentTask] = taskData.isCommentTask.toString()

            hashMap[ip_city] = taskData.delivery_address.city
            hashMap[account_name] = taskData.account.user
            hashMap[account_psw] = taskData.account.pwd

            hashMap[goods_id] = taskData.goods.id.toString()
            //hashMap[goods_keyword] = taskData.goods.keyword
            hashMap[goods_name] = taskData.goods.goods_name
            hashMap[mall_name] = taskData.goods.mall_name
            hashMap[search_price] = taskData.goods.search_price

            return hashMap
        }


        fun parseHashMap2ArrayList(hashMap: HashMap<String, String>): ArrayList<ArrayList<String>> {
            val rawList = ArrayList<ArrayList<String>>()

            val keys = hashMap.keys
            for (key in keys) {
                val raw = ArrayList<String>()
                raw.add(key)
                raw.add(hashMap[key]!!)
                rawList.add(raw)

            }

            return rawList
        }

        fun parseCommentBean2TaskBean(commentBean: CommentBean): TaskBean {
            val taskBean = TaskBean()
            taskBean.code = commentBean.code
            taskBean.msg = commentBean.msg
            val task = TaskBean.TaskData()

            task.task_id = commentBean.task.task_id
            task.comment_content = commentBean.task.comment_content
            task.isCommentTask = true
            task.order_id = commentBean.task.order_id
            task.can_comment_time = commentBean.task.can_comment_time

            val goods = TaskBean.TaskData.GoodsBean()
            goods.id = commentBean.task.goods.id
            goods.goods_id = commentBean.task.goods.goods_id
            goods.goods_name = commentBean.task.goods.goods_name
            goods.choose_info = commentBean.task.goods.choose_info
            goods.mall_name = commentBean.task.goods.mall_name
            goods.search_price = commentBean.task.goods.search_price
            task.goods = goods

            val ip = TaskBean.TaskData.IpBean()
            ip.city = commentBean.task.ip.city
            ip.content = commentBean.task.ip.content
            ip.mac_address = commentBean.task.ip.mac_address
            task.ip = ip

            val delivery_address = TaskBean.TaskData.DeliveryAddressBean()
            delivery_address.city = commentBean.task.ip.city
            task.delivery_address = delivery_address

            val account = TaskBean.TaskData.AccountBean()
            account.id = commentBean.task.account.id
            account.user = commentBean.task.account.user
            account.pwd = commentBean.task.account.pwd
            account.type = commentBean.task.account.type
            task.account = account

            val device = TaskBean.TaskData.DeviceBean()
            device.imei = commentBean.task.device.imei
            device.android = commentBean.task.device.android
            device.brand = commentBean.task.device.brand
            device.bluetooth = commentBean.task.device.bluetooth
            device.id = commentBean.task.device.id
            device.imsi = commentBean.task.device.imsi
            device.mac = commentBean.task.device.mac
            device.model = commentBean.task.device.model
            device.sn = commentBean.task.device.sn
            device.system = commentBean.task.device.system
            device.useragent = commentBean.task.device.useragent
            task.device = device

            taskBean.task = task

            return taskBean
        }
    }

}