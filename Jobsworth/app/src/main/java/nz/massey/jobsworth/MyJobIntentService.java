package nz.massey.jobsworth;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class MyJobIntentService extends JobIntentService {
    private static final String TAG = "JOB";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        for (int i = 0; i < 10; i++) {
            Log.i(TAG, "I'm a Job:"+ i);
            if (isStopped()) {
                Log.i(TAG, "Job Stopped"+i);
                return;
            }
            SystemClock.sleep(1000);
        }
    }
}
