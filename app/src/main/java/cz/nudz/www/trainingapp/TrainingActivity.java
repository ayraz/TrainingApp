package cz.nudz.www.trainingapp;

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

public abstract class TrainingActivity extends AppCompatActivity {

    private static final String TAG = TrainingActivity.class.getSimpleName();

    // Measure = milliseconds
    private static final int CUE_INTERVAL = 300;
    private static final int CUE_PAUSE = 100;
    private static final int MEMORIZATION_INTERVAL = 100;
    private static final int RETENTION_INTERVAL = 900;
    private static final int TEST_INTERVAL = 2000;
    private static final int DEBUG_SLOW = 3;

    private static final int TRIAL_COUNT = 20;
    private static final int LEFT_INDEX = 0;
    private static final int RIGHT_INDEX = 1;

    private TrainingActivityBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);

        // SETUP...
        final Trial trial = new Trial(Paradigm.COLOR, 5, this);
        final int totalStimCount = trial.getStimCount();
        final int perGridStimCount = totalStimCount / 2;

        final List<Boolean> answers = new ArrayList<>(TRIAL_COUNT);
        final ConstraintLayout[] grids = new ConstraintLayout[]{binding.trainingActivityLeftGrid, binding.trainingActivityRightGrid};
        // Two sets of stimuli corresponding to two grids (left and right)
        final List<List<ImageView>> stimuli = new ArrayList<List<ImageView>>(2);
        stimuli.add(new ArrayList<ImageView>(perGridStimCount));
        stimuli.add(new ArrayList<ImageView>(perGridStimCount));

        // user answer handlers
        binding.trainingActivitySameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.add(!trial.isChanging());

                // allow only one answer
                v.setClickable(false);
            }
        });

        binding.trainingActivityDifferentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.add(trial.isChanging());

                // allow only one answer
                v.setClickable(false);
            }
        });

        /*
         * Using this callback as an indicator that layout has finished..
         * and I can use views' measures.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // prevent infinite event loop
                binding.trainingActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Grids on both sides are identical so use whatever.
                // They aren't true squares so take the minimum dimension as grid size.
                final int gridSize = Math.min(binding.trainingActivityLeftGrid.getWidth(), binding.trainingActivityLeftGrid.getHeight());
                final int cellSize = optimalContainingSquareSize(gridSize, gridSize, perGridStimCount);
                // each cell can contain 4 actual stimuli; this excess space is for 'quasi-randomness'..
                // simulated with padding inside the cell.
                final int padding = perGridStimCount <= 4 ? 30 : 15;
                final int stimSize = (cellSize / 2) - padding;

                final List<Rect> positions = generateGridPositions(gridSize, cellSize);
                if (positions.size() < perGridStimCount)
                    throw new IllegalStateException("The grid is too small for the number of stimuli.");

                for (int i = 0; i < 2; ++i) {
                    ConstraintLayout grid = grids[i];
                    // beware this is not a deep copy! it is only for shuffling
                    final List<Rect> rndPositions = new ArrayList<Rect>(positions);
                    Collections.shuffle(rndPositions);

                    for (int j = 0; j < perGridStimCount; ++j) {
                        ImageView v = createStimView();
                        stimuli.get(i).add(v);

                        FrameLayout container = new FrameLayout(TrainingActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(stimSize, stimSize);
                        int paddingEnd = cellSize - stimSize - padding;
                        params.topMargin = RandomUtils.nextIntInclusive(padding, paddingEnd);
                        params.leftMargin = RandomUtils.nextIntInclusive(padding, paddingEnd);
                        v.setLayoutParams(params);
                        container.addView(v);

                        addViewToGrid(container, grid, rndPositions.get(j), cellSize);
                    }
                }

                // TODO: Set color filters
                final List<ImageView> mergedStimuli = new ArrayList<ImageView>(stimuli.get(LEFT_INDEX));
                mergedStimuli.addAll(stimuli.get(RIGHT_INDEX));

                initStimuli(mergedStimuli);

                // Once the last view's layout is finished we can start setting up stimuli
                final ImageView lastAddedStim = stimuli.get(RIGHT_INDEX).get(perGridStimCount - 1);
                lastAddedStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // prevent event loop
                        lastAddedStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // START THE TRIAL...
                        // CUE
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

                        final Handler handler = new Handler();
                        // CUE PAUSE
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                cue.setVisibility(View.INVISIBLE);

                                // MEMORY ARRAY
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setViewsVisible(true, mergedStimuli.toArray(new View[stimuli.size()]));

                                        // RETENTION INTERVAL
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setViewsVisible(false, mergedStimuli.toArray(new View[stimuli.size()]));

                                                if (trial.isChanging()) {
                                                    // pick random stim in cued grid
                                                    int index = RandomUtils.nextIntExclusive(0, perGridStimCount);

                                                    final ImageView changingStim = trial.getCueSide() == Side.LEFT
                                                            ? stimuli.get(LEFT_INDEX).get(index)
                                                            : stimuli.get(RIGHT_INDEX).get(index);

                                                    performChange(changingStim);
                                                }

                                                // TEST ARRAY
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        setViewsVisible(true, mergedStimuli.toArray(new View[stimuli.size()]));
                                                        setViewsVisible(true,
                                                                binding.trainingActivitySameBtn,
                                                                binding.trainingActivityDifferentBtn);

                                                        // TRIAL END
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                            }
                                                        }, TEST_INTERVAL * DEBUG_SLOW);
                                                    }
                                                }, RETENTION_INTERVAL);
                                            }
                                        }, MEMORIZATION_INTERVAL * DEBUG_SLOW);
                                    }
                                }, CUE_INTERVAL);
                            }
                        }, CUE_PAUSE * DEBUG_SLOW);
                    }
                });
            }
        });
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
}
