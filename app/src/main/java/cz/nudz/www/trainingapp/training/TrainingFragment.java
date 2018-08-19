package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.tables.Trial;
import cz.nudz.www.trainingapp.databinding.TrainingFragmentBinding;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.enums.Side;
import cz.nudz.www.trainingapp.utils.RandomUtils;
import cz.nudz.www.trainingapp.utils.Utils;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static cz.nudz.www.trainingapp.enums.Side.LEFT;

public abstract class TrainingFragment extends Fragment {

    public static final String TAG = TrainingFragment.class.getSimpleName();

    private static final String KEY_DIFFICULTY = "KEY_DIFFICULTY";
    private static final String KEY_TRIAL_COUNT = "KEY_TRIAL_COUNT";
    private static final String KEY_PRESENTATION_TIME = "KEY_PRESENTATION_TIME";

    // Measure = milliseconds
    private static final int CUE_INTERVAL = 300;
    private static final int POST_CUE_PAUSE = 250;
    public static final int MEMORIZATION_INTERVAL = 250;
    private static final int RETENTION_INTERVAL = 900;
    private static final int TEST_INTERVAL = 10000;

    private static final int POST_TRIAL_PAUSE = 2000;
    private static final int LEFT_GRID_INDEX = 0;
    private static final int RIGHT_GRID_INDEX = 1;
    // Do not set to 0, unless you want to nullify all intervals
    private static final double DEBUG_SLOW = 1;
    private static final double SPEED_FACTOR = 1;

    private TrainingFragmentBinding binding;
    private ConstraintLayout[] grids;
    private TrainingFragmentListener listener;
    private final Handler handler = new Handler();

    private Difficulty difficulty;
    private Integer trialCount;
    private Integer presentationTime;
    private boolean isTrainingMode;
    private ParadigmType paradigmType;

    private List<Boolean> answers;
    private int gridSize;
    private int cellSize;
    private int halfStimCount;
    private int totalStimCount;
    private int paddingStart;
    private int stimSize;
    private TrialRunner trialRunner;

    public static TrainingFragment newInstance(@NonNull ParadigmType paradigmType,
                                               @NonNull Difficulty difficulty) {
        TrainingFragment fragment = ParadigmType.toTrainingFragment(paradigmType);
        Bundle args = new Bundle();
        args.putString(KEY_DIFFICULTY, difficulty.toString());
        args.putString(TrainingActivity.KEY_PARADIGM, paradigmType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Only use in trial mode.
     *
     * @param paradigmType
     * @param difficulty
     * @param trialCount
     * @return
     */
    public static TrainingFragment newInstance(@NonNull ParadigmType paradigmType,
                                               @NonNull Difficulty difficulty,
                                               int trialCount) {
        TrainingFragment fragment = TrainingFragment.newInstance(paradigmType, difficulty);
        fragment.getArguments().putInt(KEY_TRIAL_COUNT, trialCount);
        return fragment;
    }

    /**
     * Only use in trial mode.
     *
     * @param paradigmType
     * @param difficulty
     * @param trialCount
     * @param presentationTime
     * @return
     */
    public static TrainingFragment newInstance(@NonNull ParadigmType paradigmType,
                                               @NonNull Difficulty difficulty,
                                               int trialCount,
                                               int presentationTime) {
        TrainingFragment fragment = TrainingFragment.newInstance(paradigmType, difficulty, trialCount);
        fragment.getArguments().putInt(KEY_PRESENTATION_TIME, presentationTime);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // lock screen orientation to landscape for trials
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (context instanceof TrainingFragmentListener) {
            listener = (TrainingFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        unlockScreen();
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        removePendingCallbacks();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment parentFragment = getParentFragment();
        if (null != parentFragment) {
            onAttachToParentFragment(parentFragment);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.training_fragment, container, false);

        trialCount = TrainingActivity.DEFAULT_TRIAL_COUNT;
        answers = new ArrayList<>(trialCount);
        grids = new ConstraintLayout[]{binding.trainingFragmentLeftGrid, binding.trainingFragmentRightGrid};
        difficulty = Difficulty.valueOf(getArguments().getString(KEY_DIFFICULTY));
        paradigmType = ParadigmType.valueOf(getArguments().getString(TrainingActivity.KEY_PARADIGM));
        isTrainingMode = true;
        if (getArguments().containsKey(KEY_TRIAL_COUNT)) {
            trialCount = getArguments().getInt(KEY_TRIAL_COUNT);
            presentationTime = getArguments().getInt(KEY_PRESENTATION_TIME);
            isTrainingMode = false;
        }

        return binding.getRoot();
    }

    /**
     * If there is a parent fragment in between this one and host activity, let it handle events.
     * @param fragment
     */
    protected void onAttachToParentFragment(Fragment fragment) {
        if (fragment instanceof TrainingFragmentListener) {
            listener = (TrainingFragmentListener) fragment;
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        // This hack is required because this fragment is reused in a viewpager which pre-creates fragments..
        // for smooth swiping, meaning that we would start count down before the fragment is visible.
        if (visible && isResumed()) {
            onResume();
        }
        // pager next/previous view
        if (!visible && getParentFragment() != null) {
            removePendingCallbacks();
            unlockScreen();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        /*
         * Using this callback as an indicator that layout has finished...
         * meaning that we can use views' measures, etc. at this point.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // prevent infinite event loop
                binding.trainingFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Adjustment may change on per-sequence basis
                totalStimCount = Utils.getStimCount(difficulty);
                halfStimCount = totalStimCount / 2;

                // Grids on both sides are identical so use whatever.
                // They aren't true squares so take the minimum dimension as grid size.
                gridSize = Math.min(
                        binding.trainingFragmentLeftGrid.getWidth(),
                        binding.trainingFragmentLeftGrid.getHeight());

                cellSize = Utils.optimalContainingSquareSize(gridSize, gridSize, halfStimCount);
                // each cell has enough space for 4 actual stimuli; this excess is for 'pseudo-randomness'..
                // simulated with padding inside the cell.
                paddingStart = halfStimCount <= 4 ? TrainingActivity.DEFAULT_TRIAL_COUNT : TrainingActivity.DEFAULT_TRIAL_COUNT / 2;
                stimSize = (cellSize / 2) - paddingStart;

                trialRunner = new TrialRunner();
                // give user half second to focus
                handler.postDelayed(trialRunner, 500);
            }
        });
    }

    private class TrialRunner implements Runnable {

        // recursive loop counter
        private int i = 0;
        private Trial currentTrial;
        private Date responseStartTime;
        private View[] views;

        TrialRunner() {
            binding.trainingFragmentSameBtn.setOnClickListener((v) -> {
                handleAnswerSubmission(!currentTrial.isChangingTrial());
                removePendingCallbacks();
                queueNextTrial();
            });
            binding.trainingFragmentSameBtn.setOnLongClickListener(view -> {
                view.callOnClick();
                return true;
            });

            binding.trainingFragmentDifferentBtn.setOnClickListener((v) -> {
                handleAnswerSubmission(currentTrial.isChangingTrial());
                removePendingCallbacks();
                queueNextTrial();
            });
            binding.trainingFragmentDifferentBtn.setOnLongClickListener(view -> {
                view.callOnClick();
                return true;
            });
        }

        @Override
        public void run() {
            if (i < trialCount) {
                executeTrial();
            } else if (listener != null) {
                listener.onSequenceFinished(answers);
                answers.clear();
            }
        }

        private void executeTrial() {
            disableAnswerBtns();
            // workaround for stuck pressed button state
            refreshAnswerBtns();

            // clear grids before new trial starts
            for (ViewGroup grid : grids) grid.removeAllViews();

            final List<List<ImageView>> stimuli = setupGridViews();
            // merge stimuli from both grids
            final List<ImageView> allStimuli = new ArrayList<>(stimuli.get(LEFT_GRID_INDEX));
            allStimuli.addAll(stimuli.get(RIGHT_GRID_INDEX));

            // Once the last view's layout is finished we can start setting up stimuli
            final ImageView lastAddedStim = allStimuli.get(allStimuli.size() - 1);
            lastAddedStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // prevent event loop
                    lastAddedStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    initStimuli(allStimuli);
                    initTrialObject();

                    // START THE TRIAL...
                    final View cue;
                    switch (currentTrial.getCuedSide()) {
                        case LEFT:
                            cue = binding.trainingFragmentCueLeft;
                            break;
                        case RIGHT:
                            cue = binding.trainingFragmentCueRight;
                            break;
                        default:
                            throw new IllegalStateException(String.format(
                                    "Trials's cueSide state is invalid: {0}",
                                    currentTrial.getCuedSide().toString()));
                    }
                    cue.setVisibility(VISIBLE);

                    // CUE PAUSE
                    handler.postDelayed(() -> {
                        cue.setVisibility(View.INVISIBLE);

                        // MEMORY ARRAY
                        handler.postDelayed(() -> {
                            views = allStimuli.toArray(new View[allStimuli.size()]);
                            Utils.setViewsVisibility(VISIBLE, views);

                            // RETENTION INTERVAL
                            handler.postDelayed(() -> {
                                Utils.setViewsVisibility(INVISIBLE, views);

                                if (currentTrial.isChangingTrial()) {
                                    // pick random stim in cued grid
                                    int index = RandomUtils.nextIntExclusive(0, stimuli.get(0).size());

                                    final ImageView changingStim = currentTrial.getCuedSide() == LEFT
                                            ? stimuli.get(LEFT_GRID_INDEX).get(index)
                                            : stimuli.get(RIGHT_GRID_INDEX).get(index);

                                    performChange(changingStim);
                                    try {
                                        final JSONObject jsonStimulus = getJSONStimulus(changingStim);
                                        currentTrial.setChangedStimulus(jsonStimulus);
                                    } catch (JSONException e) {
                                        Log.e(TrainingFragment.TAG, e.getMessage());
                                    }
                                }

                                // TEST ARRAY
                                handler.postDelayed(() -> {
                                    responseStartTime = new Date();
                                    Utils.setViewsVisibility(VISIBLE, views);
                                    enableAnswerBtns();
                                    // TRIAL END
                                    handler.postDelayed(() -> {
                                        // START NEXT TRIAL
                                        queueNextTrial();

                                    }, (int) (TEST_INTERVAL * DEBUG_SLOW * (SPEED_FACTOR / 2)));

                                }, (int) (RETENTION_INTERVAL * DEBUG_SLOW));

                            }, (int) ((presentationTime != null
                                    ? presentationTime
                                    : MEMORIZATION_INTERVAL) * DEBUG_SLOW * SPEED_FACTOR));

                        }, (int) (CUE_INTERVAL * DEBUG_SLOW * SPEED_FACTOR));

                    }, (int) (POST_CUE_PAUSE * DEBUG_SLOW));
                }
            });
        }

        private void queueNextTrial() {
            Utils.setViewsVisibility(INVISIBLE, views);

            // insert null answer if user did not answer this trial
            if (answers.size() == i) handleAnswerSubmission(null);

            if (listener != null) listener.onTrialFinished(i);
            i += 1;

            handler.postDelayed(TrialRunner.this,
                (int) (POST_TRIAL_PAUSE * DEBUG_SLOW));
        }

        private void initTrialObject() {
            currentTrial = new Trial();
            currentTrial.setCuedSide(RandomUtils.nextGaussianBool() ? Side.LEFT : Side.RIGHT);
            currentTrial.setChangingTrial(RandomUtils.nextGaussianBool());
            try {
                final JSONArray jsonArray = new JSONArray();
                for (ViewGroup grid : grids) {
                    final JSONArray gridArray = new JSONArray();
                    for (int i = 0; i < grid.getChildCount(); ++i) {
                        final ImageView stim = (ImageView) ((FrameLayout) grid.getChildAt(i)).getChildAt(0);
                        // get the global (relative to window) positions
                        final JSONObject stimJSON = getJSONStimulus(stim);
                        gridArray.put(stimJSON);
                    }
                    jsonArray.put(gridArray);
                }
                currentTrial.setStimuli(jsonArray);
            } catch (JSONException e) {
                Log.e(TrainingFragment.TAG, e.getMessage());
            }
        }

        @NonNull
        private JSONObject getJSONStimulus(ImageView stim) throws JSONException {
            final JSONObject stimJSON = new JSONObject();
            stimJSON.put("id", stim.getId());

            Rect rect = new Rect();
            stim.getGlobalVisibleRect(rect);
            stimJSON.put("width", rect.width());
            stimJSON.put("height", rect.height());
            stimJSON.put("left", rect.left);
            stimJSON.put("top", rect.top);
            stimJSON.put("right", rect.right);
            stimJSON.put("bottom", rect.bottom);

            // we stored (during init step) color and shape's name in stim view's tag
            final Pair<Integer, String> colorShapePair = (Pair<Integer, String>) stim.getTag();
            stimJSON.put("color", new JSONArray(Arrays.asList(
                    Color.red(colorShapePair.first),
                    Color.green(colorShapePair.first),
                    Color.blue(colorShapePair.first)
            )));
            stimJSON.put("rotation", stim.getRotation());
            stimJSON.put("shape", colorShapePair.second);
            return stimJSON;
        }

        private void handleAnswerSubmission(Boolean answer) {
            // allow only one answer per trial
            disableAnswerBtns();

            answers.add(answer);

            if (isTrainingMode) {
                // response time in millis
                long trialResponseTime = (new Date()).getTime() - responseStartTime.getTime();
                // TODO: remove this hardcoded dependency
                TrainingActivity parentActivity = (TrainingActivity) getActivity();
                RuntimeExceptionDao<Trial, Integer> trialDao = parentActivity.getDbHelper().getTrialDao();
                currentTrial.setSequence(parentActivity.getCurrentSequence());
                currentTrial.setCorrect(answer);
                currentTrial.setResponseTimeMillis(answer != null ? trialResponseTime : null);
                trialDao.create(currentTrial);
            }
        }

        @NonNull
        private List<List<ImageView>> setupGridViews() {
            // Two sets of stimuli corresponding to two grids (left and right)
            final List<List<ImageView>> stimuli = new ArrayList<>(2);
            stimuli.add(new ArrayList<>(halfStimCount));
            stimuli.add(new ArrayList<>(halfStimCount));

            final List<Rect> basePositions = generateGridPositions(gridSize, cellSize);
            if (basePositions.size() < halfStimCount)
                throw new IllegalStateException("The grid is too small for the number of stimuli.");

            // Prepare the grid cells..
            for (int i = 0; i < 2; ++i) {
                ConstraintLayout grid = grids[i];
                // this is not i deep copy! it is only for shuffling on different grids
                final List<Rect> gridPositions = new ArrayList<>(basePositions);
                Collections.shuffle(gridPositions);

                for (int j = 0; j < halfStimCount; ++j) {
                    ImageView v = createStimView(getActivity());
                    stimuli.get(i).add(v);

                    FrameLayout container = new FrameLayout(getActivity());
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(stimSize, stimSize);

                    int paddingEnd = cellSize - stimSize - paddingStart;
                    params.topMargin = RandomUtils.nextIntInclusive(paddingStart, paddingEnd);
                    params.leftMargin = RandomUtils.nextIntInclusive(paddingStart, paddingEnd);
                    v.setLayoutParams(params);
                    container.addView(v);

                    addViewToGrid(container, grid, gridPositions.get(j), cellSize);
                }
            }
            return stimuli;
        }

        private void disableAnswerBtns() {
            binding.trainingFragmentDifferentBtn.setClickable(false);
            binding.trainingFragmentSameBtn.setClickable(false);
            binding.trainingFragmentDifferentBtn.setLongClickable(false);
            binding.trainingFragmentSameBtn.setLongClickable(false);
        }

        private void refreshAnswerBtns() {
            binding.trainingFragmentSameBtn.cancelLongPress();
            binding.trainingFragmentDifferentBtn.cancelLongPress();
            binding.trainingFragmentSameBtn.clearAnimation();
            binding.trainingFragmentDifferentBtn.clearAnimation();
            Utils.setViewsVisibility(INVISIBLE,
                    binding.trainingFragmentDifferentBtn, binding.trainingFragmentSameBtn);
            Utils.setViewsVisibility(VISIBLE,
                    binding.trainingFragmentDifferentBtn, binding.trainingFragmentSameBtn);
        }

        private void enableAnswerBtns() {
            binding.trainingFragmentDifferentBtn.setClickable(true);
            binding.trainingFragmentSameBtn.setClickable(true);
            binding.trainingFragmentDifferentBtn.setLongClickable(true);
            binding.trainingFragmentSameBtn.setLongClickable(true);
        }
    }

    protected abstract void initStimuli(List<ImageView> stimuli);

    protected abstract void performChange(ImageView changingStim);

    private void addViewToGrid(View v, ConstraintLayout grid, Rect position, int viewSize) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(viewSize, viewSize);
        if (grid.getId() == binding.trainingFragmentLeftGrid.getId()) {
            params.setMargins(position.left, position.top, position.right, position.bottom);
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            // reverse margins for right grid
            params.setMargins(position.right, position.top, position.left, position.bottom);
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        }
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

        v.setLayoutParams(params);
        grid.addView(v);
    }

    @NonNull
    public static ImageView createStimView(Context context) {
        ImageView v = new ImageView(context);
        v.setVisibility(View.INVISIBLE);
        // we need to set view's id to later find it in the layout..
        v.setId(View.generateViewId());
        return v;
    }

    public static List<Rect> generateGridPositions(int gridSize, int cellSize) {
        List<Rect> positions = new ArrayList<>();
        int col = 0;
        while (col * cellSize + cellSize <= gridSize) {
            int row = 0;
            while (row * cellSize + cellSize <= gridSize) {
                positions.add(new Rect(
                        col * cellSize,
                        row * cellSize,
                        col * cellSize + cellSize,
                        row * cellSize + cellSize));
                ++row;
            }
            ++col;
        }
        return positions;
    }

    /**
     *  Unlock screen orientation once trials/tutorials are finished (when it is not training)
     */
    private void unlockScreen() {
        // TODO: FIXME, this makes the fragment enter infinite loop of pause/resume
//        if (!(listener instanceof TrainingActivity)) {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//        }
    }

    public void removePendingCallbacks() {
        this.handler.removeCallbacksAndMessages(null);
    }

    public interface TrainingFragmentListener {

        void onSequenceFinished(List<Boolean> answers);

        default void onTrialFinished(int count) {
            // do nothing
        }
    }
}
