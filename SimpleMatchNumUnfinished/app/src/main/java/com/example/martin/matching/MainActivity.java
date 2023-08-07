package com.example.martin.matching;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private int mLastButtonIndex = -1;
    private Button[] mButtons = new Button[16];
    private TextView mDone;
    private String[] mButtonValues = new String[16];
    private int mNumMatched = 0;
    private int mScore = 0;
    private final int[] mButtonIds= {
            R.id.button11, R.id.button12, R.id.button13, R.id.button14,
            R.id.button21, R.id.button22, R.id.button23, R.id.button24,
            R.id.button31, R.id.button32, R.id.button33, R.id.button34,
            R.id.button41, R.id.button42, R.id.button43, R.id.button44,
    };


    private void showscore() {
        if(mNumMatched==8)
            mDone.setText(getText(R.string.completed) + ":" + mScore);
        else
            mDone.setText(getText(R.string.score) + ":" + mScore);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDone = findViewById(R.id.done);
        Button mRestart = findViewById(R.id.restart);
        mRestart.setOnClickListener(view -> init());

        for (int i = 0; i < 16; i++) {
            final int v = i;
            mButtons[i] = findViewById(mButtonIds[i]);
            mButtons[i].setOnClickListener(view -> {
                Button b=(Button)view;
                String val=mButtonValues[v];
                if (!b.getText().equals(""))
                    return;
                mScore++;
                if (mLastButtonIndex == -1) {
                    mLastButtonIndex = v;
                    b.setText(val);
                } else {
                    if (mButtons[mLastButtonIndex].getText().equals(val)) {
                        b.setText(val);
                        mNumMatched++;
                        mLastButtonIndex = -1;
                    } else {
                        b.setText(val);
                        mButtons[mLastButtonIndex].setText("");
                        mLastButtonIndex = v;
                    }
                }
                showscore();
            });
        }
        if(mScore==0)
            init();
        showscore();
    }

    private void init() {
        mNumMatched = 0;
        mScore = 0;
        mLastButtonIndex = -1;
        for (int i = 0; i < 16; i++) {
            mButtons[i].setText("");
            mButtonValues[i] = "";
        }
        for (int i = 1; i < 9; i++) {
            int x;
            for (int j = 0; j < 2; j++) {
                do {
                    x = (int) (Math.random() * 16);
                } while (!"".equals(mButtonValues[x]));
                mButtonValues[x] = "" + i;
            }
        }
        showscore();
    }
}
