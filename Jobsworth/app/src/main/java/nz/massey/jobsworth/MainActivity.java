package nz.massey.jobsworth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,MyIntentService.class));
//         JobIntentService.enqueueWork(this, MyJobIntentService.class, 159336, new Intent());
//        MyJobService.scheduleJob(this);
    }
}