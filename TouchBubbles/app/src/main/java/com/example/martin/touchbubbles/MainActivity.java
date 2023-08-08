package com.example.martin.touchbubbles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Bubbles";
    private boolean mPaused=false;
    private Thread mThread;
    private BubbleView mBubbleView;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private SharedPreferences mPreferences;
    private int mUpdateTime=10;


    private void init() { // update thread
        mThread=new Thread(() -> {
                long currentTime=System.currentTimeMillis()-10;
                long secs=System.currentTimeMillis()/1000;
                long rendertime=0;
                while (!mPaused) {
                    long newtime=System.currentTimeMillis();
                    long dt=newtime-currentTime;
                    currentTime=newtime;
                    mBubbleView.update(dt/1000f);
                    newtime=System.currentTimeMillis();
                    if(newtime/1000>secs) {  // show stats every second for last update
                        Log.d(TAG, "Update took:" + (newtime - currentTime)+" dt="+dt);
                        secs=newtime/1000;
                    }
                    if(newtime-rendertime>10) {
                        mBubbleView.postInvalidate();
                        rendertime=newtime;
                    }
                    long waittime=mUpdateTime-(newtime-currentTime);
                    if(waittime>0) {
                        try {
                            sleep(waittime);
                        } catch (InterruptedException ignored) {

                        }
                    }
                }

        },"UpdateThread");

        mThread.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mBubbleView=findViewById(R.id.bubbles);
        mBubbleView.setnBubbles(
                Integer.parseInt(mPreferences.getString(getString(R.string.nsmall_key),
                        getString(R.string.default_nbubbles))),
                Integer.parseInt(mPreferences.getString(getString(R.string.nlarge_key),
                        getString(R.string.default_nlarge))),
                Integer.parseInt(mPreferences.getString(getString(R.string.smallmin_key),
                        getString(R.string.default_smallmin))),
                Integer.parseInt(mPreferences.getString(getString(R.string.smallmax_key),
                        getString(R.string.default_smallmax))),
                Integer.parseInt(mPreferences.getString(getString(R.string.largemin_key),
                        getString(R.string.default_largemin))),
                Integer.parseInt(mPreferences.getString(getString(R.string.largemax_key),
                        getString(R.string.default_largemax)))
                );
        mBubbleView.setNative(mPreferences.getBoolean(getString(R.string.native_key),
                Boolean.parseBoolean(getString(R.string.default_native))));
        mBubbleView.setCompress(mPreferences.getBoolean(getString(R.string.compress_key),
                Boolean.parseBoolean(getString(R.string.default_compress))));
        mBubbleView.setRigidity(Float.parseFloat(mPreferences.getString(getString(R.string.rigidity_key),
                getString(R.string.default_rigidity))));
        mBubbleView.setDampening(Float.parseFloat(mPreferences.getString(getString(R.string.dampening_key),
                getString(R.string.default_dampening))));

        mBubbleView.setFocusable(true);
        mUpdateTime=Integer.parseInt(mPreferences.getString(getString(R.string.updatetime_key),
                getString(R.string.default_updatetime)));
        init();
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        if(mSensorManager!=null)
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        findViewById(R.id.menu).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            finish();
         });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSensorManager!=null)
            mSensorManager.unregisterListener(this);
        mPaused=true;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread=null;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mSensorManager!=null)
            mSensorManager.registerListener(this,
                    mAccel, SensorManager.SENSOR_DELAY_GAME);
        if(mPaused) {
            mPaused=false;
            init();
        }
    }

    // Accelerometer callbacks
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float mul=Float.parseFloat(mPreferences.getString(getString(R.string.gravity_key),
                getString(R.string.default_gravity)))/10.0f;
     //   Log.i(TAG,"G="+sensorEvent.values[0]+","+sensorEvent.values[1]);
        mBubbleView.setGravity(-sensorEvent.values[0]*mul,sensorEvent.values[1]*mul);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
