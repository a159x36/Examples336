package nz.massey.roomy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;
import nz.massey.roomy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "roomy";
    public UniDao mDao;
    public CourseAdapter mCourses;
    public ActivityMainBinding mMainLayout;
    public void updatelects() {
        new Thread(() -> {
            List<Lecturer> le= mDao.getLecturers();
            mMainLayout.namespinner.post(() ->
                    mMainLayout.namespinner.setAdapter(
                            new ArrayAdapter<Lecturer>(this, android.R.layout.simple_spinner_dropdown_item, le)));
            mMainLayout.namespinner.setOnItemSelectedListener(this);
        }).start();
    }
    public void updatecourselist() {
        new Thread(() -> {
            Lecturer le=(Lecturer)mMainLayout.namespinner.getSelectedItem();
            if(le!=null) {
                List<CourseInfo> co = mDao.getCourseInfo(le.name);
                mMainLayout.namespinner.post(() -> mCourses.setCourses(co));
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mMainLayout.getRoot());
        UniDatabase db=UniDatabase.getDatabase(this);
        mCourses=new CourseAdapter(this);
        mMainLayout.courses.setAdapter(mCourses);
        mMainLayout.courses.setLayoutManager(new LinearLayoutManager(this));
        mMainLayout.floatingActionButton.setOnClickListener((v)->
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container_view, new NewOffering())
                    .commit()
        );
               // startActivity(new Intent(this,NewOffering.class)));
        mDao =db.UniDao();
        updatelects();
        updatecourselist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatelects();
        updatecourselist();
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updatecourselist();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}