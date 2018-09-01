package cz.nudz.www.trainingapp.training;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.MessageFragmentBinding;
import cz.nudz.www.trainingapp.enums.Adjustment;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.tutorial.TutorialFragmentFactory;
import cz.nudz.www.trainingapp.utils.Utils;

import static android.view.View.VISIBLE;
import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    public static final String KEY_ADJUSTMENT = "KEY_ADJUSTMENT";
    public static final String KEY_SEQ_COUNT = "KEY_SEQUENCE_COUNT";
    public static final String TAG = MessageFragment.class.getSimpleName();

    private MessageFragmentBinding binding;
    private ParadigmType paradigmType;
    private Adjustment adjustment;
    private MessageFragmentListener listener;
    private BaseActivity parent;
    private int currSeqCount;

    public static MessageFragment newInstance(@NonNull ParadigmType paradigmType) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = bundleArguments(paradigmType, null, null);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    /**
     *
     * @param paradigmType
     * @param adjustment Adjustment must only be passed for sequence pause, otherwise it has to be null signaling paradigm pause.
     * @param sequenceCount
     * @return
     */
    public static MessageFragment newInstance(@NonNull ParadigmType paradigmType,
                                              @NonNull Adjustment adjustment,
                                              int sequenceCount) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = bundleArguments(paradigmType, adjustment, sequenceCount);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @NonNull
    public static Bundle bundleArguments(@NonNull ParadigmType paradigmType,
                                         @Nullable Adjustment adjustment,
                                         @Nullable Integer sequenceCount) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PARADIGM, paradigmType.toString());
        if (adjustment != null) {
            bundle.putString(KEY_ADJUSTMENT, adjustment.toString());
        }
        if (sequenceCount != null) {
            bundle.putInt(KEY_SEQ_COUNT, sequenceCount);
        }
        return bundle;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.message_fragment, container, false);
        parent = (BaseActivity) getActivity();

        if (ParadigmSet.getOperationMode() == ParadigmSet.OperationMode.TRAINING) {
            binding.warningFragmentWarning.setText(R.string.trainingWarningText);
            binding.warningFragmentStartBtn.setText(R.string.startTrainingBtnText);
        } else {
            binding.warningFragmentWarning.setText(R.string.testWarningText);
            binding.warningFragmentStartBtn.setText(R.string.startTestBtnText);
        }

        paradigmType = ParadigmType.valueOf(getArguments().getString(KEY_PARADIGM));
        if (getArguments().containsKey(KEY_ADJUSTMENT)) {
            adjustment = Adjustment.valueOf(getArguments().getString(KEY_ADJUSTMENT));
            currSeqCount = getArguments().getInt(KEY_SEQ_COUNT);
            if (currSeqCount == 0) {
                throw error("sequence count cannot be 0.");
            }
            initSequencePause();
        } else {
            initParadigmPause();
        }

        return binding.getRoot();
    }

    private void initSequencePause() {
        adjustGuide();
        binding.warningFragmentExplanation.setText(Html.fromHtml(getString(getHelpTextForSequencePause())));
        Utils.setViewsVisibility(VISIBLE,
                binding.progressGrid,
                binding.progressTitle);
        fillProgressGrid();
    }

    private void initParadigmPause() {
        if (isFirstParadigm()) {
            binding.warningFragmentStartTrainingBtn.setOnClickListener(v -> listener.startTraining());
            Utils.setViewsVisibility(VISIBLE,
                    binding.warningFragmentWarning,
                    binding.warningFragmentStartTrainingBtn);
        } else {
            adjustGuide();
        }

        binding.warningFragmentExplanation.setText(Html.fromHtml(getString(getHelpTextForParadigmPause())));
        binding.paradigmIcon.setVisibility(VISIBLE);
        binding.paradigmIcon.setImageDrawable(getResources().getDrawable(
                TutorialFragmentFactory.getIconByParadigm(paradigmType)));
    }

    private void adjustGuide() {
        // adjust view positioning using the guide
        final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.guideline.getLayoutParams();
        params.guidePercent = 0.5F;
        binding.guideline.setLayoutParams(params);
    }

    private void fillProgressGrid() {
        for (int i = 1; i <= TrainingActivity.DEFAULT_SEQUENCE_COUNT; ++i) {
            final ImageView cb = createCheckBox(i);
            binding.progressGrid.addView(cb);
        }
    }

    private ImageView createCheckBox(int position) {
        final ImageView iv = new ImageView(getActivity());
        Drawable drawable;
        final boolean checked = position <= currSeqCount;
        final boolean current = position - 1 == currSeqCount;
        if (checked) {
            drawable = getResources().getDrawable(R.drawable.progress_check_box_done);
            iv.setColorFilter(getResources().getColor(android.R.color.holo_green_light),
                    PorterDuff.Mode.SRC_IN);
        } else if (current) {
            drawable = getResources().getDrawable(R.drawable.progress_check_box_indeterminate);
            iv.setColorFilter(getResources().getColor(android.R.color.holo_blue_light),
                    PorterDuff.Mode.SRC_IN);
        } else {
            drawable = getResources().getDrawable(R.drawable.progress_check_box_todo);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
        iv.setLayoutParams(params);

        iv.setImageDrawable(drawable);

        return iv;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // callback is only required on the main screen (start btn is hidden during pause)
        if (context instanceof MessageFragmentListener) {
            listener = (MessageFragmentListener) context;
        }
    }

    private int getHelpTextForParadigmPause() {
        switch (paradigmType) {
            case COLOR:
                return R.string.startHelpColorParadigm;
            case SHAPE:
                return R.string.startHelpShapeParadigm;
            case POSITION:
                return R.string.startHelpPositionParadigm;
        }
        throw error("current paradigm type is null.");
    }

    private int getHelpTextForSequencePause() {
        switch (adjustment) {
            case LOWERED:
                return R.string.difficultyLoweredMessage;
            case SAME:
                return R.string.difficultySameMessage;
            case RAISED:
                return R.string.difficultyRaisedMessage;
        }
        throw error("adjustment is not set.");
    }

    private boolean isFirstParadigm() {
        return ParadigmSet.indexOf(paradigmType) == 0;
    }

    private IllegalStateException error(final String message) throws IllegalStateException {
        return new IllegalStateException(String.format("%s: %s", TAG, message));
    }

    public interface MessageFragmentListener {

        void startTraining();
    }
}
