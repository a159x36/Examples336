package com.example.martin.contacts;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Cursor mCursor;
    private Context mActivity;
    private int mColindex;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
    public MyAdapter(Context context, Cursor cursor, String colname) {
        mActivity = context;
        mCursor = cursor;
        mColindex = mCursor.getColumnIndex(colname);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        final MyViewHolder vh = new MyViewHolder(v);
        v.setOnClickListener((view)-> {
            int pos = vh.getAdapterPosition();
            mCursor.moveToPosition(pos);
            // get the contact id
            int contact_id = mCursor.getInt(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
            String selectionargs[] = {"" + contact_id};
            // contacts may have more than one phone number so the numbers are stored in a separate table
            Cursor phones = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", selectionargs, null);
            if (phones == null) // shouldn't happen because we asked for contacts with phone numbers
                return;
            // get the first phone number
            phones.moveToFirst();
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phones.close();
            // call the number
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mActivity);
            if (pref.getBoolean(mActivity.getString(R.string.dial_key), mActivity.getResources().getBoolean(R.bool.dial_default)))
                mActivity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            else
                mActivity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

        });
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String val=mCursor.getString(mColindex);
        holder.textView.setText(val);
    }
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
