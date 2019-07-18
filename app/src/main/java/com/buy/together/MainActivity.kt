package com.buy.together

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.buy.together.fragment.MainFragment
import com.buy.together.service.KeepLiveService
import com.buy.together.utils.Constant
import com.safframework.log.L
import me.goldze.mvvmhabit.utils.SPUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, KeepLiveService::class.java))
        } else {
            startService(Intent(this, KeepLiveService::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        saveScreenDensity(this)
    }

    override fun onResume() {
        super.onResume()
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(
                this,
                MyAccessibilityService::class.java.canonicalName!!
            )
        ) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }
        initFragment()
    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val mainFragment = MainFragment()

        beginTransaction.add(R.id.main_container, mainFragment)

        beginTransaction.commitNow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    /**
     * 保存屏幕数据，用于Adb命令根据像素点击
     */
    fun saveScreenDensity(activity: Activity) {
        L.i(
            "width = ${com.utils.common.DisplayUtils.getRealWidth(activity)} height = ${com.utils.common.DisplayUtils.getRealHeight(
                activity
            )}"
        )
        val width = com.utils.common.DisplayUtils.getRealWidth(activity)
        val height = com.utils.common.DisplayUtils.getRealHeight(activity)

        SPUtils.getInstance().apply {
            put(Constant.KEY_SCREEN_DENSITY, "$width,$height")
        }

    }
}
