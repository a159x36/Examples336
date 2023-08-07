package com.example.matchinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matchinggame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String NUMMATCHED_KEY = "nummatched";
    private static final String SCORE_KEY = "score";
    private static final String LAST_INDEX_KEY = "lastindex";
    private static final String BUTTON_STATE_KEY = "buttonstate";
    private static final String TAG = "MatchingGame";
    private static final String BUTTON_TURNED_KEY = "turned";
    private ActivityMainBinding mMainLayout;
    private int mNumMatched = 0;
    private int mScore = 0;
    private ImageView[] mButtons;
    private ImageView mLastButton;
    private int[] mDrawables={
      R.drawable.ic_baseline_attachment_24,
      R.drawable.ic_baseline_audiotrack_24,
      R.drawable.ic_baseline_brightness_5_24,
      R.drawable.ic_baseline_brush_24,
      R.drawable.ic_baseline_build_24,
      R.drawable.ic_baseline_flight_24,
      R.drawable.ic_baseline_local_bar_24,
      R.drawable.ic_baseline_weekend_24,
    };

    class TileState {
        public int resourceid;
        public boolean turned;
        TileState(int r, boolean t) {
            resourceid=r;
            turned=t;
        }
    }

    private void showScore() {
        if(mNumMatched==8)
            mMainLayout.done.setText(getText(R.string.completed) + ":" + mScore);
        else
            mMainLayout.done.setText(getText(R.string.score) + ":" + mScore);
    }

    private void buttonClick(ImageView b) {
        int val=((TileState)b.getTag()).resourceid;
        if (!(b.getDrawable()==null))
            return;
        mScore++;
        setButton(b,true);
        if (mLastButton == null) {
            mLastButton = b;
        } else {
            if (((TileState)mLastButton.getTag()).resourceid==val) {
                mNumMatched++;
                mLastButton = null;
            } else {
                setButton(mLastButton,false);
                mLastButton = b;
            }
        }
        showScore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout=ActivityMainBinding.inflate(getLayoutInflater());
        mButtons= new ImageView[] {
            mMainLayout.button11, mMainLayout.button12, mMainLayout.button13, mMainLayout.button14,
            mMainLayout.button21, mMainLayout.button22, mMainLayout.button23, mMainLayout.button24,
            mMainLayout.button31, mMainLayout.button32, mMainLayout.button33, mMainLayout.button34,
            mMainLayout.button41, mMainLayout.button42, mMainLayout.button43, mMainLayout.button44,
        };
        setContentView(mMainLayout.getRoot());
        mMainLayout.restart.setOnClickListener(view -> init());

        for (ImageView button:mButtons) {
            button.setOnClickListener(view -> buttonClick((ImageView) view));
        }
        if(savedInstanceState!=null) {
            restoreState(savedInstanceState);
        }
        if(mScore==0)
            init();
        showScore();
    }
    private void restoreState(Bundle inState) {
        Log.i(TAG,"RestoreInstanceState");
        mNumMatched=inState.getInt(NUMMATCHED_KEY,0);
        mScore=inState.getInt(SCORE_KEY,0);
        int lastindex=inState.getInt(LAST_INDEX_KEY,-1);
        int res[];
        boolean turned[];
        res=inState.getIntArray(BUTTON_STATE_KEY);
        turned=inState.getBooleanArray(BUTTON_TURNED_KEY);
        for(int i =0;i<16; i++) {
            mButtons[i].setTag(new TileState(res[i],turned[i]));
            if(turned[i])
                setButton(mButtons[i],true);
        }
        if(lastindex!=-1)
            mLastButton=mButtons[lastindex];
        else mLastButton=null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG,"SaveInstanceState");
        outState.putInt(NUMMATCHED_KEY, mNumMatched);
        outState.putInt(SCORE_KEY, mScore);
        int res[]= new int[16];
        boolean turned[] = new boolean[16];
        int lastindex=-1;
        for(int i =0;i<16; i++) {
            TileState s=(TileState) mButtons[i].getTag();
            res[i]=s.resourceid;
            turned[i]=s.turned;
            if(mLastButton==mButtons[i])
                lastindex=i;
        }
        outState.putInt(LAST_INDEX_KEY, lastindex);
        outState.putIntArray(BUTTON_STATE_KEY, res);
        outState.putBooleanArray(BUTTON_TURNED_KEY, turned);
        super.onSaveInstanceState(outState);
    }
    private void init() {
        mNumMatched = 0;
        mScore = 0;
        mLastButton = null;
        for (ImageView button:mButtons) {
            button.setImageDrawable(null);
            button.setTag(new TileState(0,false));
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 2; j++) {
                int x;
                TileState t;
                do {
                    x = (int) (Math.random() * 16);
                    t=(TileState)mButtons[x].getTag();
                } while (t.resourceid!=0);
                t.resourceid=mDrawables[i];
            }
        }
        showScore();
    }

    private void setButton(final ImageView button,final boolean turned) {
       final TileState t=(TileState)button.getTag();
       final int from;
       final int to;
        if (!turned) {
            from = 0;
            to = 180;
            t.turned=false;
        } else {
            from = 180;
            to = 0;
            t.turned=true;
        }
        button.setRotationY(from);
        button.animate().rotationY((from+to) / 2f).setDuration(200).withEndAction(() -> {
            if(turned)
                button.setImageResource(t.resourceid);
            else
                button.setImageDrawable(null);
            button.animate().rotationY(to).setDuration(200).setInterpolator(new BounceInterpolator()).withEndAction(() -> button.setRotationY(0));
        });

    }
}