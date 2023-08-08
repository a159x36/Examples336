package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

public class Bubbles {
    private ArrayList<Bubble> mBubbleList;
    private int nBubbles = 100;
    private Random mRand = new Random();
    private int mWidth, mHeight;
    private long mLastTime = 0;
    Paint mTextPaint = new Paint();
    private Bubble mHeldBubble = null;

    Bubbles(int width, int height) {
        mWidth = width;
        mHeight = height;
        mBubbleList = new ArrayList<>(nBubbles);
        for (int i = 0; i < nBubbles; i++) {
            int r = mRand.nextInt(100) + 10;
            mBubbleList.add(new Bubble(mRand.nextInt(width - 2 * r) + r, mRand.nextInt(height - 2 * r) + r,
                    -5 + mRand.nextInt(1000) / 100f, -5 + mRand.nextInt(1000) / 100f, r, width, height));
        }


        mTextPaint.setColor(Color.GRAY);
        //    mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mTextPaint.setTextSize(48);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //  mTextPaint.setColorFilter(new ColorMatrixColorFilter(cm));
    }

    public void touch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Bubble b : mBubbleList) {
                    if (b.inside(x, y)) {
                        mHeldBubble = b;
                        b.hold(true);
                        return;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mHeldBubble != null)
                    mHeldBubble.hold(false);
                mHeldBubble = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHeldBubble != null)
                    mHeldBubble.move(x, y);
                break;
        }

    }

    public void draw(Canvas c) {
        for (Bubble b : mBubbleList)
            b.draw(c);
        long time = System.nanoTime();
        long dt = time - mLastTime;
        float fps = 0;
        if (dt != 0) fps = 1000000000.0f / dt;
        c.drawText("Fps:" + fps, 16, 192, mTextPaint);
        mLastTime = time;
    }

    public void update(float dt, boolean col) {
        for (Bubble b : mBubbleList) {
            b.move(dt);
            if (b.getY() < 0) {
                b.move(b.getX(), mHeight);
            }
        }
        if(col)
            collide();
    }

    float mRigidity=0.2f;
    float mDampening=0.9f;
    void collide() {
        for(int i=0;i<nBubbles;i++) {
            Bubble b = mBubbleList.get(i);
            float x = b.mX;
            float y = b.mY;
            float r = b.mR;
            for (int j = i + 1; j < nBubbles; j++) {
                Bubble b1 = mBubbleList.get(j);
                float x1 = b1.mX, y1 = b1.mY, r1 = b1.mR;
                float dx = x1 - x;
                float dy = y1 - y;
                float d = dx * dx + dy * dy;
                if (d < (r1 + r) * (r1 + r)) {
                    d = (float) Math.sqrt(d);
                    if (d != 0) { // normalise dx,dy
                        dx = dx / d;
                        dy = dy / d;
                    }
                    float displacement = (r + r1) - d;
                    b.mVx = (b.mVx - mRigidity * dx * displacement) * mDampening;
                    b.mVy = (b.mVy - mRigidity * dy * displacement) * mDampening;
                    b1.mVx = (b1.mVx + mRigidity * dx * displacement) * mDampening;
                    b1.mVy = (b1.mVy + mRigidity * dy * displacement) * mDampening;
                }
            }
        }
    }
}
