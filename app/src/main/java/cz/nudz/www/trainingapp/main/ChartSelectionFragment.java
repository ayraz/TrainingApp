package cz.nudz.www.trainingapp.main;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.ChartSelectionFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartSelectionFragment extends Fragment {

    private static final String TAG = ChartSelectionFragment.class.getSimpleName();
    private ChartSelectionFragmentBinding binding;

    public ChartSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.chart_selection_fragment, container, false);

        binding.chartList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.chartList.setAdapter(new ChartRowAdapter());

        return binding.getRoot();
    }

}
