package com.login.test

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.View
import com.github.otherlogin.qq.TencentUtil
import com.login.test.accessibility.BaseAccessibilityService
import com.login.test.accessibility.QQLoginAccessService
import com.login.test.util.FileIOUtils
import com.login.test.util.ThreadUtils
import com.safframework.log.L
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError

class MainActivity : AppCompatActivity() {
    private val tag: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        L.init(tag)

        openAccessibilityService()
        // openQQlogin()
    }

    fun loginQQ(view: View) {
        openQQlogin()
    }

    private fun openQQlogin() {

        TencentUtil.openQQ(this, "MyAppId", object : IUiListener {
            override fun onComplete(p0: Any?) {
                L.i("Tencent login complete")
            }

            override fun onCancel() {
                L.i("Tencent login cancel")
            }

            override fun onError(p0: UiError?) {
                L.i("Tencent login error")
            }

        })
    }

    private fun openAccessibilityService() {
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(
                this,
                QQLoginAccessService::class.java.canonicalName!!
            )
        ) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }
        ThreadUtils.executeByCached(object : ThreadUtils.Task<ArrayList<String>>() {
            override fun doInBackground(): ArrayList<String> {
                val arrayList = ArrayList<String>()
                val file = assets.open("qq_test.txt")
                val dataList = FileIOUtils.readInputStream2List(file)

                return dataList
            }

            override fun onSuccess(result: ArrayList<String>?) {
                L.i("result size: ${result?.size} threadName: ${Thread.currentThread().name}")
                BaseAccessibilityService.setDataList(result!!)

            }

            override fun onCancel() {
            }

            override fun onFail(t: Throwable?) {
            }
        })

        printDeviceParams()
    }


    private fun printDeviceParams() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        L.i("androidId: $androidId")

        val mTelephonyMgr = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            L.i("imei: ${mTelephonyMgr.deviceId}")
        }

    }


}
