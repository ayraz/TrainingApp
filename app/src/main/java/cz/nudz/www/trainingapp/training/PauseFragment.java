package cz.nudz.www.trainingapp.training;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.PauseFragmentBinding;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class PauseFragment extends DialogFragment implements CountDownFragment.OnCountDownListener {

    private static final String KEY_DIFFICULTY_STATE = "KEY_DIFFICULTY_STATE";
    private static final String KEY_PAUSE_TYPE = "KEY_PAUSE_TYPE";

    private static final int SEQUENCE_TIMEOUT = 10000; // 10 sec
    private static final int PARADIGM_TIMEOUT = 3000 * 60; // 3 min

    private PauseFragmentBinding binding;
    private Paradigm currentParadigm;
    private boolean isSequencePause;
    private Adjustment adjustment;

    public static PauseFragment newInstance(@NonNull Paradigm paradigm, boolean isSequencePause, @Nullable Adjustment adjustment) {
        PauseFragment pauseFragment = new PauseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PARADIGM, paradigm.toString());
        bundle.putBoolean(KEY_PAUSE_TYPE, isSequencePause);
        if (adjustment != null) {
            bundle.putString(KEY_DIFFICULTY_STATE, adjustment.toString());
        }
        pauseFragment.setArguments(bundle);
        return pauseFragment;
    }

    private int getHelpTextForParadigm() {
        if (isSequencePause) {
            switch (adjustment) {
                case LOWERED:
                    return R.string.difficultyLoweredMessage;
                case SAME:
                    return R.string.difficultySameMessage;
                case RAISED:
                    return R.string.difficultyRaisedMessage;
            }
        } else {
            switch (currentParadigm) {
                case COLOR:
                    return R.string.colorParadigmStartHelp;
                case SHAPE:
                    return R.string.shapeParadigmStartHelp;
                case POSITION:
                    return R.string.positionParadigmStartHelp;
            }
        }
        return R.string.genericErrorMessage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pause_fragment, container, false);

        if (!getArguments().containsKey(KEY_PAUSE_TYPE))
            throw new IllegalStateException("Pause type must be present.");

        currentParadigm = Paradigm.valueOf(getArguments().getString(KEY_PARADIGM));
        isSequencePause = getArguments().getBoolean(KEY_PAUSE_TYPE, false);

        if (getArguments().containsKey(KEY_DIFFICULTY_STATE))
            adjustment = Adjustment.valueOf(getArguments().getString(KEY_DIFFICULTY_STATE));
        else if (isSequencePause)
            throw new IllegalStateException("Difficulty state must be present during sequence pause.");

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        WarningFragment warningFragment = WarningFragment.newInstance(getHelpTextForParadigm());
        transaction.add(R.id.pauseFragmentMessageContainer, warningFragment);

        CountDownFragment countDownFragment = CountDownFragment.newInstance(isSequencePause ? SEQUENCE_TIMEOUT : PARADIGM_TIMEOUT);
        transaction.add(R.id.pauseFragmentCountDownContainer, countDownFragment);

        transaction.commit();

        return binding.getRoot();
    }

    @Override
    public void onCountDownExpired() {
        dismiss();
    }

    @Override
    public void onContinueClicked() {
        TrainingActivity.startActivity(getActivity(), currentParadigm);
    }
}
