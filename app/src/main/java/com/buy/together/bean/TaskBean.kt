package com.buy.together.bean


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
data class TaskBean(
    val code: Int,
    val msg: String,
    val task: Task
)

data class Task(
    val `data`: Data,
    val account: Account,
    val ip: Ip,
    val task_id: Int
)

data class Account(
    val buy_together: BuyTogether,
    val we_chat: WeChat
)

data class BuyTogether(
    val buttogether_id: Int,
    val name: String,
    val psw: String
)

data class WeChat(
    val name: String,
    val psw: String,
    val wechat_id: Int
)

data class Data(
    val goods: List<Good>,
    val task_type: Int
)

data class Good(
    val comment_content: String,
    val comment_need: Boolean,
    val delivery_address: String,
    val goods_id: Int,
    val goods_type: Int,
    val keyword: String,
    val name: String,
    val score_need: Boolean,
    val score_value: Int,
    val stay_time: StayTime,
    val talk_msg: String,
    val tasked_count: Int
)

data class StayTime(
    val goods_time: Int,
    val search_time: Int
)

data class Ip(
    val city: String,
    val content: String,
    val ip_id: Int,
    val mac_address: String
)