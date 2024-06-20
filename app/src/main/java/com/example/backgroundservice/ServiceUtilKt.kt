package com.example.backgroundservice

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.example.backgroundservice.service.ConnectionService

private const val TAG = "ServiceUtil"
fun isServiceRunning(serviceClass: Class<*>): Boolean {
    val activityManager =
        BackgroundApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services =
        activityManager.getRunningServices(Int.MAX_VALUE)
    for (runningServiceInfo in services) {
        if (runningServiceInfo.service.className == serviceClass.name) {
            Log.d(TAG, "isServiceRunning ${true}")
            return true
        }
    }
    Log.d(TAG, "isServiceRunning ${false}")
    return false
}


fun isApplicationInBackground(context: Context): Boolean {
    try {
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses =
            am.runningAppProcesses ?: return false
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        Log.d(TAG, "isApplicationInBackground false")
                        return false
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, e.message?:"")
    }
    return true
}

fun tryToStartXmppService(context: Context) {
    try {
        val intent = Intent(context, ConnectionService::class.java)
        intent.action = ConnectionService.ACTION_TRY_AGAIN
        val pendingIntent: PendingIntent = PendingIntent.getService(
            context, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager: AlarmManager? =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val timeToWake: Long = SystemClock.elapsedRealtime() + 1000
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToWake, pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToWake, pendingIntent
                )
            }
        }
    } catch (e: SecurityException) {
        Log.e(TAG, "SecurityException " + e.localizedMessage)
    } catch (e: RuntimeException) {
        Log.d(TAG, "RuntimeException " + e.localizedMessage)
    }
}