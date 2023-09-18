package nz.massey.roomy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nz.massey.roomy.databinding.CourseBinding;

public class CourseAdapter  extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private static final String TAG = "CourseAdapter";
    CourseBinding mCourseLayout;
    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final CourseBinding courseLayout;

        private CourseViewHolder(CourseBinding item) {
            super(item.getRoot());
            courseLayout = item;
        }
    }

    private final LayoutInflater mInflater;
    private List<CourseInfo> mCourses;

    MainActivity mContext;

    CourseAdapter(AppCompatActivity context) {
        mContext=(MainActivity) context;
        mInflater = LayoutInflater.from(context);
        mCourseLayout=CourseBinding.inflate(mInflater);
    }

    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CourseBinding itemView = CourseBinding.inflate(mInflater, parent, false);
        CourseViewHolder vh=new CourseViewHolder(itemView);
        itemView.edit.setOnClickListener(view -> {
            int id=mCourses.get(vh.getAbsoluteAdapterPosition()).id;
            EditOffering frag=new EditOffering();
            Bundle b=new Bundle();
            b.putInt("id",id);
            frag.setArguments(b);
            mContext.getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container_view, frag)
                    .commit();
        });
        itemView.delete.setOnClickListener(view -> {
            new Thread(() -> {
                long id=mCourses.get(vh.getAbsoluteAdapterPosition()).id;
                mContext.mDao.deleteOffering(id);
                mContext.updatecourselist();
            }).start();
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        if (mCourses != null) {

            CourseInfo current = mCourses.get(position);
            Log.i(TAG,"c:" + current.coursename);
            holder.courseLayout.courseName.setText(current.coursename);
            holder.courseLayout.lecturerName.setText(current.lecturername);
            holder.courseLayout.year.setText(""+current.year);
            holder.courseLayout.semester.setText("s"+current.semester);
        } else {
            // Covers the case of data not being ready yet.
            holder.courseLayout.courseName.setText("No Course");
        }
    }

    public void setCourses(List<CourseInfo> cs) {
        mCourses=cs;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {

        if (mCourses != null) {
            return mCourses.size();
        }
        else return 0;
    }
}