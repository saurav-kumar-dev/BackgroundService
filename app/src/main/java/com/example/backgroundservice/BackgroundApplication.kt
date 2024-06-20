package com.example.backgroundservice

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.backgroundservice.service.ConnectionService
import com.example.backgroundservice.worker.ConnectionWorker

class BackgroundApplication : Application() {

    companion object {
        private var instance: BackgroundApplication? = null

        @Synchronized
        fun getInstance(): BackgroundApplication {
            return instance ?: BackgroundApplication().also { instance = it }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //if (isServiceRunning(ConnectionService::class.java).not()){
            enqueueOnetimeConnectionWorker()
        //}
    }

    fun enqueueOnetimeConnectionWorker() {
        val workManager = WorkManager.getInstance(this)
        //prune finished work
        workManager.pruneWork()
        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.NOT_REQUIRED).build()
        val connectionWorker =
            OneTimeWorkRequest.Builder(ConnectionWorker::class.java).addTag(ConnectionWorker.TAG)
                //.setConstraints(constraints)
         .build()
        Log.d("TAG", "enqueue OneTime ConnectionWorker " + connectionWorker.id)
        workManager.enqueueUniqueWork(
            ConnectionWorker.NAME_ONETIME, ExistingWorkPolicy.KEEP, connectionWorker
        )
    }

}
