package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.RandomUtils;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

import static cz.nudz.www.trainingapp.training.Side.LEFT;
import static java.lang.Enum.valueOf;

public abstract class TrainingActivity extends AppCompatActivity implements CountDownFragment.OnCountDownListener {

    public static final String TAG = TrainingActivity.class.getSimpleName();
    public static final String KEY_PARADIGM = "KEY_PARADIGM";

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
    private static final int DEBUG_SLOW = 0;

    private TrainingActivityBinding binding;
    private ConstraintLayout[] grids;
    private final Handler handler = new Handler();

    private int difficulty;
    // Unanswered trials are NULL
    private List<Boolean> answers = new ArrayList<>(TRIAL_COUNT);;
    private Paradigm currentParadigm;
    private SequenceRunner sequenceRunner;

    public static void startActivity(Context context, Paradigm paradigm) {
        Intent intent = new Intent(context, Paradigm.toTrainingClass(paradigm));
        intent.putExtra(KEY_PARADIGM, paradigm.toString());
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Unregister all handler callbacks to prevent unwanted navigation.
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentParadigm = Paradigm.valueOf(getIntent().getStringExtra(KEY_PARADIGM));

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        grids = new ConstraintLayout[]{binding.trainingActivityLeftGrid, binding.trainingActivityRightGrid};

        // TODO: Each session/paradigm starts with lowest difficulty.
        difficulty = 4;

        /*
         * Using this onCountDownExpired as an indicator that layout has finished...
         * meaning that we can use views' measures, etc. at this point.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // prevent infinite event loop
                binding.trainingActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // START PARADIGM
                sequenceRunner = new SequenceRunner();
                sequenceRunner.run();
            }
        });
    }

    private class SequenceRunner implements Runnable {

        // Difficulty may change on per-sequence basis
        private final int totalStimCount = TrainingUtils.getStimCount(difficulty);
        private final int perGridStimCount = totalStimCount / 2;

        // Grids on both sides are identical so use whatever.
        // They aren't true squares so take the minimum dimension as grid size.
        private final int gridSize  = Math.min(
                binding.trainingActivityLeftGrid.getWidth(),
                binding.trainingActivityLeftGrid.getHeight());

        private final int cellSize = TrainingUtils.optimalContainingSquareSize(gridSize, gridSize, perGridStimCount);

        // recursive loop counter
        private int count = 0;

        public int getTotalStimCount() {
            return totalStimCount;
        }

        public int getPerGridStimCount() {
            return perGridStimCount;
        }

        public int getGridSize() {
            return gridSize;
        }

        public int getCellSize() {
            return cellSize;
        }

        @Override
        public void run() {
            if (count < SEQUENCE_COUNT) {
                // TODO remove after debug
                binding.seqCount.setText(String.format("Seq. #: %s", Integer.toString(count + 1)));

                if (count == 0) {
                    // START FIRST SEQUENCE
                    runNextSequence();
                } else {
                    CountDownFragment countDownFragment = CountDownFragment.newInstance(true);
                    showDialog(countDownFragment);
                }
            }
            // MOVE TO NEXT PARADIGM
            else {
                Paradigm paradigm = TrainingApp.nextParadigmActivity(currentParadigm);
                if (paradigm != null) {
                    PauseActivity.startActivity(TrainingActivity.this, paradigm, false, null);
                    //startActivity(TrainingActivity.this, paradigm);
                }
            }
        }

        private void runNextSequence() {
            new TrialRunner(this).run();
            count += 1;
        }
    }

    private class TrialRunner implements Runnable {

        private final int perGridStimCount;
        private final int cellSize;
        private final int gridSize;
        private final SequenceRunner parentSequence;

        // recursive loop counter
        private int count = 0;
        private final int paddingStart;
        private final int stimSize;

        public TrialRunner(SequenceRunner parentSequence) {
            this.parentSequence = parentSequence;
            this.perGridStimCount = parentSequence.getPerGridStimCount();
            this.gridSize = parentSequence.getGridSize();
            this.cellSize = parentSequence.getCellSize();

            // each cell can contain 4 actual stimuli; this excess space is for 'pseudo-randomness'..
            // simulated with padding inside the cell.
            paddingStart = perGridStimCount <= 4 ? 20 : 10;
            stimSize = (cellSize / 2) - paddingStart;
        }

        @Override
        public void run() {
            if (count < TRIAL_COUNT) {
                final List<List<ImageView>> stimuli = setupGridViews();

                // merge stimuli from both grids
                final List<ImageView> allStimuli = new ArrayList<ImageView>(stimuli.get(LEFT_INDEX));
                allStimuli.addAll(stimuli.get(RIGHT_INDEX));

                // Once the last view's layout is finished we can start setting up stimuli
                final ImageView lastAddedStim = allStimuli.get(allStimuli.size() - 1);
                lastAddedStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                    // prevent event loop
                    lastAddedStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final Trial trial = new Trial(difficulty);

                    // TODO remove after debug
                    binding.trialCount.setText(String.format("Trial #: %s", Integer.toString(count + 1)));

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
                            throw new IllegalStateException(String.format(
                                    "Trials's cueSide state is invalid: {0}", trial.getCueSide().toString()));
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
                                    TrainingUtils.setViewsVisible(true, views);

                                    // RETENTION INTERVAL
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            TrainingUtils.setViewsVisible(false, views);

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
                                                    TrainingUtils.setViewsVisible(true, views);
                                                    TrainingUtils.setViewsVisible(true,
                                                            binding.trainingActivitySameBtn,
                                                            binding.trainingActivityDifferentBtn);

                                                    // TRIAL END
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            TrainingUtils.setViewsVisible(false, views);

                                                            disableAnswerBtns();
                                                            // insert null if user did not answer this trial
                                                            if (answers.size() == count)
                                                                answers.add(null);

                                                            // START NEXT TRIAL
                                                            handler.postDelayed(
                                                                    TrialRunner.this,
                                                                    POST_TRIAL_PAUSE * DEBUG_SLOW);

                                                            count += 1;

                                                        }
                                                    }, TEST_INTERVAL * DEBUG_SLOW);

                                                }
                                            }, RETENTION_INTERVAL * DEBUG_SLOW);

                                        }
                                    }, MEMORIZATION_INTERVAL * DEBUG_SLOW);

                                }
                            }, CUE_INTERVAL * DEBUG_SLOW);

                        }
                    }, POST_CUE_PAUSE * DEBUG_SLOW);
                    }
                });
            }
            // START NEXT SEQUENCE
            else {
                handler.post(parentSequence);
            }
        }

        @NonNull
        private List<List<ImageView>> setupGridViews() {
            // Two sets of stimuli corresponding to two grids (left and right)
            final List<List<ImageView>> stimuli = new ArrayList<List<ImageView>>(2);
            stimuli.add(new ArrayList<ImageView>(perGridStimCount));
            stimuli.add(new ArrayList<ImageView>(perGridStimCount));

            final List<Rect> basePositions = TrainingUtils.generateGridPositions(gridSize, cellSize);
            if (basePositions.size() < perGridStimCount)
                throw new IllegalStateException("The grid is too small for the number of stimuli.");

            // Prepare the grid cells..
            for (int i = 0; i < 2; ++i) {
                ConstraintLayout grid = grids[i];
                // this is not i deep copy! it is only for shuffling on different grids
                final List<Rect> gridPositions = new ArrayList<Rect>(basePositions);
                Collections.shuffle(gridPositions);

                for (int j = 0; j < perGridStimCount; ++j) {
                    ImageView v = TrainingUtils.createStimView(TrainingActivity.this);
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
            return stimuli;
        }

        private void disableAnswerBtns() {
            binding.trainingActivityDifferentBtn.setClickable(false);
            binding.trainingActivitySameBtn.setClickable(false);
        }
    }

    protected abstract void performChange(ImageView changingStim);

    protected abstract void initStimuli(List<ImageView> stimuli);

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

    private void showDialog(DialogFragment fragment) {
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onCountDownExpired() {
        finish();
    }

    @Override
    public void onContinueClicked() {
        sequenceRunner.runNextSequence();
    }
}
