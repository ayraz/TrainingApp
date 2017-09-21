package cz.nudz.www.trainingapp.training;


import android.content.Context;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.databinding.WarningFragmentBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

import static cz.nudz.www.trainingapp.training.Adjustment.*;
import static cz.nudz.www.trainingapp.training.Paradigm.*;
import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;
import static cz.nudz.www.trainingapp.training.TrainingActivity.startActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarningFragment extends Fragment {

    public static final String KEY_DIFFICULTY_STATE = "KEY_DIFFICULTY_STATE";
    public static final String TAG = WarningFragment.class.getSimpleName();
    private WarningFragmentBinding binding;
    private Paradigm currentParadigm;
    private Adjustment adjustment;
    private boolean isSequencePause;
    private WarningFragmentListener listener;

    /**
     *
     * @param paradigm
     * @param adjustment Adjustment must only be passed for sequence pause, otherwise it has to be null signaling paradigm pause.
     * @return
     */
    public static WarningFragment newInstance(@NonNull Paradigm paradigm, @Nullable Adjustment adjustment) {
        WarningFragment warningFragment = new WarningFragment();
        Bundle bundle = bundleArguments(paradigm, adjustment);
        warningFragment.setArguments(bundle);
        return warningFragment;
    }

    @NonNull
    public static Bundle bundleArguments(@NonNull Paradigm paradigm, @Nullable Adjustment adjustment) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PARADIGM, paradigm.toString());
        if (adjustment != null) {
            bundle.putString(KEY_DIFFICULTY_STATE, adjustment.toString());
        }
        return bundle;
    }

    public WarningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.warning_fragment, container, false);

        currentParadigm = Paradigm.valueOf(getArguments().getString(KEY_PARADIGM));
        if (getArguments().containsKey(KEY_DIFFICULTY_STATE)) {
            adjustment = Adjustment.valueOf(getArguments().getString(KEY_DIFFICULTY_STATE));
            isSequencePause = true;
        } else {
            isSequencePause = false;
        }

        if (isFirstParadigm() && !isSequencePause) {
            binding.warningFragmentGoBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.goBack();
                }
            });
            binding.warningFragmentStartTrainingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.startTraining();
                }
            });
        } else {
            TrainingUtils.setViewsVisible(false,
                    binding.warningFragmentWarning,
                    binding.warningFragmentGoBackBtn,
                    binding.warningFragmentStartTrainingBtn);
        }

        binding.warningFragmentExplanation.setText(Html.fromHtml(getString(getHelpTextForParadigm())));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WarningFragmentListener) {
            listener = (WarningFragmentListener) context;
        } else {
            throw new ClassCastException("Activity must implement WarningFragmentListener interface.");
        }
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

    private boolean isFirstParadigm() {
        return TrainingApp.indexOfParadigm(currentParadigm) == 0;
    }

    public interface WarningFragmentListener {

        void startTraining();

        void goBack();
    }
}
