package nz.massey.simplecamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISIONS = 1;
    final String TAG="SimpleCamera";
    ImageView mShutterButton,mGalleryButton;
    SurfaceView mSurfaceView;
    Camera mCamera;
    private boolean mPhotoInProgesss;
    private SoundPool mSoundPool;
    private int mClick;

    void init() {
        mSurfaceView=findViewById(R.id.camera_preview);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mCamera = Camera.open(0);
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    Camera.Parameters params = mCamera.getParameters();
                    List<Camera.Size> sizes = params.getSupportedPictureSizes();
                    Camera.Size largest = sizes.get(0);
                    for (Camera.Size sz : sizes)
                        if (sz.height * sz.width > largest.width * largest.height)
                            largest = sz;
                    params.setPictureSize(largest.width, largest.height);
                    // most phones have this preview size
                    params.setPreviewSize(640,480);
                    List<String> focusmodes=params.getSupportedFocusModes();
                    if(focusmodes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    mCamera.setParameters(params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCamera.stopPreview();
            }
        });
        mSurfaceView.setVisibility(View.VISIBLE);
        mSoundPool= new SoundPool(4, AudioManager.STREAM_MUSIC,1);
        mClick=mSoundPool.load(this,R.raw.camera_click,1);
        mShutterButton=findViewById(R.id.shutter_button);
        mGalleryButton=findViewById(R.id.gallery);

        mShutterButton.setOnClickListener(view -> {
            if(mPhotoInProgesss)
                return;
            mPhotoInProgesss=true;
            mShutterButton.animate().scaleX(1.5f).scaleY(1.5f);
            mCamera.takePicture(null, null, (bytes, camera) -> {
                mPhotoInProgesss=false;
                mShutterButton.animate().scaleX(1).scaleY(1);
                new Thread(() -> {
                    String name = "IMG" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date(
                            System.currentTimeMillis()));
                    ContentValues newphoto = new ContentValues();
                    newphoto.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name);
                    newphoto.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    newphoto.put(MediaStore.MediaColumns.DATE_TAKEN,System.currentTimeMillis());
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newphoto);
                    mCamera.startPreview();
                    OutputStream op;
                    try {
                        op = getContentResolver().openOutputStream(uri);
                        op.write(bytes);
                        op.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            );
            mSoundPool.play(mClick,1, 1, 1, 0, 1);
        });

        mGalleryButton.setOnClickListener(view -> {

            final String[] imageColumns = {MediaStore.Images.Media._ID};
            final String imageOrderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
            imageCursor.moveToFirst();
            long photoId = 0;
            if(imageCursor!=null) {
                if (imageCursor.moveToFirst()) photoId = imageCursor.getLong(0);
                imageCursor.close();
            }
            Log.i(TAG,"id="+photoId);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + photoId),"image/jpeg");
            startActivity(intent);
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED/* ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/)) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA/*,Manifest.permission.WRITE_EXTERNAL_STORAGE*/}, MainActivity.REQUEST_PERMISIONS);
            mSurfaceView.setVisibility(View.GONE);
        } else
            init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISIONS:
                boolean granted=true;
                for(int r:grantResults)
                    if(r!=PackageManager.PERMISSION_GRANTED)
                        granted=false;
                if (granted) {
                    init();
                    return;
                } else {
                    Toast.makeText(this, "This permission is required", Toast.LENGTH_LONG)
                            .show();
                }
                mSurfaceView.setVisibility(View.VISIBLE);
        }
    }
}
