package nz.massey.jobsworth

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "MyWorker"

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){
    override fun doWork(): Result {
        Log.i(TAG, "I'm a Worker, the time is:" + System.currentTimeMillis() / 1000)

        // Re-enqueue the worker after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        }, 5000)

        return Result.success()
    }
}
