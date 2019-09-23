package com.accessibility.service.util

import android.text.TextUtils
import com.accessibility.service.data.TaskBean
import com.safframework.log.L
import com.utils.common.rsa.RSAUtil

/**
 * Description:
 * Created by Quinin on 2019-07-08.
 **/
class TaskDataUtil private constructor() {

    private var mTaskServiceData: TaskBean? = null

    companion object {
        val instance: TaskDataUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TaskDataUtil()
        }
    }

    fun clearData() {
        mTaskServiceData = null
    }

    fun initData(taskServiceData: TaskBean) {
        mTaskServiceData = taskServiceData
    }

    fun getTaskServiceData(): TaskBean? {
        return mTaskServiceData
    }

    fun getPictureList(): List<String>? {
        return mTaskServiceData?.task?.pic_links
    }

    /**
     * 购买行为
     * 0:参团购买
     * 1:拼单购买
     * 2:单独购买
     */
    fun getBuy_type(): Int? {
        return mTaskServiceData?.run {
            task?.buy_behavior
        }
    }

    /**
     * 返回登录渠道
     */
    fun getLogin_channel(): Int? {
        return mTaskServiceData?.run {
            task?.account?.type
        }
    }

    /**
     * 是评论任务还是正常任务
     */
    fun isCommentTask(): Boolean? {
        return mTaskServiceData?.run {
            task?.isCommentTask
        }
    }

    /**
     * 获取评论内容
     */
    fun getCommentContent(): String? {
        return mTaskServiceData?.run {
            task?.comment_content
        }
    }

    /**
     * 获取账号名字
     */
    fun getLogin_name(): String? {
        return mTaskServiceData?.run {
            task?.account?.user
        }
    }

    /**
     * 获取账号密码
     */
    fun getLogin_psw(): String? {
        return mTaskServiceData?.run {
            task?.account?.pwd
        }
    }

    /**
     * 获取账号id
     */
    fun getPdd_account_id(): Int? {
        return mTaskServiceData?.run {
            task?.account?.id
        }
    }

    /**
     * 获取支付宝账号
     */
    fun getAlipayAccount(): String? {
        return mTaskServiceData?.run {
            task?.pay_account?.username
        }
    }

    /**
     * 获取支付宝登录密码
     */
    fun getAliLoginPsw(): String? {
        val rsaPsw = mTaskServiceData?.run {
            task?.pay_account?.login_pwd
        }
        L.d("登录密码解密前：$rsaPsw")
        return RSAUtil.decrypt(Constant.RSA_PRIVATE_KEY, rsaPsw)

        /* val rsaPsw = mTaskServiceData?.run {
             task?.pay_account?.pwd
         }
         if (!TextUtils.isEmpty(rsaPsw)) {
             return String(BASE64Decoder().decodeBuffer(base64Psw))
         }*/

        // return ""
    }

    /**
     * 获取支付宝的支付密码
     */
    fun getAliPay_psw(): String? {
        val rsaPayPsw = mTaskServiceData?.run {
            task?.pay_account?.pay_pwd
        }
        return RSAUtil.decrypt(Constant.RSA_PRIVATE_KEY, rsaPayPsw)
    }


    /**
     * 获取商品名字
     */
    fun getGoods_name(): String? {
        return mTaskServiceData?.run {
            task?.goods?.goods_name
        }
    }

    /**
     * 获取商品链接
     */
    fun getGoodUrl(): String? {
        return mTaskServiceData?.run {
            task?.goods?.goods_url
        }
    }

    /**
     * 获取商品的店铺名称
     */
    fun getMall_name(): String? {
        return mTaskServiceData?.run {
            task?.goods?.mall_name
        }
    }

    /**
     * 获取关键字
     */
    fun getGoodKeyWord(): String? {
        return mTaskServiceData?.task?.goods?.keyword
    }

    /**
     * 获取商品的关键词
     */
    fun getGoods_keyword(): String? {
        try {
            return mTaskServiceData?.task?.goods?.keyword?.apply {
                L.i("关键字：$this")
                when {
                    this.contains("，") -> {
                        val replaceResult = this.replace("，", ",")
                        L.i("过滤，的关键字：$replaceResult")
                        splitKeyWord(replaceResult.split(","))
                    }
                    this.contains(",") -> splitKeyWord(this.split(","))

                    else -> this
                }
            }
        } catch (e: Exception) {
            L.e(e.message, e)
        }
        return ""
    }

    /**
     * 获取关键字列表
     */
    fun getGoodKeyWordList(): List<String>? {
        try {
            mTaskServiceData?.task?.goods?.keyword?.apply {
                L.i("关键字：$this")
                when {
                    this.contains("，") -> {
                        val replaceResult = this.replace("，", ",")
                        L.i("过滤，的关键字：$replaceResult")
                        return replaceResult.split(",")
                    }
                    this.contains(",") -> {
                        return this.split(",")
                    }

                    else -> {
                        return ArrayList<String>().run {
                            add(this@apply)
                            this@run
                        }
                    }
                }
            }
        } catch (e: Exception) {
            L.e(e.message, e)
        }
        return null
    }

    private fun splitKeyWord(keywordList: List<String>): String? {
        if (keywordList.isNotEmpty()) {
            val index = (0 until keywordList.size).random()

            return keywordList[index]
        }
        return ""
    }

    /**
     * 获取搜索的价格
     */
    fun getSearchPrice(): String? {
        return mTaskServiceData?.run {
            task?.goods?.search_price
        }
    }

    /**
     * 获取任务类型
     * {
     *  1：刷浏览数
     *  2：与卖家沟通
     *  3：刷收藏数
     *  4：刷订单数
     *  5：评分
     *  6：评论
     *  12：浏览和沟通
     * }
     */
    fun getTask_type(): Int? {
        var taskType = mTaskServiceData?.run { task?.task_type ?: 1 }
        //过滤5和6的类型
        if (taskType.toString().contains("5") || taskType.toString().contains("6")) {
            taskType = taskType.toString().replace("5", "").replace("6", "").toInt()
        }
        return taskType
    }

    /**
     * 获取聊天信息
     */
    fun getTalk_msg(): String? {
        return mTaskServiceData?.run {
            task?.talk_msg ?: "老板你好"
        }
    }

    /**
     * 获取选择的商品信息
     */
    fun getChoose_info(): List<String>? {
        val serviceChooseInfo = mTaskServiceData?.run {
            task?.goods?.choose_info ?: ""
        }
        if (!TextUtils.isEmpty(serviceChooseInfo)) {

            try {
                return serviceChooseInfo?.split("规格:")?.get(1)?.split(",")
            } catch (e: Exception) {
                L.e(e.message)
            }
        }

        return null
    }

    /**
     * 返回买家的名字
     */
    fun getBuyer_name(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.name
        }
    }

    /**
     * 返回买家的手机号
     */
    fun getBuyer_phone(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.phone
        }
    }

    /**
     * 返回收货地址的省份
     */
    fun getProvince(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.province
        }
    }

    /**
     * 返回城市
     */
    fun getCity(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.city
        }
    }

    /**
     * 返回区域
     */
    fun getDistrict(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.district
        }
    }

    /**
     * 返回街道等详细地址
     */
    fun getStreet(): String? {
        return mTaskServiceData?.run {
            task?.delivery_address?.street
        }
    }

    /**
     * 获取一个任务下的子任务完成的数量
     */
    fun getTaskFinishedCount(): Int? {
        return mTaskServiceData?.task?.task_complete
    }
}