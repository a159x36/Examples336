package com.example.mjjohnso.simplematch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String SCORE = "SCORE";
    private static final String LAST = "LAST";
    private static final String MATCH = "MATCH";
    private static final String NUMS = "NUMS";

    private TextView mDone;
    private Button mRestart;
    private String mButtonValues[]=new String[16];
    private int mScore=0;
    private int mNumMatched=0;
    private int mLastButtonIndex=-1;
    private Button mButtons[]=new Button[16];
    private final int[] mButtonIds={
            R.id.button11,R.id.button12,R.id.button13,R.id.button14,
            R.id.button21,R.id.button22,R.id.button23,R.id.button24,
            R.id.button31,R.id.button32,R.id.button33,R.id.button34,
            R.id.button41,R.id.button42,R.id.button43,R.id.button44
    };

    private void setButton(final int i,final String s) {
          // mButtons[i].setText(s);

        final int from;
        final int to;
        if (s == "") {
            from = 0;
            to = 180;
        } else {
            from = 180;
            to = 0;
        }
        mButtons[i].setRotationY(from);

        mButtons[i].animate().rotationY((from+to) / 2).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                mButtons[i].setText(s);
                mButtons[i].animate().rotationY(to).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mButtons[i].setRotationY(0);
                    }
                });
            }
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SCORE, mScore);
        savedInstanceState.putInt(LAST, mLastButtonIndex);
        savedInstanceState.putInt(MATCH, mNumMatched);
        savedInstanceState.putStringArray(NUMS, mButtonValues);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null) {
            mScore=savedInstanceState.getInt(SCORE,0);
            mLastButtonIndex=savedInstanceState.getInt(LAST,-1);
            mNumMatched=savedInstanceState.getInt(MATCH,0);
            mButtonValues=savedInstanceState.getStringArray(NUMS);
        }
        mDone=(TextView)findViewById(R.id.done);
        mRestart=(Button)findViewById(R.id.restart);
        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
        for(int i=0;i<16;i++) {
            final int v=i;
            mButtons[i]=(Button)findViewById(mButtonIds[i]);
            mButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!"".equals(mButtons[v].getText()) || mButtons[v].getRotationY()!=0)
                        return;
                    mScore++;
                    setButton(v,mButtonValues[v]);
                    if(mLastButtonIndex==-1) {
                        mLastButtonIndex=v;
                    } else {
                        if(mButtons[mLastButtonIndex].getText().equals(mButtonValues[v])) {
                            mNumMatched++;
                            mLastButtonIndex=-1;
                        } else {
                            setButton(mLastButtonIndex,"");
                            mLastButtonIndex=v;
                        }

                    }
                    showscore();
                }
            });
        }
        if(mScore==0)
            init();
        showscore();
    }

    private void showscore() {
        if(mNumMatched==8) {
            mDone.setText("Complete:"+mScore);
        } else {
            mDone.setText("Score:"+mScore);
        }
    }
    private void init() {
        mNumMatched=0;
        mScore=0;
        mLastButtonIndex=-1;
        for (int i=0;i<16;i++) {
            mButtons[i].setText("");
            mButtonValues[i]="";
        }
        for(int i=1;i<9;i++) {
            int x;
            for(int j=0;j<2;j++) {
                do {
                    x=(int)(Math.random()*16);
                } while(!"".equals(mButtonValues[x]));
                mButtonValues[x]=""+i;
            }
        }
    }
}
