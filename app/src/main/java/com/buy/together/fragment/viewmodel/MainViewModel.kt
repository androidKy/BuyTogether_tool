package com.buy.together.fragment.viewmodel

import android.content.Context
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.fragment.view.MainView
import com.buy.together.utils.Constant
import com.safframework.log.L
import okhttp3.*
import java.io.IOException

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainViewModel(val context: Context,val mainView: MainView) : BaseViewModel<Context,BaseView>() {

    fun getTask() {
        L.init(MainViewModel::class.java.simpleName)

        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(Constant.URL_GET_TASK)
            .get()
            .build()

        okHttpClient.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                L.i("exception msg : ${e.message}")
                mainView.onFailed(e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                L.i("get task result : ${response.body()?.string()}")
            }
        })
    }
}