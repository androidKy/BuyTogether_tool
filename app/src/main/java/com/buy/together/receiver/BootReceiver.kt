package com.buy.together.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.buy.together.MainActivity
import com.safframework.log.L

class BootReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_BOOT: String = Intent.ACTION_BOOT_COMPLETED
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        L.i("开机自启动成功1")
        when (intent?.action) {
            ACTION_BOOT -> {
                L.i("开机自启动成功2")
                val intentMainActivity = Intent(context, MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intentMainActivity)
                L.i("开机自启动成功3")
            }
        }
        Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show()
    }
}































