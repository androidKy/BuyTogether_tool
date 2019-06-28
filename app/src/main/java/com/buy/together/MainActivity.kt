package com.buy.together

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        initFragment()
    }

    private fun initFragment() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val mainFragment = MainFragment()

        beginTransaction.add(R.id.main_container,mainFragment)

        beginTransaction.commitNow()
    }

}
