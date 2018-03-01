package cz.nudz.www.trainingapp.training;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.MessageFragmentBinding;
import cz.nudz.www.trainingapp.enums.Adjustment;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.tutorial.TutorialFragmentFactory;
import cz.nudz.www.trainingapp.utils.Utils;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    public static final String KEY_ADJUSTMENT = "KEY_ADJUSTMENT";
    public static final String TAG = MessageFragment.class.getSimpleName();

    private MessageFragmentBinding binding;
    private ParadigmType currentParadigmType;
    private Adjustment adjustment;
    private boolean isSequencePause;
    private MessageFragmentListener listener;

    /**
     *
     * @param paradigmType
     * @param adjustment Adjustment must only be passed for sequence pause, otherwise it has to be null signaling paradigm pause.
     * @return
     */
    public static MessageFragment newInstance(@NonNull ParadigmType paradigmType, @Nullable Adjustment adjustment) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = bundleArguments(paradigmType, adjustment);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @NonNull
    public static Bundle bundleArguments(@NonNull ParadigmType paradigmType, @Nullable Adjustment adjustment) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PARADIGM, paradigmType.toString());
        if (adjustment != null) {
            bundle.putString(KEY_ADJUSTMENT, adjustment.toString());
        }
        return bundle;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.message_fragment, container, false);

        currentParadigmType = ParadigmType.valueOf(getArguments().getString(KEY_PARADIGM));
        if (getArguments().containsKey(KEY_ADJUSTMENT)) {
            adjustment = Adjustment.valueOf(getArguments().getString(KEY_ADJUSTMENT));
            isSequencePause = true;
        } else {
            isSequencePause = false;
        }

        if (isFirstParadigm() && !isSequencePause) {
            binding.warningFragmentStartTrainingBtn.setOnClickListener(v -> listener.startTraining());
        } else {
            // adjust view positioning using the guide
            final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.guideline.getLayoutParams();
            params.guidePercent = 0.5F;
            binding.guideline.setLayoutParams(params);
            Utils.setViewsVisibility(GONE,
                    binding.warningFragmentWarning,
                    binding.warningFragmentStartTrainingBtn);
        }

        binding.warningFragmentExplanation.setText(Html.fromHtml(getString(getHelpTextForParadigm())));

        binding.paradigmIcon.setImageDrawable(getResources().getDrawable(
                TutorialFragmentFactory.getIconByParadigm(currentParadigmType)));

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // callback is only required on the main screen (start btn is hidden during pause)
        if (context instanceof MessageFragmentListener) {
            listener = (MessageFragmentListener) context;
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
            switch (currentParadigmType) {
                case COLOR:
                    return R.string.startHelpColorParadigm;
                case SHAPE:
                    return R.string.startHelpShapeParadigm;
                case POSITION:
                    return R.string.startHelpPositionParadigm;
            }
        }
        return R.string.errorGenericMessage;
    }

    private boolean isFirstParadigm() {
        return ParadigmSet.indexOf(currentParadigmType) == 0;
    }

    public interface MessageFragmentListener {

        void startTraining();
    }
}
