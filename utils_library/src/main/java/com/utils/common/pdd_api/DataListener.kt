package com.utils.common.pdd_api

/**
 * Description:
 * Created by Quinin on 2019-07-29.
 **/
interface DataListener {
    fun onSucceed(result: String)

    fun onFailed(errorMsg: String)
}