package cz.nudz.www.trainingapp.trial;


import android.databinding.DataBindingUtil;
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
import cz.nudz.www.trainingapp.utils.Utils;

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
    public void onResume() {
        super.onResume();

        Utils.enableViews(true, binding.trainingBtn, binding.tutorialBtn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mode_selection_fragment, container, false);
        activity = (BaseActivity) getActivity();

        binding.trainingBtn.setOnClickListener(v -> {
            v.setEnabled(false);
            // Check for user immediately as we cannot do anything in training without one.
            if (activity.getSessionManager().checkLogin()) {
                TrainingActivity.startActivity(activity, ParadigmType.COLOR);
            }
        });

        binding.tutorialBtn.setOnClickListener(v -> {
            v.setEnabled(false);
            TutorialPagerActivity.startActivity(activity);
        });

        binding.trialBtn.setOnClickListener(v -> {
            TrialSelectionFragment trialSelectionFragment = new TrialSelectionFragment();
            trialSelectionFragment.show(getChildFragmentManager(), "");
        });

        return binding.getRoot();
    }

}
