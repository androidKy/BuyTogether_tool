package com.proxy.service.base

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.proxy.service.R
import com.proxy.service.receiver.VpnNotificationReceiver

/**
 * Description:
 * Created by Quinin on 2019-07-18.
 **/
abstract class BaseService : Service() {

    companion object {
        const val CHANNEL_ID = "vpn"
        const val CHANNEL_NAME = "公牛代理"
        const val CHANNEL_DESCRIPTION = "国内代理IP"
        const val VPN_NOTIFICATION_ID = 0x01 //通知的唯一ID
        const val VPN_NOTIFICATION_NAME = "VpnService"
        const val VPN_NOTIFICATION_DESC = "国内代理IP切换"
    }

    override fun onCreate() {
        super.onCreate()

    }

    /**
     * 创建通知
     */
    fun createNotification(id: Int, name: String) {
        try {
            createNotification(this, name, VPN_NOTIFICATION_DESC)?.run {
                startForeground(VPN_NOTIFICATION_ID, this)
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 根据通知ID标识刷新通知内容
     */
    fun refreshNotification(id: Int, desc: String) {
        val notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(NotificationManager::class.java) as NotificationManager
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        notificationManager.notify(id, createNotification(this, VPN_NOTIFICATION_NAME, desc))
    }


    private fun createNotification(context: Context, name: String, desc: String): Notification? {
        var notification: Notification? = null

        val intent = Intent(context, VpnNotificationReceiver::class.java)
        //intent.putExtra() //todo 传数据给广播
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

            val channel = NotificationChannel(
                "$CHANNEL_ID.$name",
                "$CHANNEL_NAME-$name",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "$CHANNEL_DESCRIPTION-$name"

            val builder = NotificationCompat.Builder(context, "$CHANNEL_ID.$name")

            builder.setLargeIcon(iconBitmap)
                .setSmallIcon(R.drawable.icon) //设置通知图标
                .setContentTitle(context.resources.getString(R.string.app_name))//设置通知标题
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setShowWhen(false) //关闭显示时间
                .setAutoCancel(false) //用户触摸时，自动关闭
                .setOngoing(true)//设置处于运行状态
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .priority = NotificationCompat.PRIORITY_DEFAULT

            notificationManager.createNotificationChannel(channel)

            notification = builder.build()
        } else {
            val builder = Notification.Builder(context)

            builder.setContentTitle(context.resources.getString(R.string.app_name))
                .setLargeIcon(iconBitmap)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .setContentTitle(context.resources.getString(R.string.app_name))
                .setShowWhen(false) //关闭显示时间
                .setAutoCancel(false) //用户触摸时，自动关闭
                .setOngoing(true)//设置处于运行状态
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notification = builder.build()
        }

        return notification
    }
}