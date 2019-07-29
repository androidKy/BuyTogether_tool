package com.utils.common.pdd_api

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseListener
import com.safframework.log.L
import com.utils.common.ThreadUtils
import okhttp3.Response

/**
 * Description:接口管理类
 * Created by Quinin on 2019-07-29.
 **/
class ApiManager {
    private var mDataListener: DataListener? = null

    companion object {
        private const val URL_DOMAIN: String = "192.168.1.157:8000"
        private const val URL_HTTP: String = "http://"
        const val POST_JSON_CONTENT_TYPE: String = "application/json"

        const val URL_GET_TASK: String = "$URL_HTTP$URL_DOMAIN/task/get/"
        const val URL_GET_COMENT_TASK: String = "$URL_HTTP$URL_DOMAIN/task/comment/"
        const val URL_UPLOAD_IP: String = "$URL_HTTP$URL_DOMAIN/task/inform/"
        const val URL_UPDATE_TASK_STATUS: String = "$URL_HTTP$URL_DOMAIN/task/inform/"
        const val URL_GET_ACCOUNT: String = "$URL_HTTP$URL_DOMAIN/others/account/?id="
        const val URL_UPDATE_ACCOUNT: String = "$URL_HTTP$URL_DOMAIN/others/account/"


        val instance: ApiManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ApiManager()
        }
    }

    init {
        L.init(ApiManager::class.java)
    }

    fun setDataListener(dataListener: DataListener): ApiManager {
        mDataListener = dataListener

        return this
    }

    /**
     * 获取普通任务
     */
    fun getNormalTask(imei: String) {
        AndroidNetworking.get(URL_GET_TASK)
            .build()
            .getAsOkHttpResponse(object : OkHttpResponseListener {
                override fun onResponse(response: Response?) {
                    responseSucceed(response, "获取任务成功")
                }

                override fun onError(anError: ANError?) {
                    responseError(anError, "获取任务失败")
                }
            })
    }

    /**
     * 获取QQ账号
     * @param taskId 任务ID
     */
    fun getQQAccount(taskId: String) {
        AndroidNetworking.get("$URL_GET_ACCOUNT$taskId")
            .build()
            .getAsOkHttpResponse(object : OkHttpResponseListener {
                override fun onResponse(response: Response?) {
                    responseSucceed(response, "获取QQ账号成功")
                }

                override fun onError(anError: ANError?) {
                    responseError(anError, "获取账号失败")
                }
            })
    }

    /**
     * 上报账号使用情况
     */
    fun updateQQAcount(accountId: Int, isValid: Boolean) {
        AndroidNetworking.post(URL_UPDATE_ACCOUNT)
            .setContentType(POST_JSON_CONTENT_TYPE)
            .addBodyParameter("id", accountId.toString())
            .addBodyParameter("effective", isValid.toString())
            .build()
            .getAsOkHttpResponse(object : OkHttpResponseListener {
                override fun onResponse(response: Response?) {
                    responseSucceed(response, "上报账号使用成功")
                }

                override fun onError(anError: ANError?) {
                    responseError(anError, "更新账号使用情况失败")
                }
            })
    }

    /**
     * 更新任务状态
     */
    fun updateTaskStatus(taskId: String, isSucceed: Boolean, accountName: String, orderId: String, failedMark: String) {
        AndroidNetworking.post(URL_UPDATE_TASK_STATUS)
            .setContentType(POST_JSON_CONTENT_TYPE)
            .addBodyParameter("option", "complete_task")
            .addBodyParameter("task_id", taskId)
            .addBodyParameter("success", isSucceed.toString())
            .addBodyParameter("nickname", accountName)
            .addBodyParameter("order_id", orderId)
            .addBodyParameter("remark", failedMark)
            .build()
            .getAsOkHttpResponse(object : OkHttpResponseListener {
                override fun onResponse(response: Response?) {
                    responseSucceed(response, "更新任务状态成功")
                }

                override fun onError(anError: ANError?) {
                    responseError(anError, "更新任务状态失败")
                }

            })
    }


    private fun responseSucceed(response: Response?, log: String) {
        L.i("mainThread: ")
        ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
            override fun doInBackground(): String? {
                return response?.body()?.string()
            }

            override fun onSuccess(result: String?) {
                result?.let {
                    L.i("$log：$it")
                    mDataListener?.onSucceed(it)
                }
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }

        })

    }

    private fun responseError(anError: ANError?, log: String) {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
            override fun doInBackground(): String? {
                return anError?.errorDetail
            }

            override fun onSuccess(result: String?) {
                result?.let {
                    L.e("$log：$it")
                    mDataListener?.onFailed(it)
                }
            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }

        })
    }
}