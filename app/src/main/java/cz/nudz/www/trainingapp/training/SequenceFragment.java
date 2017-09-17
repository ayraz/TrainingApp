package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TrainingFragmentBinding;
import cz.nudz.www.trainingapp.utils.RandomUtils;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

import static cz.nudz.www.trainingapp.training.Side.LEFT;

public abstract class SequenceFragment extends Fragment {

    public static final String TAG = SequenceFragment.class.getSimpleName();

    private static final String KEY_DIFFICULTY = "KEY_DIFFICULTY";
    // Measure = milliseconds
    private static final int CUE_INTERVAL = 300;
    private static final int POST_CUE_PAUSE = 100;
    private static final int MEMORIZATION_INTERVAL = 100;
    private static final int RETENTION_INTERVAL = 900;
    private static final int TEST_INTERVAL = 2000;

    private static final int POST_TRIAL_PAUSE = 2000;
    private static final int TRIAL_COUNT = 20;
    private static final int LEFT_INDEX = 0;
    private static final int RIGHT_INDEX = 1;
    // Do not set to 0, unless you want to nullify other intervals..
    private static final int DEBUG_SLOW = 0;

    private TrainingFragmentBinding binding;
    private ConstraintLayout[] grids;
    private SequenceFragmentListener listener;
    private final Handler handler = new Handler();

    private int difficulty;
    // Unanswered trials are NULL
    private List<Boolean> answers = new ArrayList<>(TRIAL_COUNT);;
    private int gridSize;
    private int cellSize;
    private int halfStimCount;
    private int totalStimCount;
    private int paddingStart;
    private int stimSize;

    public static SequenceFragment newInstance(Paradigm paradigm, int difficulty) {
        SequenceFragment fragment = Paradigm.toTrainingFragment(paradigm);
        Bundle args = new Bundle();
        args.putInt(KEY_DIFFICULTY , difficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SequenceFragmentListener) {
            listener = (SequenceFragmentListener) context;
        } else {
            throw new ClassCastException("Parent must implement SequenceFragmentListener interface.");
        }
    }

    public void removePendingCallbacks() {
        this.handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.training_fragment, container, false);

        grids = new ConstraintLayout[]{binding.trainingFragmentLeftGrid, binding.trainingFragmentRightGrid};
        difficulty = getArguments().getInt(KEY_DIFFICULTY);

        /*
         * Using this onExpired as an indicator that layout has finished...
         * meaning that we can use views' measures, etc. at this point.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingFragmentRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // prevent infinite event loop
                binding.trainingFragmentRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Difficulty may change on per-sequence basis
                totalStimCount = TrainingUtils.getStimCount(difficulty);
                halfStimCount = totalStimCount / 2;

                // Grids on both sides are identical so use whatever.
                // They aren't true squares so take the minimum dimension as grid size.
                gridSize = Math.min(
                        binding.trainingFragmentLeftGrid.getWidth(),
                        binding.trainingFragmentLeftGrid.getHeight());

                cellSize = TrainingUtils.optimalContainingSquareSize(gridSize, gridSize, halfStimCount);
                // each cell can contain 4 actual stimuli; this excess space is for 'pseudo-randomness'..
                // simulated with padding inside the cell.
                paddingStart = halfStimCount <= 4 ? 20 : 10;
                stimSize = (cellSize / 2) - paddingStart;

                new TrialRunner().run();

            }
        });
        
        return binding.getRoot();
    }

    private class TrialRunner implements Runnable {

        // recursive loop counter
        private int count = 0;

        @Override
        public void run() {
            if (count < TRIAL_COUNT) {
                executeTrial();
            } else {
                listener.onSequenceFinished(answers);
            }
        }

        private void executeTrial() {
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
                binding.trainingFragmentSameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answers.add(!trial.isChanging());
                        // allow only one answer
                        disableAnswerBtns();
                    }
                });

                binding.trainingFragmentDifferentBtn.setOnClickListener(new View.OnClickListener() {
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
                        cue = binding.trainingFragmentCueLeft;
                        break;
                    case RIGHT:
                        cue = binding.trainingFragmentCueRight;
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
                                                        binding.trainingFragmentSameBtn,
                                                        binding.trainingFragmentDifferentBtn);

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

        @NonNull
        private List<List<ImageView>> setupGridViews() {
            // Two sets of stimuli corresponding to two grids (left and right)
            final List<List<ImageView>> stimuli = new ArrayList<List<ImageView>>(2);
            stimuli.add(new ArrayList<ImageView>(halfStimCount));
            stimuli.add(new ArrayList<ImageView>(halfStimCount));

            final List<Rect> basePositions = TrainingUtils.generateGridPositions(gridSize, cellSize);
            if (basePositions.size() < halfStimCount)
                throw new IllegalStateException("The grid is too small for the number of stimuli.");

            // Prepare the grid cells..
            for (int i = 0; i < 2; ++i) {
                ConstraintLayout grid = grids[i];
                // this is not i deep copy! it is only for shuffling on different grids
                final List<Rect> gridPositions = new ArrayList<Rect>(basePositions);
                Collections.shuffle(gridPositions);

                for (int j = 0; j < halfStimCount; ++j) {
                    ImageView v = TrainingUtils.createStimView(getActivity());
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

    protected abstract void performChange(ImageView changingStim);

    protected abstract void initStimuli(List<ImageView> stimuli);

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

    public interface SequenceFragmentListener {
        void onSequenceFinished(List<Boolean> answers);
    }
}