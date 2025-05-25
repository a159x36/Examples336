package nz.massey.jobsworth

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.Configuration

class MyJobService : JobService() {
    init {
        Configuration.Builder().setJobSchedulerJobIdRange(0, 1000).build()
    }
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i(TAG, "I'm a Job, the time is:" + System.currentTimeMillis() / 1000)
        scheduleJob(this)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
    companion object {
        private const val TAG = "MyJobService"
        fun scheduleJob(context: Context) {
            val serviceComponent = ComponentName(context, MyJobService::class.java)
            val builder = JobInfo.Builder(0, serviceComponent)
            builder.setMinimumLatency((5 * 1000).toLong()) // Wait at least 5s
            builder.setOverrideDeadline((6 * 1000).toLong()) // Maximum delay 6s
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
        }
    }
}
