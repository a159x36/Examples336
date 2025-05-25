@file:Suppress("DEPRECATION")

package nz.massey.jobsworth

import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.JobIntentService

private const val TAG = "JOB"

class MyJobIntentService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        for (i in 0..9) {
            Log.i(TAG, "I'm a Job:$i")
            if (isStopped) {
                Log.i(TAG, "Job Stopped$i")
                return
            }
            SystemClock.sleep(1000)
        }
    }
}
