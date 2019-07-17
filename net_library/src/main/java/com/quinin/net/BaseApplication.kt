package com.quinin.net

import android.app.Application
import com.androidnetworking.AndroidNetworking

/**
 * Description:
 * Created by Quinin on 2019-07-17.
 **/
abstract class BaseApplication : Application(){

    override fun onCreate() {
        AndroidNetworking.initialize(getApplicationContext());

        super.onCreate()
    }
}