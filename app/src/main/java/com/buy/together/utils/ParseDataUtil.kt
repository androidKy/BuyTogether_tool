package com.buy.together.utils

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

        val comment_content = "comment_content"
        val ip_address = "ip_address"
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
    }

}