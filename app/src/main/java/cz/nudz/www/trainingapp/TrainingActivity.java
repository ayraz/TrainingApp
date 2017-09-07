package cz.nudz.www.trainingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.RandomUtils;

import static cz.nudz.www.trainingapp.Side.LEFT;

public abstract class TrainingActivity extends AppCompatActivity {

    public static final String TAG = TrainingActivity.class.getSimpleName();

    // Measure = milliseconds
    private static final int CUE_INTERVAL = 300;
    private static final int POST_CUE_PAUSE = 100;
    private static final int MEMORIZATION_INTERVAL = 100;
    private static final int RETENTION_INTERVAL = 900;
    private static final int TEST_INTERVAL = 2000;
    private static final int POST_TRIAL_PAUSE = 2000;

    private static final int TRIAL_COUNT = 20;
    private static final int SEQUENCE_COUNT = 7;
    private static final int LEFT_INDEX = 0;
    private static final int RIGHT_INDEX = 1;
    // Do not set to 0, unless you want to nullify other intervals..
    private static final int DEBUG_MODIFIER = 0;

    private TrainingActivityBinding binding;
    private ConstraintLayout[] grids;

    private int difficulty;
    // Unanswered trials are NULL
    private List<Boolean> answers = new ArrayList<>(TRIAL_COUNT);;
    private final SequenceRunner sequenceRunner = new SequenceRunner();;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        grids = new ConstraintLayout[]{binding.trainingActivityLeftGrid, binding.trainingActivityRightGrid};

        // TODO: Each session starts with lowest diff.
        difficulty = 4;

        /*
         * Using this callback as an indicator that layout has finished...
         * meaning that we can use views' measures, etc. at this point.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // prevent infinite event loop
                binding.trainingActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // START PARADIGM
                sequenceRunner.run();
            }
        });
    }

    private class SequenceRunner implements Runnable {

        // recursive loop counter
        private int seqCount = 0;

        @Override
        public void run() {
            if (seqCount < SEQUENCE_COUNT) {
                // TODO remove after debug
                binding.seqCount.setText(String.format("Seq. #: %s", Integer.toString(seqCount + 1)));

                final int totalStimCount = getStimCount(difficulty);
                final int perGridStimCount = totalStimCount / 2;

                // Grids on both sides are identical so use whatever.
                // They aren't true squares so take the minimum dimension as grid size.
                final int gridSize = Math.min(binding.trainingActivityLeftGrid.getWidth(), binding.trainingActivityLeftGrid.getHeight());
                final int cellSize = optimalContainingSquareSize(gridSize, gridSize, perGridStimCount);

                // Two sets of stimuli corresponding to two grids (left and right)
                final List<List<ImageView>> stimuli = new ArrayList<List<ImageView>>(2);
                stimuli.add(new ArrayList<ImageView>(perGridStimCount));
                stimuli.add(new ArrayList<ImageView>(perGridStimCount));

                // each cell can contain 4 actual stimuli; this excess space is for 'pseudo-randomness'..
                // simulated with padding inside the cell.
                final int paddingStart = perGridStimCount <= 4 ? 20 : 10;
                final int stimSize = (cellSize / 2) - paddingStart;

                final List<Rect> basePositions = generateGridPositions(gridSize, cellSize);
                if (basePositions.size() < perGridStimCount)
                    throw new IllegalStateException("The grid is too small for the number of stimuli.");

                // Prepare the grid cells..
                for (int i = 0; i < 2; ++i) {
                    ConstraintLayout grid = grids[i];
                    // this is not i deep copy! it is only for shuffling on different grids
                    final List<Rect> gridPositions = new ArrayList<Rect>(basePositions);
                    Collections.shuffle(gridPositions);

                    for (int j = 0; j < perGridStimCount; ++j) {
                        ImageView v = createStimView();
                        stimuli.get(i).add(v);

                        FrameLayout container = new FrameLayout(TrainingActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(stimSize, stimSize);

                        int paddingEnd = cellSize - stimSize - paddingStart;
                        params.topMargin = RandomUtils.nextIntInclusive(paddingStart, paddingEnd);
                        params.leftMargin = RandomUtils.nextIntInclusive(paddingStart, paddingEnd);
                        v.setLayoutParams(params);
                        container.addView(v);

                        addViewToGrid(container, grid, gridPositions.get(j), cellSize);
                    }
                }

                // Once the last view's layout is finished we can start setting up stimuli
                final ImageView lastAddedStim = stimuli.get(0).get(perGridStimCount - 1);
                lastAddedStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // prevent event loop
                        lastAddedStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // START SEQUENCE
                        TrialRunner trialRunner = new TrialRunner(stimuli);
                        trialRunner.run();

                        seqCount += 1;
                    }
                });
            } else {
                Intent intent = new Intent(getBaseContext(), ColorParadigmActivity.class);
                startActivity(intent);
            }
        }
    }

    private class TrialRunner implements Runnable {

        private final List<List<ImageView>> stimuli;
        private final List<ImageView> allStimuli;
        private final Handler handler;

        // recursive loop counter
        private int trialCount = 0;

        public TrialRunner(List<List<ImageView>> stimuli) {
            this.stimuli = stimuli;
            this.handler = new Handler(getMainLooper());

            // merge stimuli from both grids
            allStimuli = new ArrayList<ImageView>(stimuli.get(LEFT_INDEX));
            allStimuli.addAll(stimuli.get(RIGHT_INDEX));
        }

        @Override
        public void run() {
            if (trialCount < TRIAL_COUNT) {
                final Trial trial = new Trial(difficulty);

                // TODO remove after debug
                binding.trialCount.setText(String.format("Trial #: %s", Integer.toString(trialCount + 1)));

                // user answer handlers have to be set trial-wise
                binding.trainingActivitySameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answers.add(!trial.isChanging());
                        // allow only one answer
                        disableAnswerBtns();
                    }
                });

                binding.trainingActivityDifferentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answers.add(trial.isChanging());
                        disableAnswerBtns();
                    }
                });

                initStimuli(allStimuli);

                // START THE TRIAL...
                final View cue;
                switch (trial.getCueSide()) {
                    case LEFT:
                        cue = binding.trainingActivityCueLeft;
                        break;
                    case RIGHT:
                        cue = binding.trainingActivityCueRight;
                        break;
                    default:
                        throw new IllegalStateException(String.format("Trials's cueSide state is invalid: {0}", trial.getCueSide().toString()));
                }
                cue.setVisibility(View.VISIBLE);

                // CUE PAUSE
                handler.postDelayed(new Runnable() {
                    public void run() {
                        cue.setVisibility(View.INVISIBLE);

                        // MEMORY ARRAY
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final View[] views = allStimuli.toArray(new View[allStimuli.size()]);
                                setViewsVisible(true, views);

                                // RETENTION INTERVAL
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setViewsVisible(false, views);

                                        if (trial.isChanging()) {
                                            // pick random stim in cued grid
                                            int index = RandomUtils.nextIntExclusive(0, stimuli.get(0).size());

                                            final ImageView changingStim = trial.getCueSide() == LEFT
                                                    ? stimuli.get(LEFT_INDEX).get(index)
                                                    : stimuli.get(RIGHT_INDEX).get(index);

                                            performChange(changingStim);
                                        }

                                        // TEST ARRAY
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setViewsVisible(true, views);
                                                setViewsVisible(true,
                                                        binding.trainingActivitySameBtn,
                                                        binding.trainingActivityDifferentBtn);

                                                // TRIAL END
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        setViewsVisible(false, views);
                                                        disableAnswerBtns();

                                                        // insert null if user did not answer this trial
                                                        if (answers.size() == trialCount)
                                                            answers.add(null);

                                                        // START NEXT TRIAL
                                                        handler.postDelayed(
                                                                TrialRunner.this,
                                                                POST_TRIAL_PAUSE * DEBUG_MODIFIER);

                                                        trialCount += 1;
                                                    }
                                                }, TEST_INTERVAL * DEBUG_MODIFIER);

                                            }
                                        }, RETENTION_INTERVAL * DEBUG_MODIFIER);

                                    }
                                }, MEMORIZATION_INTERVAL * DEBUG_MODIFIER);

                            }
                        }, CUE_INTERVAL * DEBUG_MODIFIER);

                    }
                }, POST_CUE_PAUSE * DEBUG_MODIFIER);
            }
            // START NEXT SEQUENCE
            else {
                handler.post(sequenceRunner);
            }
        }

        private void disableAnswerBtns() {
            binding.trainingActivityDifferentBtn.setClickable(false);
            binding.trainingActivitySameBtn.setClickable(false);
        }
    }

    protected abstract void performChange(ImageView changingStim);

    protected abstract void initStimuli(List<ImageView> stimuli);


    @NonNull
    private ImageView createStimView() {
        ImageView v = new ImageView(TrainingActivity.this);
        v.setVisibility(View.INVISIBLE);
        // we need to set view's id to later find it in the layout..
        v.setId(View.generateViewId());
        return v;
    }

    private static void setViewsVisible(boolean visible, View... views) {
        for (View v : views) {
            v.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        }
    }

    private List<Rect> generateGridPositions(int gridSize, int cellSize) {
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

    private void addViewToGrid(View v, ConstraintLayout grid, Rect position, int viewSize) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(viewSize, viewSize);
        if (grid.getId() == binding.trainingActivityLeftGrid.getId()) {
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

    /**
     * See: https://math.stackexchange.com/questions/466198/algorithm-to-get-the-maximum-size-of-n-squares-that-fit-into-a-rectangle-with-a
     * @param x Width of containing grid.
     * @param y Height of containing grid.
     * @param n Number of rectangles to be contained within the grid.
     * @return Optimal containing square size.
     */
    private static int optimalContainingSquareSize(int x, int y, int n) {
        double sx, sy;

        double px = Math.ceil(Math.sqrt(n * x / y));
        if (Math.floor(px * y / x) * px < n)  // does not fit, y/(x/px)=px*y/x
            sx = y / Math.ceil(px * y / x);
        else
            sx = x / px;

        double py = Math.ceil(Math.sqrt(n * y / x));
        if (Math.floor(py * x / y) * py < n)  // does not fit
            sy = x / Math.ceil(x * py / y);
        else
            sy = y / py;

        return (int) Math.max(sx, sy);
    }

    /**
     * Calculates stimuli count based on sequence's difficulty.
     * @param difficulty
     * @return
     */
    private static int getStimCount(int difficulty) {
        return (1 + difficulty) * 2;
    }
}
