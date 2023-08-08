package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by martin on 19/09/17.
 */

class Bubble {
    Paint mPaint;
    RectF mArcRect;


    protected float mX,mY,mR, mVx, mVy, mMaxX, mMaxY;
    private boolean mHolding=false;

 //   public static float sGravity=10;

    public void draw(Canvas c) {
        c.save();
        c.translate(mX,mY);
        c.drawCircle(0,0,mR,mPaint);
        c.scale(mR/100f,mR/100f);
  //      c.drawText("Hello",-mR,mR+32,mPaint);
        c.drawArc(mArcRect,300,30,false, mPaint);
        c.restore();
    }
    Bubble(int x,int y, float vx, float vy, float r, int maxx, int maxy) {
        mX=x;
        mY=y;
        mR=r;
        mVx=vx;
        mVy=vy;
        mMaxX=maxx;
        mMaxY=maxy;
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.argb(255, (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setTextSize(64);
        mPaint.setAntiAlias(true);
        float sr=2f*mR/3f;
        mArcRect=new RectF(-sr,-sr,sr,sr);

    }
    public void move(float dt) {
        if(!mHolding) {
            mX += mVx * dt * 100;
            mY += mVy * dt * 100;
            if (mX < mR || mX > (mMaxX - mR)) {
                mVx = -mVx;
                mX += mVx * dt * 100;
            }
            if (mY < mR || mY > (mMaxY - mR)) {
                mVy = -mVy;
                mY += mVy * dt * 100;
            }
        }
    }

    public void move(float x,float y) {
        mX=x;
        mY=y;
    }

    public int getX() {
        return (int)mX;
    }

    public int getY() {
        return (int)mY;
    }

    public boolean inside(float x,float y) {
        if(((x - mX) * (x - mX) + (y - mY) * (y - mY)) < mR * mR)
            return true;
        return false;
    }

    public void hold(boolean h) {
        mHolding=h;
    }
}
