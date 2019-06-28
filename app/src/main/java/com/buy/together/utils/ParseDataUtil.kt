package com.buy.together.utils

import com.buy.together.bean.TaskBean

/**
 * Description:
 * Created by Quinin on 2019-06-28.
 **/
class ParseDataUtil {
    companion object {
        val task_id = "task_id"
        val ip_address = "ip_address"
        val accountName = "accountName"
        val accountPsw = "accountPsw"
        val task_type = "task_type"
        val goods_size = "goods_size"
        val goods_id = "goods_id"
        val goods_type = "goods_type"
        val goods_name = "goods_name"
        val goods_keyword = "goods_keyword"
        val delivery_address = "delivery_address"
        val talk_msg = "talk_msg"
        val tasked_count = "tasked_count"
        val comment_need = "comment_need"
        val comment_content = "comment_content"
        val score_need = "score_need"
        val score_value = "score_value"


        fun parseTaskBean2HashMap(taskBean: TaskBean): HashMap<String, String> {

            val task = taskBean.task
            val hashMap = HashMap<String, String>()
            hashMap[task_id] = task.task_id.toString()
            hashMap[ip_address] = task.ip.content
            hashMap[accountName] = task.account.qq.name
            hashMap[accountPsw] = task.account.buy_together.psw
            hashMap[task_type] = task.task_type.toString()

            val goods = task.goods
            hashMap[goods_size] = goods.size.toString()
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
            }

            return hashMap
        }


        fun parseHashMap2ArrayList(hashMap: HashMap<String, String>) :ArrayList<ArrayList<String>>  {
            val rawList = ArrayList<ArrayList<String>>()

            val keys = hashMap.keys
            for (key in keys)
            {
                val raw = ArrayList<String>()
                raw.add(key)
                raw.add(hashMap[key]!!)
                rawList.add(raw)

            }

            return rawList
        }
    }

}