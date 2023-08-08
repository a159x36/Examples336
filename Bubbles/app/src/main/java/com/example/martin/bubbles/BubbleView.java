package com.example.martin.bubbles;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by martin on 19/09/17.
 */

public class BubbleView extends View {

    private Bubbles mBubbles;

    public BubbleView(Context context, AttributeSet attrs) {
        super(context,attrs);
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
    protected void onDraw(Canvas c) {
        if(mBubbles!=null)
            mBubbles.draw(c);
    }


    public void update(float dt, boolean collide) {
        if(mBubbles!=null)
            mBubbles.update(dt, collide);
    }
}
