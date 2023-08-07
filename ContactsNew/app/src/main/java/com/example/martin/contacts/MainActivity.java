package com.example.martin.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView mList;
    RecyclerView.LayoutManager mLayoutManager;
    Cursor mCursor;
    MyAdapter mAdapter;
    int mPosition=0;

    void init() {
        // get preference to see if contacts are displayed in reverse order
        final SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean sort_rev=pref.getBoolean(getString(R.string.sort_rev_key),getResources().getBoolean(R.bool.sort_rev_default));
        // get contacts with a phone number
        mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER, null, ContactsContract.Contacts.DISPLAY_NAME+(sort_rev?" DESC":" ASC"));

        mAdapter=new MyAdapter(this,mCursor,ContactsContract.Contacts.DISPLAY_NAME);
        mLayoutManager=new LinearLayoutManager(this);
        mList.setLayoutManager(mLayoutManager);
        // set the adapter
        mList.setAdapter(mAdapter);
    }


    // for the app bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu,m);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // save the list position
        mPosition=((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();//findmList.getFirstVisiblePosition();
        // close the cursor (will be opened again in init() during onResume())
        mCursor.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        // reinit in case things have changed
        init();
        // set the list position
//        mLayoutManager
        mList.scrollToPosition(mPosition);
    }

    // for the app bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = findViewById(R.id.mylist);
        // ask for permissions
        if (Build.VERSION.SDK_INT > 23 && (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ))
            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 1);
        else
            init();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull  String[] permissions, int[] grantResults) {
        // check all permissions have been granted
        boolean granted=true;
        for(int result: grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                granted=false;
            }
        }
        if(granted)
            init();
        else
            finish();
    }


}
