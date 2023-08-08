package com.example.martin.bubbles;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private boolean mPaused=false;
    private Thread mThread;
    BubbleView mBubbleView;
    BubbleSurfaceView mSurfaceView;
    boolean mUseSurfaceView;
    private SharedPreferences mPreferences;
    boolean mCollide;

    void init() {

        mThread = new Thread(() -> {
            long currentTime = System.currentTimeMillis() - 10;


            while (!mPaused) {
                long dt = System.currentTimeMillis() - currentTime;
                currentTime = System.currentTimeMillis();
                if (mUseSurfaceView) {
                    mSurfaceView.update(dt / 1000f, mCollide);
                    Canvas c;

                    if (Build.VERSION.SDK_INT > 22) {
                        if (mSurfaceView.getHolder().getSurface().isValid()) {
                            c = mSurfaceView.getHolder().getSurface().lockHardwareCanvas();
                            mSurfaceView.draw(c);
                            mSurfaceView.getHolder().getSurface().unlockCanvasAndPost(c);
                        }

                    } else {
                        c = mSurfaceView.getHolder().lockCanvas();
                        if (c != null) {
                            mSurfaceView.draw(c);
                            mSurfaceView.getHolder().unlockCanvasAndPost(c);
                        }
                    }

                } else {
                    mBubbleView.update(dt / 1000f, mCollide);
                    mBubbleView.postInvalidate();
                }
                long waittime = 16 - (System.currentTimeMillis() - currentTime);
                if (waittime > 0) {
                    try {
                        sleep(waittime);
                    } catch (InterruptedException e) {
                        Log.i(TAG,"Interrupted");
                    }
                }
            }
        });
        mThread.setName("Update Thread");
        if(mUseSurfaceView)
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mThread.start();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    try {
                        mThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mThread = null;
                }
            });
        else
            mThread.start();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUseSurfaceView=mPreferences.getBoolean("surface",false);
        mCollide=mPreferences.getBoolean("collide",false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(mUseSurfaceView) {
            setContentView(R.layout.activity_main_surface);//new GameSurface(this));//
            mSurfaceView=findViewById(R.id.bubbles);
            init();
        }
        else {
            setContentView(R.layout.activity_main);
            mBubbleView = findViewById(R.id.bubbles);
            init();
        }
        findViewById(R.id.menu).setOnClickListener(
                view -> startActivityForResult(
                        new Intent(this, SettingsActivity.class),0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        if(!mUseSurfaceView) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThread = null;
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mUseSurfaceView) {
            if (mPaused) {
                mPaused = false;
                init();
            }
        }
        mPaused = false;
    }
}
