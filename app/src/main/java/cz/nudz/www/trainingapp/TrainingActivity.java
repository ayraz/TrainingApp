package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.ArrayUtils;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

public class TrainingActivity extends AppCompatActivity {

    private static final String TAG = TrainingActivity.class.getSimpleName();

    // Measure = milliseconds
    public static final int CUE_INTERVAL = 350;
    public static final int MEMORIZATION_INTERVAL = 200;
    public static final int RETENTION_INTERVAL = 900;
    public static final int TEST_INTERVAL = 2000;

    private TrainingActivityBinding binding;
    private ConstraintLayout layout;
    private Context context;
    private Resources res;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        layout = binding.trainingActivityRootLayout;
        context = this;
        res = getResources();

        // SETUP...
        final Trial trial = new Trial(Paradigm.COLOR, 1, context);
        final int stimCount = trial.getStimCount();
        final int halfCount = stimCount / 2;

        final Drawable square = res.getDrawable(R.drawable.square);

        // Setup stimuli/probe colors..
        final List<Integer> colors = ArrayUtils.toIntArrayList(res.getIntArray(R.array.trialColors));
        Collections.shuffle(colors);

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

                // Drawable height/width are the same (square) so get whatever..
                // also right and left grids are the same.
                int stimWidth = square.getIntrinsicWidth();
                // add padding half of stimuli size so that we have some space for 'randomness' later
                int padding = stimWidth;
                final int gridCellSize = stimWidth + padding;
                final int gridSize = binding.trainingActivityLeftGrid.getWidth();
                final List<Rect> positions = generateGridPositions(gridSize, gridCellSize);
                Collections.shuffle(positions);

                final List<ImageView> stimuli = new ArrayList<>(stimCount);
                for (int i = 0; i < stimCount; ++i) {
                    // 'clone' drawable so that we can alter color for each.
                    Drawable drawable = res.getDrawable(R.drawable.square).mutate();
                    ImageView v = new ImageView(context);
                    v.setVisibility(View.INVISIBLE);
                    v.setImageDrawable(drawable);
                    v.setColorFilter(colors.get(i % colors.size()));
                    // we need to set view's id to later find it in the layout..
                    v.setId(View.generateViewId());
                    stimuli.add(v);

                    FrameLayout container = new FrameLayout(context);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = RandomUtils.nextIntInclusive(0, padding);
                    params.leftMargin = RandomUtils.nextIntInclusive(0, padding);
                    v.setLayoutParams(params);
                    container.addView(v);

                    if (i < halfCount) {
                        addViewToGrid(container, binding.trainingActivityLeftGrid, positions.get(i), gridCellSize);
                    } else {
                        addViewToGrid(container, binding.trainingActivityRightGrid, positions.get(i % halfCount), gridCellSize);
                    }
                }

                // Once the last view's layout is finished we can start setting up stimuli
                final ImageView lastStim = stimuli.get(stimuli.size() - 1);
                lastStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // prevent event loop
                        lastStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);

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

                        // MEMORY ARRAY
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cue.setVisibility(View.GONE);
                                setViewsVisible(stimuli, true);

                                // RETENTION INTERVAL
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setViewsVisible(stimuli, false);

                                        // pick random index for non-cued grid
                                        int index = trial.getCueSide() == Side.RIGHT
                                                ? RandomUtils.nextIntExclusive(0, halfCount)
                                                : RandomUtils.nextIntExclusive(halfCount, stimCount);

                                        final ImageView changedStim = stimuli.get(index);

                                        // pick random color, different from previous
                                        List<Integer> filteredColors = CollectionUtils.filterList(colors, new Predicate() {
                                            @Override
                                            public boolean apply(Object o) {
                                                return changedStim.getColorFilter().equals(o) ? false : true;
                                            }
                                        });
                                        int colorIndex = RandomUtils.nextIntExclusive(0, filteredColors.size());
                                        changedStim.setColorFilter(filteredColors.get(colorIndex));

                                        // TEST ARRAY
                                        new android.os.Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setViewsVisible(stimuli, true);
                                            }
                                        }, RETENTION_INTERVAL);
                                    }
                                }, MEMORIZATION_INTERVAL);
                            }
                        }, CUE_INTERVAL);
                    }
                });
            }
        });
    }

    private static void setViewsVisible(List<ImageView> stimuli, boolean visible) {
        for (ImageView stim : stimuli) {
            stim.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
        params.setMargins(position.left, position.top, position.right, position.bottom);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        v.setLayoutParams(params);
        grid.addView(v);
    }
}
