package com.buy.together

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.accessibility.service.MyAccessibilityService
import com.accessibility.service.base.BaseAccessibilityService
import com.buy.together.fragment.MainFragment
import com.buy.together.service.KeepLiveService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this,KeepLiveService::class.java))
        }else{
            startService(Intent(this,KeepLiveService::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (!BaseAccessibilityService.isAccessibilitySettingsOn(this, MyAccessibilityService::class.java.canonicalName!!)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }
        initFragment()
    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val mainFragment = MainFragment()

        beginTransaction.add(R.id.main_container,mainFragment)

        beginTransaction.commitNow()
    }

}
