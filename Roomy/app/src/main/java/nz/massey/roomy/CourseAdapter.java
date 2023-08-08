package nz.massey.roomy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter  extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseNameView;

        private CourseViewHolder(View itemView) {
            super(itemView);
            courseNameView = itemView.findViewById(R.id.course_name);
        }
    }

    private final LayoutInflater mInflater;
    private List<Course> mCourses;

    CourseAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        if (mCourses != null) {
            Course current = mCourses.get(position);
            holder.courseNameView.setText(current.name);
        } else {
            // Covers the case of data not being ready yet.
            holder.courseNameView.setText("No Course");
        }
    }

    public void setCourses(List<Course> cs) {
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