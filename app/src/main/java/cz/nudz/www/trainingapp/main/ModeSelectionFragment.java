package cz.nudz.www.trainingapp.main;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.ModeSelectionFragmentBinding;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.TrainingActivity;
import cz.nudz.www.trainingapp.trial.TrialSelectionFragment;
import cz.nudz.www.trainingapp.tutorial.TutorialPagerActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModeSelectionFragment extends Fragment {

    private ModeSelectionFragmentBinding binding;
    private BaseActivity activity;

    public ModeSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mode_selection_fragment, container, false);
        activity = (BaseActivity) getActivity();

        binding.mainActivityTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for user immediately as we cannot do anything in training without one.
                if (activity.getSessionManager().checkLogin()) {
                    TrainingActivity.startActivity(activity, ParadigmType.COLOR);
                }
            }
        });

        binding.mainActivityTutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialPagerActivity.startActivity(activity);
            }
        });

        binding.mainActivityTrialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrialSelectionFragment trialSelectionFragment = new TrialSelectionFragment();
                trialSelectionFragment.show(getChildFragmentManager(), "");
            }
        });

        return binding.getRoot();
    }

}
