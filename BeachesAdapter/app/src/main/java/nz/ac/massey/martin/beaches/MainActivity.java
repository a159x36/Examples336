package nz.ac.massey.martin.beaches;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//
// Show Surf Cams from some NZ beaches
//

public class MainActivity extends Activity {
    private static final String TAG = "Beaches";
    ImageButton reload;
    ListView beachlist;
    BeachAdapter adapter;
    // list of camera URLs, anything that returns a jpeg will work here.
    String[] urls1 = {
            "http://www.windsurf.co.nz/webcams/mairangi1_lg.jpg",
            "http://www.windsurf.co.nz/webcams/mairangi2_lg.jpg",
            "http://www.windsurf.co.nz/webcams/ptchev/PtChev.jpg",
            "http://www.windsurf.co.nz/webcams/bayswater.jpg",
            "http://greylynnweather.net/cam.jpg",
            "http://www.windsurf.co.nz/webcams/muriwai/muriwai.jpg",
            "http://www.windsurf.co.nz/webcams/orewa.jpg",
            "http://www.windsurf.co.nz/webcams/orewa2.jpg",
            "https://www.primo.nz/webcameras/snapshot_chimney_sth.jpg",
            "https://www.primo.nz/webcameras/snapshot_fitzboardriders_sth.jpg",
            "https://www.primo.nz/webcameras/snapshot_twlbuilding_sth.jpg"
    };
    // if you like roads instead of beaches
    String[] urls = {
           // "http://www.trafficnz.info/camera/601.jpg",
            "http://www.trafficnz.info/camera/654.jpg",
            "http://www.trafficnz.info/camera/603.jpg",
            "http://www.trafficnz.info/camera/604.jpg",
            "http://www.trafficnz.info/camera/605.jpg",
            "http://www.trafficnz.info/camera/655.jpg",
            "http://www.trafficnz.info/camera/608.jpg",
            "http://www.trafficnz.info/camera/610.jpg",
            "http://www.trafficnz.info/camera/612.jpg",
            "http://www.trafficnz.info/camera/651.jpg",
            "http://www.trafficnz.info/camera/653.jpg",
            "http://www.trafficnz.info/camera/10.jpg",
    };

    // use this to run 4 tasks at a time
    private ExecutorService mExecutor= Executors.newFixedThreadPool(4);

    // The adapter, this supplies data to the ListView,
    // it grabs an image in the background using an AsyncTask
    public class BeachAdapter extends BaseAdapter {
        // Holds the beach imageview and it's position in the list
        class ViewHolder {
            int position;
            ImageView image;
        }
        // How many items in the ListView, should be urls.length but make it 1000 to show
        // long lists
        @Override
        public int getCount() {
            return 1000;//urls.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @SuppressLint("StaticFieldLeak")
        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            Log.i(TAG,"getView:"+i+","+convertView);
            final ViewHolder vh;
            if (convertView == null) {
                // if it's not recycled, inflate it from xml
                convertView = getLayoutInflater().inflate(R.layout.beach, viewGroup, false);
                // create a new ViewHolder for it
                vh=new ViewHolder();
                vh.image=convertView.findViewById(R.id.beachimg);
                // and set the tag to it
                convertView.setTag(vh);
            } else
                vh=(ViewHolder)convertView.getTag(); // otherwise get the viewholder
            // set it's position
            vh.position=i;
            // and erase the image so we don't see old photos
            vh.image.setImageBitmap(null);

            // make an AsyncTask to load the image
/*
            new AsyncTask<ViewHolder,Void,Bitmap>() {
                private ViewHolder vh;
                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    vh=params[0];
                    // get the string for the url
                    String address=urls[vh.position%urls.length];
                    Bitmap bmp=null;
                    try {
                        Log.i(TAG,"Loading:"+address);
                        URL url = new URL(address);
                        // open network connection
                        URLConnection connection=url.openConnection();
                        // vh position might have changed
                        if(vh.position!=i)
                            return null;
                        // decode the jpeg into a bitmap
                        bmp = BitmapFactory.decodeStream(connection.getInputStream());
                    } catch (Exception e) {
                        Log.i(TAG,"Error Loading:"+address);
                        e.printStackTrace();
                    }
                    // return the bitmap (might be null)
                    return bmp;
                }
                @Override
                protected void onPostExecute(Bitmap bmp) {
                    // only set the imageview if the position hasn't changed.
                    if(vh.position==i) {
                        vh.image.setImageBitmap(bmp);
                    }
                }
            }.executeOnExecutor(mExecutor,vh);


 */

            mExecutor.submit( () -> {
                if(vh.position!=i)
                    return;
                String address = urls[vh.position % urls.length];
                final Bitmap bmp;
                try {
                    Log.i(TAG,"Loading:"+address);
                    URL url = new URL(address);
                    // open network connection
                    URLConnection connection=url.openConnection();
                    // vh position might have changed
                    if(vh.position!=i)
                        return;
                    // decode the jpeg into a bitmap
                    bmp = BitmapFactory.decodeStream(connection.getInputStream());
                } catch (Exception e) {
                    Log.i(TAG,"Error Loading:"+address);
                    e.printStackTrace();
                    return;
                }
                if(vh.position==i && vh.image!=null) {
                    vh.image.post(()->vh.image.setImageBitmap(bmp));
                }
            });


            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beachlist=findViewById(R.id.beaches);
        adapter=new BeachAdapter();
        beachlist.setAdapter(adapter);
        reload=findViewById(R.id.button);
        reload.setOnClickListener(view -> adapter.notifyDataSetChanged());
    }
}
