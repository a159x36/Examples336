package com.example.martin.touchbubbles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * Created by martin on 19/09/17.
 */

public class BubbleView extends View {

    private static final String TAG = "BubbleView";
    private boolean mCompress=false;  // do the bubbles compresss
    private float mRigidity=0.3f;     // how bouncy are the bubbles
    private float mDampening=0.9f;    // how much do they slow down after a bounce
    private boolean mNative = true;   // use navite code?
    private float mTouchX;            // where was the last touch
    private float mTouchY;
    private float mVx;                // what is the velocity of the curent screen drag
    private float mVy;
    private long mTouchTime;          // when was the screen last touched (for working out velocity)

    private float mGravityX=0,mGravityY=9.8f;    // gravity
    private int mTouchedBubble=-1;               // which bubble is held, -1 means none

    private Bitmap mBubbleBitmap;                // used to draw a bubble
    private static final Paint mRedPaint;        // gives a bubble a red tint
    private static final Paint mGreenPaint;      // gives a bubble a green tint
    private final Rect mDestRect =new Rect();    // rectangle to draw bubble in
  //  private static final Paint mWhitePaint;      // not currently used
  //  private static final Path mPath;             // not currently used

    static {
        System.loadLibrary("bubblemover");  // load native library
        mRedPaint =new Paint(0);
        mRedPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{  // red *1.3
                1.3f, 0f, 0f, 0f, 0,
                0, 1f, 0f, 0f, 0,
                0, 0, 1f, 0f, 0,
                0f, 0f, 0f, 1f, 0f}));

        mGreenPaint =new Paint(0);
        mGreenPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{ // green *1.3
                1f, 0f, 0f, 0f, 0,
                0, 1.3f, 0f, 0f, 0,
                0, 0, 1f, 0f, 0,
                0f, 0f, 0f, 1f, 0f}));
/*
        mWhitePaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhitePaint.setColor(Color.argb(255, 255, 255, 255));
        mWhitePaint.setStyle(Paint.Style.STROKE);
        mWhitePaint.setStrokeWidth(8);
        mWhitePaint.setAntiAlias(false);
        mPath=new Path();
        mPath.addCircle(0,0,200, Path.Direction.CW);
        RectF r=new RectF(-133, -133, 133, 133);
        mPath.addArc(r,300,30);

 */
    }

    // some setters
    public void setCompress(boolean mCompress) {
        this.mCompress = mCompress;
    }
    public void setRigidity(float mRigidity) {
        this.mRigidity = mRigidity;
    }
    public void setDampening(float mDampening) {
        this.mDampening = mDampening;
    }
    public void setNative(boolean mNative) {
        this.mNative = mNative;
    }

    // set bubble parameters and initialise
    public void setnBubbles(int nsmall, int nlarge, int smallmin, int smallmax, int largemin, int largemax) {
        nBubbles = nsmall+nlarge;
        mSmall=nsmall;
        mSmallMin=smallmin;
        mLargeMin=largemin;
        mSmallMax=smallmax;
        mLargeMax=largemax;
     //   init();
    }

    // bubble parameters
    private int nBubbles, mSmall, mSmallMin, mLargeMin, mSmallMax, mLargeMax;
    private final Random mRand=new Random();
    // use a single array of floats for the bubble data to make it easier to pass to native code
    private float[] mBubbleData;
    private int mWidth, mHeight;

    // offsets for the bubble data
    private final int X=0;
    private final int Y=1;
    private final int VX=2;
    private final int VY=3;
    private final int RR=4;
    private final int CR=5;

    private final Object mBubbleLock=new Object();

    private void init() {
        mBubbleData= new float[nBubbles*6];//ByteBuffer.allocateDirect((nBubbles)*6*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        synchronized (mBubbleLock) {
            for (int i = 0; i < nBubbles*6; i+=6) {
                float r;

                mBubbleData[i+VX]=-5+ mRand.nextInt(100)/10f;
                mBubbleData[i+VY]=-5 + mRand.nextInt(100) / 10f;
                if(i<mSmall*6)
                    r=mRand.nextInt(Math.max((mSmallMax* mWidth)/100,1))+(mSmallMin* mWidth)/200;
                else
                    r=mRand.nextInt(Math.max((mLargeMax* mWidth)/20,1))+(mLargeMin* mWidth)/40;
                mBubbleData[i+ RR]=r;
                mBubbleData[i+CR]=r;
                mBubbleData[i+X]=mRand.nextInt(mWidth-2*(int)r) + (int)r;
                mBubbleData[i+Y]=mRand.nextInt(mHeight-2*(int)r) + (int)r;
            }
        }
    }
    public BubbleView(Context context, AttributeSet attrs) {
        super(context,attrs);

        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inScaled=false;
        mBubbleBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble,opts);
  //      mBubbleBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.redball,opts);
//        mBubbleBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.greyball,opts);


    }

    // only initialise when we know what size the view is
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            mWidth = right - left;
            mHeight = bottom - top;
            mRedPaint.setStrokeWidth(mWidth /200+1);
            mGreenPaint.setStrokeWidth(mWidth /200+1);
            init();
        }

    }

    // private RectF mRect=new RectF();


    // draw a bubble
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        Paint p;
        for(int i=0;i<nBubbles*6;i+=6) {

            if(i==mTouchedBubble)
                p=mGreenPaint;
            else
                p=mRedPaint;

    //        c.save();
     //       c.translate(mBubbleData[i+X],mBubbleData[i+Y]);
    //        float sc=mBubbleData[i+CR]/260f;
          //  p.setStrokeWidth(8);
    //        c.scale(sc,sc);
    //        c.drawPath(mPath,p);
            int r=(int)mBubbleData[i+CR];
            int x=(int)mBubbleData[i+X];
            int y=(int)mBubbleData[i+Y];
            mDestRect.set(x-r,y-r,x+r,y+r);
            c.drawBitmap(mBubbleBitmap,null, mDestRect,p);
            /*
            c.drawCircle(0,0,mBubbleData[i+CR],p);
            float sr=2f* mBubbleData[i+CR] /3f;
            mRect.set(-sr, -sr, sr, sr);
            c.drawArc(mRect, 300, 30, false, p);
            */
       //     c.restore();
        }
    }

    public void update(float dt) {
        synchronized (mBubbleLock) {
            if(mBubbleData==null)
                return;
            if(mNative)
                nativeUpdate(nBubbles, mTouchedBubble, getWidth(),getHeight(),dt,mGravityX,mGravityY,mBubbleData, mRigidity, mDampening, mCompress);
            else {
                for (int i=0;i<nBubbles*6;i+=6) {
                    mBubbleData[i + X] += mBubbleData[i + VX] * dt * 100;
                    mBubbleData[i + Y] += mBubbleData[i + VY] * dt * 100;
                    if (mBubbleData[i + Y] < mBubbleData[i + RR] || mBubbleData[i + Y] > getHeight() - mBubbleData[i + RR]) {
                        mBubbleData[i + Y] -= mBubbleData[i + VY] * dt * 100;
                        mBubbleData[i + VY] = -mBubbleData[i + VY]*mDampening;
                    }
                    if (mBubbleData[i + X] < mBubbleData[i + RR] || mBubbleData[i + X] > getWidth() - mBubbleData[i + RR]) {
                        mBubbleData[i + X] -= mBubbleData[i + VX] * dt * 100;
                        mBubbleData[i + VX] = -mBubbleData[i + VX]*mDampening;
                    }
                    if (i != mTouchedBubble) {
                        mBubbleData[i + VX] += dt * mGravityX * 10;
                        mBubbleData[i + VY] += dt * mGravityY * 10;
                    }
                }

                for (int i=0;i<nBubbles*6;i+=6) {
                    float x=mBubbleData[i+X];
                    float y=mBubbleData[i+Y];
                    float r=mBubbleData[i+ RR];
                    mBubbleData[i+CR]=r;
                    for(int j=i+6;j<nBubbles*6;j+=6) {
                        float x1=mBubbleData[j+X];
                        float y1=mBubbleData[j+Y];
                        float r1=mBubbleData[j+ RR];
                        float d=(x1-x)*(x1-x)+(y1-y)*(y1-y);
                        if(d<(r1+r)*(r1+r)) {
                            d = (float)Math.sqrt(d);
                            if(mCompress) {
                                float sz = d - mBubbleData[j + CR];
                                if (sz < 8)
                                    sz = 8;
                                if (sz < mBubbleData[i + CR])
                                    mBubbleData[i + CR] = sz;
                            }
                            float dx = x1 - x;
                            float dy = y1 - y;
                            if(d!=0) {
                                dx = dx / d;
                                dy = dy / d;
                            }
                            float displacement = (r + r1) - d;
                            if (i != mTouchedBubble) {
                                mBubbleData[i+VX] = (mBubbleData[i+VX] - mRigidity * dx * displacement) * mDampening;
                                mBubbleData[i+VY] = (mBubbleData[i+VY] - mRigidity * dy * displacement) * mDampening;
                            }
                            if (j != mTouchedBubble) {
                                mBubbleData[j+VX] = (mBubbleData[j+VX] + mRigidity * dx * displacement) * mDampening;
                                mBubbleData[j+VY] = (mBubbleData[j+VY] + mRigidity * dy * displacement) * mDampening;
                            }
                        }
                    }

                }
            }
            if(mTouchedBubble!=-1) {
                mBubbleData[mTouchedBubble + X] = mTouchX;
                mBubbleData[mTouchedBubble + Y] = mTouchY;
            }
        }


    }

    public void setGravity(float gx,float gy) {
        mGravityX=gx;
        mGravityY=gy;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchX = event.getX();
            mTouchY = event.getY();

            for (int i = 0; i < nBubbles * 6; i += 6) {
                float x = mBubbleData[i + X];
                float y = mBubbleData[i + Y];
                float r = mBubbleData[i + RR];
                if (((x - mTouchX) * (x - mTouchX) + (y - mTouchY) * (y - mTouchY)) < r * r) {
                    mTouchedBubble = i;
                    mBubbleData[i + VX] = 0;
                    mBubbleData[i + VY] = 0;
                    break;
                }
            }
            mTouchTime = event.getEventTime();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mTouchedBubble != -1) {
                mBubbleData[mTouchedBubble + VX] = mVx;
                mBubbleData[mTouchedBubble + VY] = mVy;
            }
            mTouchedBubble = -1;
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mTouchX = event.getX();
            mTouchY = event.getY();
            long time = event.getEventTime();
            if (time == mTouchTime)
                time = mTouchTime + 1;

            if (mTouchedBubble != -1) {
                float r = mBubbleData[mTouchedBubble + RR];
                if (mTouchX < r)
                    mTouchX = r;
                if (mTouchX > getWidth() - r)
                    mTouchX = getWidth() - r;
                mVx = 10 * (mTouchX - mBubbleData[mTouchedBubble + X]) / (time - mTouchTime);

                if (mTouchY < r)
                    mTouchY = r;
                if (mTouchY > getHeight() - r)
                    mTouchY = getHeight() - r;
                mVy = 10 * (mTouchY - mBubbleData[mTouchedBubble + Y]) / (time - mTouchTime);
                mTouchTime = time;
                mBubbleData[mTouchedBubble + X] = mTouchX;
                mBubbleData[mTouchedBubble + Y] = mTouchY;

            }
            return true;
        }
        return false;
    }

    native void nativeUpdate(int nb, int touched, int width,int height,float dt,float gx,float gy,float[] mBubbles, float rigidity, float dampening, boolean compress);
}

