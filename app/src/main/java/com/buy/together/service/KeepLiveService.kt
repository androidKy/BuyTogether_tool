package com.buy.together.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.buy.together.R

class KeepLiveService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notification:Notification? = null

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notification = Notification.Builder(this,NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("任务状态：")
                .setContentText("正在进行")
                .build()
            startForeground(NotificationManager.IMPORTANCE_MAX, notification)
        }else{
            notification = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("任务状态：")
                .setContentText("正在进行")
                .build()
           // notification  = Notification(R.mipmap.ic_launcher_round,"任务开始",System.currentTimeMillis())
            notificationManager.notify(1,notification)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(true)
    }
}
