package cz.nudz.www.trainingapp.training;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.databinding.PauseFragmentBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;
import static cz.nudz.www.trainingapp.training.WarningFragment.KEY_DIFFICULTY_STATE;

public class PauseFragment extends DialogFragment {

    public static final String TAG = PauseFragment.class.getSimpleName();

    private static final int SEQUENCE_TIMEOUT = 10000; // 10 sec
    private static final int PARADIGM_TIMEOUT = 3000 * 60; // 3 min

    private PauseFragmentBinding binding;
    private Paradigm currentParadigm;
    private boolean isSequencePause;
    private Adjustment adjustment;

    /**
     *
     * @param paradigm
     * @param adjustment Adjustment must only be passed for sequence pause, otherwise it has to be null signaling paradigm pause.
     * @return
     */
    public static PauseFragment newInstance(@NonNull Paradigm paradigm, @Nullable Adjustment adjustment) {
        PauseFragment pauseFragment = new PauseFragment();
        Bundle bundle = WarningFragment.bundleArguments(paradigm, adjustment);
        pauseFragment.setArguments(bundle);
        return pauseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pause_fragment, container, false);

        currentParadigm = Paradigm.valueOf(getArguments().getString(KEY_PARADIGM));
        if (getArguments().containsKey(KEY_DIFFICULTY_STATE)) {
            adjustment = Adjustment.valueOf(getArguments().getString(KEY_DIFFICULTY_STATE));
            isSequencePause = true;
        } else {
            isSequencePause = false;
        }

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        WarningFragment warningFragment = WarningFragment.newInstance(currentParadigm, adjustment);
        transaction.add(R.id.pauseFragmentMessageContainer, warningFragment);

        CountDownFragment countDownFragment = CountDownFragment.newInstance(isSequencePause ? SEQUENCE_TIMEOUT : PARADIGM_TIMEOUT);
        transaction.add(R.id.pauseFragmentCountDownContainer, countDownFragment);

        transaction.commit();

        return binding.getRoot();
    }
}
