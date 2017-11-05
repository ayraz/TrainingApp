package cz.nudz.www.trainingapp.trial;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TrialSelectionFragmentBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrialSelectionFragment extends DialogFragment {

    private TrialSelectionFragmentBinding binding;

    public TrialSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.trial_selection_fragment, container, false);

        binding.paradigmTypeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.paradigmTypeList.setAdapter(new TrialRowAdapter((v, paradigmType, difficulty) -> TrialActivity.startActivity(getActivity(), paradigmType, difficulty)));

        return binding.getRoot();
    }
}
