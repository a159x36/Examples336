package nz.massey.roomy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    CourseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mCourseLayout=CourseBinding.inflate(mInflater);
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CourseBinding itemView = CourseBinding.inflate(mInflater, parent, false);
        return new CourseViewHolder(itemView);
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