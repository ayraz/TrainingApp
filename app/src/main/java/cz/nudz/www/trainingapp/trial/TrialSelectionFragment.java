package cz.nudz.www.trainingapp.trial;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TrialSelectionFragmentBinding;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrialSelectionFragment extends DialogFragment {

    public static final String TAG = TrialSelectionFragment.class.getSimpleName();
    public static final int TEST_TRIAL_COUNT = 3;

    private TrialSelectionFragmentBinding binding;
    private OnTrialSelectedListener listener;

    public TrialSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.trial_selection_fragment, container, false);

        binding.paradigmTypeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.paradigmTypeList.setAdapter(new TrialRowAdapter((v, paradigmType, difficulty) -> listener.onTrialSelected(paradigmType, difficulty)));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            listener = (OnTrialSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnTrialSelectedListener.class.getSimpleName());
        }
    }

    public interface OnTrialSelectedListener {

        void onTrialSelected(ParadigmType paradigmType, Difficulty difficulty);
    }
}
