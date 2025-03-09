package com.example.martin.matching;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

    // Tag for use by Log
    private static final String TAG = "MatchingGame";
    // Keys used to store the state in onSaveInstanceState
    private static final String SCORE="SCORE";
    private static final String LAST ="LAST";
    private static final String MATCHED="MATCH";
    private static final String TILEVALS="TILEVALS";
    private static final String TURNED="TURNED";
    // Number of tiles
    // this must be devisible by 8 for the initialization code to work.
    private static final int NTILES=64;
    // Number of columns in the gridview
    private static final int NCOLS=4;
    // Index of previous tile, -1 means no previous button
    private int mLastTileIndex = -1;
  //  private ImageView mLastView;
    // 4x4 grid of image tiles
    private GridView mTiles;
    // button to restart the game
    private Button mRestart;
    // Shows the score
    private TextView mDone;
    // index of drawable behind a tile
    private int mTileValues[] = new int[NTILES];
    // has a tile been turned round
    private boolean mTurned[] = new boolean[NTILES];
    // number of matched tiles 0..8
    private int mNumMatched = 0;
    // Adapter to provide tile data for the GridView
    private TileAdapter mTileAdapter;
    // current score
    private int mScore = 0;
    // how the tile flip animation moves
    private final TimeInterpolator mFlipInterpolator = new AccelerateDecelerateInterpolator();

    // ids for the icons
    private final int[] mDrawables = {
            R.drawable.ic_attachment_black_24dp,
            R.drawable.ic_audiotrack_black_24dp,
            R.drawable.ic_brightness_5_black_24dp,
            R.drawable.ic_brush_black_24dp,
            R.drawable.ic_build_black_24dp,
            R.drawable.ic_flight_black_24dp,
            R.drawable.ic_spa_black_24dp,
            R.drawable.ic_weekend_black_24dp
    };

    // for pinch to zoom
    private ScaleGestureDetector mScaleGestureDetector;

    // gets view data
    public class TileAdapter extends BaseAdapter {
        class ViewHolder {
            int position;
            ImageView image;
        }
        // how many tiles
        @Override
        public int getCount() {
            return NTILES;
        }
        // not used
        @Override
        public Object getItem(int i) {
            return null;
        }
        // not used
        @Override
        public long getItemId(int i) {
            return i;
        }

        // populate a view
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder vh;
            //ImageView image;
            if (convertView == null) {
                // if it's not recycled, inflate it from xml
                convertView = getLayoutInflater().inflate(R.layout.tile,  viewGroup, false);
                // convertview will be a LinearLayout
                vh=new ViewHolder();
                vh.image=convertView.findViewById(R.id.tilebtn);
                // and set the tag to it
                convertView.setTag(vh);
            } else
                vh=(ViewHolder)convertView.getTag();
            // set size to be square
            convertView.setMinimumHeight(mTiles.getWidth() /  mTiles.getNumColumns());
            // make sure it isn't rotated
            vh.image.setRotationY(0);
            // if it's turned over, show it's icon
            if (mTurned[i])
                vh.image.setImageResource(mDrawables[mTileValues[i]]);
            else
                vh.image.setImageDrawable(null);
            vh.position=i;

            return convertView;
        }
    }

    public static class SureDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            MainActivity activity=(MainActivity)getActivity();
            return  new AlertDialog.Builder(activity)
                    .setMessage(R.string.sure)
                    .setPositiveButton(android.R.string.ok, (di, i) -> activity.init())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart) {
            new SureDialog().show(getSupportFragmentManager(),null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG,"onSaveInstanceState()");
        // Save the user's current game state
        savedInstanceState.putInt(SCORE, mScore);
        savedInstanceState.putInt(LAST, mLastTileIndex);
        savedInstanceState.putInt(MATCHED, mNumMatched);
        savedInstanceState.putIntArray(TILEVALS, mTileValues);
        savedInstanceState.putBooleanArray(TURNED, mTurned);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    // show the score
    private void showscore() {
        Log.i(TAG,"score="+mScore);
        String score=getText(R.string.app_name).toString()+"  ";
        if(mNumMatched==8) {
            score+=getText(R.string.complete).toString()+": "+mScore;
        } else {
            score+=getText(R.string.score).toString()+": "+mScore;
        }
        ActionBar ab=getSupportActionBar();
        if(ab!=null) ab.setTitle(score);
    }

    // turn tile v round to show drawable n or blank if n is -1
    // if turnback is true, turn the tile around again after a 400ms delay
    private void setTile(final int v, final int n, final boolean turnback, int delay) {
        final int from, to;
        final TileAdapter.ViewHolder vh;

        // get the View for the tile if it's visible
        View layout=mTiles.getChildAt(v-mTiles.getFirstVisiblePosition());
        if(layout!=null)
            vh=(TileAdapter.ViewHolder)layout.getTag();
        else {
            // if not visible make sure it's turned back
            mTurned[v] = false;
            return;
        }
        // rotation animation start and end values
        if(!mTurned[v]) {
            from = -180;
            to = 0;
        } else {
            from=0;
            to=-180;
        }
        // record if it's turned or not
        if (n == -1)
            mTurned[v] = false;
        else
            mTurned[v] = true;
        // if view is no longer valid
        if(vh.position!=v) {
            if(turnback)
                mTurned[v] = false;
            return;
        }
        // if we are turning it back, make it visible first.
        if(n==-1) {
            vh.image.setImageResource(mDrawables[mTileValues[v]]);
        } else {
            vh.image.setImageDrawable(null);
        }
        // set initial rotation
        vh.image.setRotationY(from);
        // rotate half way
        vh.image.animate().rotationY((from+to)/2f).setDuration(200).setInterpolator(mFlipInterpolator).setStartDelay(delay).withEndAction(() -> {
            // after first animation change the tile drawable
            if(vh.position!=v) {
                if(turnback)
                    mTurned[v] = false;
                return;
            }
            if (n == -1)
                vh.image.setImageDrawable(null);
            else
                vh.image.setImageResource(mDrawables[n]);
            // then rotate the rest of the way
            vh.image.animate().rotationY(to).setStartDelay(0).setInterpolator(mFlipInterpolator).setDuration(5000).withEndAction(() -> {
                if(turnback)
                    setTile(v,-1,false,400);
            });
        });

    }

    private void setTile(final int v, final int n) {
        setTile(v,n,false,0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate:"+mScore);
        // set layout
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        // if activity has been restarted, restore the state of the game
        if(savedInstanceState!=null) {
            mScore = savedInstanceState.getInt(SCORE, 0);
            mLastTileIndex = savedInstanceState.getInt(LAST, 0);
            mNumMatched = savedInstanceState.getInt(MATCHED, 0);
            mTileValues = savedInstanceState.getIntArray(TILEVALS);
            mTurned = savedInstanceState.getBooleanArray(TURNED);
        }
        // set the number of columns in the grid
        mTiles=findViewById(R.id.gridview);
        mTiles.setNumColumns(NCOLS);
        // and the adapter for tile data
        mTileAdapter=new TileAdapter();
        mTiles.setAdapter(mTileAdapter);
        // when a tile is clicked
        mTiles.setOnItemClickListener((adapterView, view, v, l) -> {
            // if already turned round do nothing
            if (mTurned[v])
                return;
            // increment score for each try
            mScore++;
            // if first try
            if (mLastTileIndex == -1) {
                // just turn round this tile
                mLastTileIndex = v;
                setTile(v,  mTileValues[v]);
            } else {
                // if it matches the previous tile
                if (mTileValues[mLastTileIndex] == mTileValues[v]) {
                    // keep them both turned round
                    setTile(v, mTileValues[v]);
                    mNumMatched++;
                    mLastTileIndex = -1;
                } else {
                    // doesn't match: turn it round
                    setTile(v,  mTileValues[v], true, 0);
                    // and turn the previous one back
                    setTile(mLastTileIndex,  -1);
                    // make this the new previous tile
                    mLastTileIndex = -1;
                }
            }
            showscore();
        });
        // for pinch to zoom
        mScaleGestureDetector=new ScaleGestureDetector(this,new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            // must be a float so it knows if we are half way between integer values
            private float mCols = NCOLS;
            // not used
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
            // nut used
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }
            // change the columns if necessary
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                //
                mCols = mCols/ detector.getScaleFactor();
                if(mCols<1)
                    mCols=1;
                if(mCols>8)
                    mCols=8;
                mTiles.setNumColumns((int)mCols);
                // recalculate the tile heights
                for(int i=0;i<mTiles.getChildCount();i++) {
                    if (mTiles.getChildAt(i) != null) {
                        mTiles.getChildAt(i).setMinimumHeight(( (mTiles.getWidth() / (int)(mCols))));
                    }
                }
                // make sure it's redrawn
                mTiles.invalidate();
                return true;
            }
        });
        // call the ScaleGestureDetector when the view is touched
        mTiles.setOnTouchListener((View view, MotionEvent motionEvent) -> {
                Log.i(TAG,"onTouch:"+ motionEvent.getAction()+","+ motionEvent.getX()+","+ motionEvent.getY());
                mScaleGestureDetector.onTouchEvent(motionEvent);
                return false;
        });
        // only init if not restored
        if(mScore==0)
            init();
        showscore();
    }

    private void init() {
        // initialise the score, number matched and last tile index.
        mNumMatched = 0;
        mScore = 0;
        mLastTileIndex = -1;
        // initialise tile values
        for (int i = 0; i < NTILES; i++) {
            mTileValues[i] = -1;
            mTurned[i]=false;
        }
        // for all 8 images
        for (int i = 0; i < 8; i++) {
            int x;
            // choose some unused tiles and set their values to i
            for (int j = 0; j < NTILES/8; j++) {
                do {
                    x = (int) (Math.random() * NTILES);
                } while (mTileValues[x]!=-1);
                mTileValues[x] =  i;
            }
        }
        // tell the tile adapted that data has changed
        mTileAdapter.notifyDataSetInvalidated();
    }
}
