package cz.nudz.www.trainingapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerformanceSummaryFragment extends Fragment {


    public PerformanceSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.performance_summary_fragment, container, false);
    }

}
