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

import static cz.nudz.www.trainingapp.enums.Side.LEFT;

public abstract class TrainingFragment extends Fragment {

    public static final String TAG = TrainingFragment.class.getSimpleName();

    private static final String KEY_DIFFICULTY = "KEY_DIFFICULTY";
    private static final String KEY_TRIAL_COUNT = "KEY_TRIAL_COUNT";

    // Measure = milliseconds
    private static final int CUE_INTERVAL = 300;
    private static final int POST_CUE_PAUSE = 100;
    private static final int MEMORIZATION_INTERVAL = 100;
    private static final int RETENTION_INTERVAL = 900;
    private static final int TEST_INTERVAL = 2000;

    private static final int POST_TRIAL_PAUSE = 2000;
    private static final int LEFT_GRID_INDEX = 0;
    private static final int RIGHT_GRID_INDEX = 1;
    // Do not set to 0, unless you want to nullify other intervals..
    private static final double DEBUG_SLOW = 1;
    private static final double SPEED_FACTOR = 1;

    private TrainingFragmentBinding binding;
    private ConstraintLayout[] grids;
    private TrainingFragmentListener listener;
    private final Handler handler = new Handler();

    private Difficulty difficulty;
    private int trialCount;
    private List<Boolean> answers;
    private int gridSize;
    private int cellSize;
    private int halfStimCount;
    private int totalStimCount;
    private int paddingStart;
    private int stimSize;
    private TrialRunner trialRunner;
    private boolean isTrainingMode;
    private ParadigmType paradigmType;

    public static TrainingFragment newInstance(@NonNull ParadigmType paradigmType, @NonNull Difficulty difficulty) {
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
    public static TrainingFragment newInstance(@NonNull ParadigmType paradigmType, @NonNull Difficulty difficulty, int trialCount) {
        TrainingFragment fragment = TrainingFragment.newInstance(paradigmType, difficulty);
        fragment.getArguments().putInt(KEY_TRIAL_COUNT, trialCount);
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
        // unlock screen orientation once trials/tutorials are finished (when it is not training)
        if (!(listener instanceof TrainingActivity)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        super.onDetach();
    }

    public void removePendingCallbacks() {
        this.handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        removePendingCallbacks();
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
            isTrainingMode = false;
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

        return binding.getRoot();
    }

    private class TrialRunner implements Runnable {

        // recursive loop counter
        private int i = 0;
        private Trial currentTrial;
        private Date responseStartTime;

        @Override
        public void run() {
            if (i < trialCount) {
                executeTrial();
            } else if (listener != null) {
                listener.onSequenceFinished(answers);
            }
        }

        private void executeTrial() {
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

                    Utils.enableViews(false, binding.trainingFragmentDifferentBtn, binding.trainingFragmentSameBtn);
                    // user answer handlers have to be set trial-wise
                    binding.trainingFragmentSameBtn.setOnClickListener(v ->
                            handleAnswerSubmission(!currentTrial.isChangingTrial()));
                    binding.trainingFragmentDifferentBtn.setOnClickListener(v ->
                            handleAnswerSubmission(currentTrial.isChangingTrial()));

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
                                    "Trials's cueSide state is invalid: {0}", currentTrial.getCuedSide().toString()));
                    }
                    cue.setVisibility(View.VISIBLE);

                    // CUE PAUSE
                    handler.postDelayed(() -> {
                        cue.setVisibility(View.INVISIBLE);

                        // MEMORY ARRAY
                        handler.postDelayed(() -> {
                            final View[] views = allStimuli.toArray(new View[allStimuli.size()]);
                            Utils.setViewsVisible(true, views);

                            // RETENTION INTERVAL
                            handler.postDelayed(() -> {
                                Utils.setViewsVisible(false, views);

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
                                    Utils.setViewsVisible(true, views);
                                    Utils.enableViews(true, binding.trainingFragmentDifferentBtn, binding.trainingFragmentSameBtn);

                                    // TRIAL END
                                    handler.postDelayed(() -> {
                                        Utils.setViewsVisible(false, views);

                                        // insert null answer if user did not answer this trial
                                        if (answers.size() == i)
                                            handleAnswerSubmission(null);

                                        // START NEXT TRIAL
                                        handler.postDelayed(
                                                TrialRunner.this,
                                                (int) (POST_TRIAL_PAUSE * DEBUG_SLOW));

                                        if (listener != null) listener.onTrialFinished(i);
                                        i += 1;

                                    }, (int) (TEST_INTERVAL * DEBUG_SLOW * (SPEED_FACTOR / 2)));

                                }, (int) (RETENTION_INTERVAL * DEBUG_SLOW));

                            }, (int) (MEMORIZATION_INTERVAL * DEBUG_SLOW * SPEED_FACTOR));

                        }, (int) (CUE_INTERVAL * DEBUG_SLOW * SPEED_FACTOR));

                    }, (int) (POST_CUE_PAUSE * DEBUG_SLOW));
                }
            });
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

    public interface TrainingFragmentListener {

        void onSequenceFinished(List<Boolean> answers);

        default void onTrialFinished(int trialCount) {
            // do nothing
        }
    }
}
