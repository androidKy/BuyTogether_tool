package com.buy.together.fragment.viewmodel

import android.content.Context
import com.buy.together.base.BaseView
import com.buy.together.base.BaseViewModel
import com.buy.together.bean.*
import com.buy.together.fragment.view.MainView
import com.buy.together.utils.Constant
import com.buy.together.utils.ParseDataUtil
import com.buy.together.utils.TestData
import com.google.gson.Gson
import com.safframework.log.L
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.goldze.mvvmhabit.utils.SPUtils

/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainViewModel(val context: Context, val mainView: MainView) : BaseViewModel<Context, BaseView>() {

    private val mSubscribeList = ArrayList<Disposable>()

    /**
     * 获取任务
     */
    fun getTask() {
        L.init(MainViewModel::class.java.simpleName)

        /* val okHttpClient = OkHttpClient()

         val request = Request.Builder()
             .url(Constant.URL_GET_TASK)
             .get()
             .build()

         okHttpClient.newCall(request).enqueue(object : Callback {
             override fun onFailure(call: Call, e: IOException) {
                 L.i("exception msg : ${e.message}")
                 mainView.onFailed(e.message!!)
             }

             override fun onResponse(call: Call, response: Response) {
                 L.i("get task result : ${response.body()?.string()}")
             }
         })*/


        val disposable = Observable.just(TestData.taskBean_str)
            .flatMap {
               val taskBean = try {
                    Gson().fromJson(it, TaskBean::class.java)
                } catch (e: Exception) {
                    L.e(e.message)
                    val exceptionTask = TaskBean()
                    exceptionTask.msg = e.message
                    exceptionTask.code = 400
                    exceptionTask
                }
                saveTaskData2SP(it)
                Observable.just(taskBean)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mainView.onResponTask(it)
            }
        mSubscribeList.add(disposable)
    }

    /**
     * 解析数据为键值对，显示到表格上
     */
    fun parseTask(taskBean: TaskBean) {
        val subscribe = Observable.just(taskBean)
            .flatMap {

                val datas = try {
                    val hashMapData = ParseDataUtil.parseTaskBean2HashMap(it)

                    ParseDataUtil.parseHashMap2ArrayList(hashMapData).reversed()
                } catch (e: Exception) {
                    L.e(e.message)
                    ArrayList<ArrayList<String>>()
                }

                val arrayList = ArrayList<ArrayList<String>>()
                arrayList.addAll(datas)
                Observable.just(arrayList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mainView.onParseDatas(it)
            }

        mSubscribeList.add(subscribe)
    }

    /**
     * 释放订阅
     */
    override fun clearSubscribes() {
        if (mSubscribeList.size > 0) {
            for (sub in mSubscribeList) {
                sub.dispose()
            }
        }
    }

    /**
     * 保存任务数据到SP
     */
    private fun saveTaskData2SP(strData:String) {
        val spUtils = SPUtils.getInstance(Constant.SP_TASK_FILE_NAME)

        spUtils.put(Constant.KEY_TASK_DATA,strData)
    }
}