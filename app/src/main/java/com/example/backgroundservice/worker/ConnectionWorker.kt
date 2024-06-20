package com.example.backgroundservice.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.backgroundservice.BackgroundApplication
import com.example.backgroundservice.isServiceRunning
import com.example.backgroundservice.service.ConnectionService

class ConnectionWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        dumpWorkStatus()
        Log.d(TAG, "doWork " + id + " on " + Thread.currentThread())
        return if (ensureConnection()) Result.success() else Result.retry()
    }

    private fun ensureConnection(): Boolean {
        if (isServiceRunning(ConnectionService::class.java).not()) {
            tryToStartXmppService()
        }
        return isServiceRunning(ConnectionService::class.java)
    }

    private fun dumpWorkStatus() {
        try {
            val statusList = WorkManager.getInstance(BackgroundApplication.getInstance())
                .getWorkInfosByTagLiveData(TAG)
            statusList.observeForever(object : Observer<List<WorkInfo?>?> {
                override fun onChanged(value: List<WorkInfo?>?) {
                    statusList.removeObserver(this)
                    if (null != value) {
                        Log.d(TAG, "dumpWorkStatus " + value.size + " items:")
                        for (status in value) {
                            Log.d(
                                TAG,
                                "  job: " + status?.id + " " + status?.tags + " " + status?.state
                            )
                        }
                    }
                }
            })
        } catch (e: Exception) {
            // Do Nothing
        }
    }

    private fun tryToStartXmppService() {
        val intent = Intent(
            BackgroundApplication.getInstance().applicationContext,
            ConnectionService::class.java
        )
        try {
            BackgroundApplication.getInstance().startForegroundService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Exception " + e.localizedMessage)
        }
    }

    companion object {
        val TAG: String = ConnectionWorker::class.java.simpleName
        const val NAME_PERIODIC: String = "ConnectionWorker-Periodic"
        const val NAME_ONETIME: String = "ConnectionWorker-OneTime"
    }
}