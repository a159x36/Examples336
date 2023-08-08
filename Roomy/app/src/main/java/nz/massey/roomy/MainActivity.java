package nz.massey.roomy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "roomy";
    UniDao mDao;
    CourseAdapter mCourses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UniDatabase db=UniDatabase.getDatabase(this);
        RecyclerView r=findViewById(R.id.courses);
        mCourses=new CourseAdapter(this);
        r.setAdapter(mCourses);
        r.setLayoutManager(new LinearLayoutManager(this));

        mDao=db.UniDao();
        new Thread(() -> {
            List<Course> co = mDao.getCourses("Martin",2020);
            mCourses.setCourses(co);
            Log.i(TAG,"Got "+co.size()+" courses");
            for (Course c : co) {
                Log.i(TAG, "Course :" + c.name + " " + c.campus);
            }
        }).start();
    }
}