package com.example.martin.bubbles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.CollapsibleActionView;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

/**
 * Created by martin on 19/09/17.
 */

public class BubbleSurfaceView extends SurfaceView {

    private Bubbles mBubbles;
    private Paint mBgPaint;

    public BubbleSurfaceView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mBgPaint=new Paint();
        mBgPaint.setColor(Color.GRAY);
        this.setFocusable(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed)
            mBubbles=new Bubbles(getWidth(),getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mBubbles!=null)
            mBubbles.touch(event);
        return true;
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
//        c.drawRect(0,0,getWidth(),getHeight(),mBgPaint);
        if(mBubbles!=null)
            mBubbles.draw(c);
    }

    public void update(float dt, boolean collide) {
        if(mBubbles!=null)
            mBubbles.update(dt, collide);
    }
}
