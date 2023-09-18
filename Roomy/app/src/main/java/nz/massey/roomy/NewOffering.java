package nz.massey.roomy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import nz.massey.roomy.databinding.OfferingBinding;

public class NewOffering extends Fragment {
    void insertnew() {
        Lecturer l=(Lecturer)mNewOfferingLayout.lectspinner.getSelectedItem();
        Course c=(Course)mNewOfferingLayout.coursespinner.getSelectedItem();
        try {
            int year = Integer.parseInt(mNewOfferingLayout.year.getText().toString());
            int semester = Integer.parseInt(mNewOfferingLayout.semester.getText().toString());
            CourseOffering co = new CourseOffering(c.id, l.id, year, semester);
            MainActivity act = (MainActivity)getActivity();
            new Thread(() -> {
                act.mDao.insert(co);
                act.updatecourselist();
            }).start();
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(),R.string.badnumber,Toast.LENGTH_SHORT).show();
        }
    }
    public void populatespinners() {
        new Thread(() -> {
            MainActivity act=(MainActivity)getActivity();
            List<Lecturer> le=act.mDao.getLecturers();
            List<Course> co=act.mDao.getAllCourses();
            getActivity().runOnUiThread(() -> {
                mNewOfferingLayout.year.setText("2023");
                mNewOfferingLayout.semester.setText("2");
                mNewOfferingLayout.lectspinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, le));
                mNewOfferingLayout.coursespinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, co));
            });
        }).start();
    }
    OfferingBinding mNewOfferingLayout;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewOfferingLayout= OfferingBinding.inflate(getLayoutInflater());
        populatespinners();
        mNewOfferingLayout.okbutton.setOnClickListener((v)->insertnew());
        mNewOfferingLayout.cancelbutton.setOnClickListener((v)->
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(this).commit());
        return mNewOfferingLayout.getRoot();
    }
}