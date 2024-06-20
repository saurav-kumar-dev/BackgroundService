package com.example.backgroundservice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.example.backgroundservice.BackgroundApplication

class ConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.e("TAG", "Received action: $action")
        if (action != null) {
            when (action) {
                "android.intent.action.BOOT_COMPLETED",
                "android.intent.action.LOCKED_BOOT_COMPLETED",
                "android.intent.action.REBOOT",
                "android.intent.action.ACTION_BOOT_COMPLETED",
                "android.intent.action.QUICKBOOT_POWERON",
                "com.htc.intent.action.QUICKBOOT_POWERON" -> tryToStartXmppService()

                PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                    tryToStartXmppService()
                }

                PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED -> {
                    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (pm.isDeviceIdleMode) {
                        // the device just entered doze mode
                    } else {
                        // the device just removed from doze mode
                        tryToStartXmppService()
                    }
                }
            }
        }
    }

    private fun tryToStartXmppService() {
        BackgroundApplication.getInstance().enqueueOnetimeConnectionWorker()
    }
}