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
import cz.nudz.www.trainingapp.enums.Adjustment;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;

import static cz.nudz.www.trainingapp.training.MessageFragment.KEY_DIFFICULTY;
import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class PauseFragment extends DialogFragment {

    public static final String TAG = PauseFragment.class.getSimpleName();

    private static final int SEQUENCE_TIMEOUT = 20000; // 10 sec
    private static final int PARADIGM_TIMEOUT = 3000 * 60; // 3 min

    private PauseFragmentBinding binding;

    public static PauseFragment newInstance(@NonNull ParadigmType paradigmType) {
        PauseFragment pauseFragment = new PauseFragment();
        Bundle bundle = MessageFragment.bundleArguments(paradigmType, null, null, null);
        pauseFragment.setArguments(bundle);
        return pauseFragment;
    }

    public static PauseFragment newInstance(@NonNull ParadigmType paradigmType,
                                            @Nullable Adjustment adjustment,
                                            @NonNull int sequenceCount,
                                            @Nullable Difficulty difficulty) {
        PauseFragment pauseFragment = new PauseFragment();
        Bundle bundle = MessageFragment.bundleArguments(paradigmType, adjustment, sequenceCount, difficulty);
        pauseFragment.setArguments(bundle);
        return pauseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pause_fragment, container, false);

        ParadigmType currentParadigmType = ParadigmType.valueOf(getArguments().getString(KEY_PARADIGM));
        Integer pauseDuration = 0;
        Integer sequenceCount = 0;
        Adjustment adjustment = null;
        Difficulty difficulty = null;
        // if we have any sort of adjustment, then this is inter-sequence pause
        if (getArguments().containsKey(MessageFragment.KEY_ADJUSTMENT)) {
            adjustment = Adjustment.valueOf(getArguments().getString(MessageFragment.KEY_ADJUSTMENT));
            sequenceCount = getArguments().getInt(MessageFragment.KEY_SEQ_COUNT);
            difficulty = Difficulty.valueOf(getArguments().getString(KEY_DIFFICULTY));
            pauseDuration = SEQUENCE_TIMEOUT;
        } else {
            pauseDuration = PARADIGM_TIMEOUT;
        }

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        MessageFragment messageFragment = MessageFragment.newInstance(
                currentParadigmType, adjustment, sequenceCount, difficulty);
        transaction.add(R.id.pauseFragmentMessageContainer, messageFragment);

        CountDownFragment countDownFragment = CountDownFragment.newInstance(pauseDuration);
        transaction.add(R.id.pauseFragmentCountDownContainer, countDownFragment);

        transaction.commit();

        return binding.getRoot();
    }
}
