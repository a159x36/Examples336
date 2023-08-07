package com.example.mjjohnso.simplematch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String SCORE = "SCORE";
    private static final String LAST = "LAST";
    private static final String MATCH = "MATCH";
    private static final String NUMS = "NUMS";
    private static final String TURNED = "TURNED";
    private static final String TAG = "Matching Game";

    private int[] mTileValues = new int[16];
    private boolean[] mTurned = new boolean[16];
    private int mScore=0;
    private int mNumMatched=0;
    private int mLastTileIndex =-1;
    private ImageView[] mTiles = new ImageView[16];
    private final int[] mTileIds ={
            R.id.button11,R.id.button12,R.id.button13,R.id.button14,
            R.id.button21,R.id.button22,R.id.button23,R.id.button24,
            R.id.button31,R.id.button32,R.id.button33,R.id.button34,
            R.id.button41,R.id.button42,R.id.button43,R.id.button44
    };

    private int[] mDrawables={
      R.drawable.ic_attachment_black_24dp,
      R.drawable.ic_audiotrack_black_24dp,
      R.drawable.ic_brightness_5_black_24dp,
      R.drawable.ic_brush_black_24dp,
      R.drawable.ic_build_black_24dp,
      R.drawable.ic_flight_black_24dp,
      R.drawable.ic_spa_black_24dp,
      R.drawable.ic_weekend_black_24dp,
    };

    public static class SureDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MainActivity activity=(MainActivity)getActivity();
            return  new AlertDialog.Builder(activity)
                    .setMessage("Are you sure")
                    .setPositiveButton("Yes", (di, i) -> activity.init())
                    .setNegativeButton("No", null)
                    .create();
        }
    }

    private void setTile(final int i, final int s) {
        final int from;
        final int to;
        if (s == -1) {
            from = 0;
            to = 180;
            mTurned[i]=false;
        } else {
            from = 180;
            to = 0;
            mTurned[i]=true;
        }
        mTiles[i].setRotationY(from);

        mTiles[i].animate().rotationY((from+to) / 2f).setDuration(100).withEndAction(() -> {
                if(s!=-1)
                    mTiles[i].setImageResource(mDrawables[s]);
                else
                    mTiles[i].setImageDrawable(null);
                mTiles[i].animate().rotationY(to).setDuration(100).withEndAction(() -> mTiles[i].setRotationY(0));
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SCORE, mScore);
        savedInstanceState.putInt(LAST, mLastTileIndex);
        savedInstanceState.putInt(MATCH, mNumMatched);
        savedInstanceState.putIntArray(NUMS, mTileValues);
        savedInstanceState.putBooleanArray(TURNED,mTurned);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart) {
            /*
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.restart);
            dialog.setMessage(R.string.sure);
            dialog.setPositiveButton(android.R.string.ok, (arg0,arg1) -> init());
            dialog.setNegativeButton(android.R.string.cancel,null);
            dialog.create().show();
            */
             new SureDialog().show(getSupportFragmentManager(),null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        for(int i=0;i<16;i++) {
            final int v=i;
            mTiles[i]=findViewById(mTileIds[i]);
            mTiles[i].setOnClickListener(view -> {
                if(mTurned[v])
                    return;
                mScore++;
                setTile(v, mTileValues[v]);
                if(mLastTileIndex ==-1) {
                    mLastTileIndex =v;
                } else {
                    if(mTileValues[mLastTileIndex]== mTileValues[v]) {
                        mNumMatched++;
                        mLastTileIndex =-1;
                    } else {
                        setTile(mLastTileIndex,-1);
                        mLastTileIndex =v;
                    }
                }
                showscore();
            });
        }
        if(savedInstanceState!=null) {
            mScore=savedInstanceState.getInt(SCORE,0);
            mLastTileIndex =savedInstanceState.getInt(LAST,-1);
            mNumMatched=savedInstanceState.getInt(MATCH,0);
            mTileValues =savedInstanceState.getIntArray(NUMS);
            mTurned=savedInstanceState.getBooleanArray(TURNED);
            for(int i=0;i<16;i++)
                if(mTurned!=null && mTurned[i]) {
                    setTile(i, mTileValues[i]);
                }
        }

        if(mScore==0)
            init();
        else
            showscore();
    }

    private void showscore() {
        Log.i(TAG,"score="+mScore);
        String score=getText(R.string.app_name).toString()+"  ";
        if(mNumMatched==8) {
            score+=getText(R.string.complete).toString()+mScore;
        } else {
            score+=getText(R.string.score).toString()+mScore;
        }
        ActionBar ab=getSupportActionBar();
        if(ab!=null) ab.setTitle(score);
    }
    private void init() {
        mNumMatched=0;
        mScore=0;
        mLastTileIndex =-1;
        for (int i=0;i<16;i++) {
            mTiles[i].setImageDrawable(null);
            mTileValues[i]=-1;
            mTurned[i]=false;
        }
        for(int i=0;i<8;i++) {
            int x;
            for(int j=0;j<2;j++) {
                do {
                    x=(int)(Math.random()*16);
                } while(mTileValues[x]!=-1);
                mTileValues[x]=i;
            }
        }
        showscore();
    }
}
