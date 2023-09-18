package nz.massey.roomy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import nz.massey.roomy.databinding.OfferingBinding;

public class EditOffering extends Fragment {
    private int mId;
    void update() {
        Lecturer l=(Lecturer) mEditOfferingLayout.lectspinner.getSelectedItem();
        Course c=(Course) mEditOfferingLayout.coursespinner.getSelectedItem();
        try {
            int year = Integer.parseInt(mEditOfferingLayout.year.getText().toString());
            int semester = Integer.parseInt(mEditOfferingLayout.semester.getText().toString());
            MainActivity act = (MainActivity) getActivity();
            new Thread(() -> {
                act.mDao.update(mId,l.id,c.id,year,semester);
                act.updatecourselist();
            }).start();
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(),R.string.badnumber,Toast.LENGTH_SHORT).show();
        }
    }
    public void populatedata() {
        new Thread(() -> {
            MainActivity act=(MainActivity)getActivity();
            CourseOffering coff= act.mDao.getOffering(mId);
            List<Lecturer> le=act.mDao.getLecturers();
            List<Course> co=act.mDao.getAllCourses();
            getActivity().runOnUiThread(() -> {
                mEditOfferingLayout.year.setText("2023");
                mEditOfferingLayout.semester.setText("2");
                mEditOfferingLayout.lectspinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, le));
                mEditOfferingLayout.coursespinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, co));
                for(int i=0;i<le.size();i++)
                    if(le.get(i).id==coff.lecturer_id)
                        mEditOfferingLayout.lectspinner.setSelection(i);
                for(int i=0;i<co.size();i++)
                    if(co.get(i).id==coff.course_id)
                        mEditOfferingLayout.coursespinner.setSelection(i);
                mEditOfferingLayout.year.setText(""+coff.year);
                mEditOfferingLayout.semester.setText(""+coff.semester);
            });
        }).start();
    }
    OfferingBinding mEditOfferingLayout;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId=getArguments().getInt("id",0);
        mEditOfferingLayout = OfferingBinding.inflate(getLayoutInflater());
        populatedata();
        mEditOfferingLayout.okbutton.setVisibility(View.GONE);
        mEditOfferingLayout.cancelbutton.setVisibility(View.GONE);
        mEditOfferingLayout.updatebutton.setVisibility(View.VISIBLE);
        mEditOfferingLayout.updatebutton.setOnClickListener((v)->
        {
            update();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(this).commit();
        });
        return mEditOfferingLayout.getRoot();
    }
}