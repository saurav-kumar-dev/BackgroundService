package com.example.backgroundservice.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.backgroundservice.notification.NotificationHelper

class ConnectionService : Service() {

    companion object {
        const val ACTION_TRY_AGAIN: String = "try_again"
        private const val NOTIFICATION_ID = 123
    }

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")

        // Perform your service logic here

        // Create and show notification using NotificationHelper



        while (true){
            Log.d("laksbdlakbf", "jbdskfj")
        }
        notificationHelper = NotificationHelper(this)
        startForeground(NOTIFICATION_ID, notificationHelper.createNotification(
            "Foreground Service",
            input ?: "Foreground Service is running"
        ))

        // Return START_STICKY to restart the service if it's killed by the system
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
