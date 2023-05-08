package com.weather.weartest

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat


class FindService() : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){
            return START_STICKY
        }
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //알림 채널 설정
        val serviceChannel = NotificationChannel(
            "CHANNEL_ID",
            "알림 설정 모드 타이틀",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)!!
        manager!!.createNotificationChannel(serviceChannel)
//        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //알림 설정
        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("알림 타이틀")
            .setContentText("알림 설명")
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
//
//        Thread(Runnable {
////            findLocation.startLocationUpdates()
//        }).run()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}