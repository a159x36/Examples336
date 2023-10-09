package nz.massey.jobsworth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import nz.massey.jobsworth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mMp;
    public ActivityMainBinding mMainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mMainLayout.getRoot());
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }
        mMainLayout.intentButton.setOnClickListener(view ->
            startService(new Intent(this,MyIntentService.class))
        );
        mMainLayout.jobIntentButton.setOnClickListener(view ->
                JobIntentService.enqueueWork(this, MyJobIntentService.class, 159336, new Intent())
        );
        mMainLayout.jobButton.setOnClickListener(view -> MyJobService.scheduleJob(this));
        mMp=MediaPlayer.create(this,R.raw.example);
        mMp.setLooping(true);
        mMainLayout.soundButton.setOnClickListener(view -> {
            if(mMp.isPlaying()) mMp.pause(); else mMp.start();
        } );
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMp.pause();
    }
}