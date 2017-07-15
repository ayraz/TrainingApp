package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.ArrayUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

public class TrainingActivity extends AppCompatActivity {

    private static final String TAG = TrainingActivity.class.getSimpleName();

    // Measure = milliseconds
    public static final int CUE_INTERVAL = 350;
    public static final int MEMORIZATION_INTERVAL = 100;
    public static final int RETENTION_INTERVAL = 900;
    public static final int TEST_INTERVAL = 2000;

    private TrainingActivityBinding binding;
    private ConstraintLayout layout;
    private Context context;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        layout = binding.trainingActivityRootLayout;
        context = this;
        res = getResources();

        // SETUP...
        final Trial trial = new Trial(Paradigm.COLOR, 5, context);
        final int stimCount = trial.getStimCount();
        final int halfCount = stimCount / 2;

        final Drawable square = res.getDrawable(R.drawable.square);

        // Expand grid with empty views to ensure proper placement of stimuli.
//        for (int i = 0; i < rowColCount; ++i) {
//            GridLayout.Spec spec = GridLayout.spec(i);
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.columnSpec = spec;
//            params.rowSpec = spec;
//
//            for (int j = 0; j < 2; ++j) {
//                Space space = new cSpace(this);
//                space.setBackground(square);
//                space.setVisibility(View.INVISIBLE);
//                space.setLayoutParams(params);
//                if (j == 0)
//                    binding.trainingActivityLeftGrid.addView(space);
//                else
//                    binding.trainingActivityRightGrid.addView(space);
//            }
//        }

        // Setup stimuli/probe colors..
        final List<Integer> colors = ArrayUtils.toIntArrayList(res.getIntArray(R.array.trialColors));
        Collections.shuffle(colors);

        //
        // EVENT HANDLERS
        //

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
                final int gridCellSize = stimWidth * 2;
                final int gridSize = binding.trainingActivityLeftGrid.getWidth();
                final int rowColCount = (int) Math.sqrt(Math.pow(gridSize, 2) / Math.pow(gridCellSize, 2));

                binding.trainingActivityLeftGrid.setRowCount(rowColCount);
                binding.trainingActivityLeftGrid.setColumnCount(rowColCount);
                binding.trainingActivityRightGrid.setRowCount(rowColCount);
                binding.trainingActivityRightGrid.setColumnCount(rowColCount);

                // Generate semi-random positions in the grid..
                final List<Point> leftPositions = new ArrayList<>(stimCount);
                final List<Point> rightPositions = new ArrayList<>(stimCount);
                for (int i = 0; i < halfCount; ++i) {
                    // find empty position
                    Point pos;
                    do {
                        pos = getRandomPos(rowColCount);
                    }
                    while (leftPositions.indexOf(pos) != -1);
                    leftPositions.add(pos);
                }
                for (int i = halfCount; i < stimCount; ++i) {
                    Point pos;
                    do {
                        pos = getRandomPos(rowColCount);
                    }
                    while (rightPositions.indexOf(pos) != -1);
                    rightPositions.add(pos);
                }

                // First just create views to get them drawn, so that we have actual sizes to work with..
                final List<ImageView> stimuli = new ArrayList<>(stimCount);
                for (int i = 0; i < stimCount; ++i) {
                    // 'clone' drawable so that we can alter color for each.
                    Drawable drawable = square.mutate();
                    ImageView v = new ImageView(context);
//                    v.setVisibility(View.INVISIBLE);
                    v.setImageDrawable(drawable);
                    v.setColorFilter(colors.get(i % colors.size()));
                    // we need to set view's id to later find it in the layout..
                    v.setId(View.generateViewId());
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.addRule(RelativeLayout.CENTER_IN_PARENT);
                    v.setLayoutParams(params1);

                    Point pos;
                    if (i < halfCount) {
                        pos = leftPositions.get(i);
                    } else {
                        pos = rightPositions.get(i-halfCount);
                    }
                    GridLayout.Spec colSpec = GridLayout.spec(pos.x);
                    GridLayout.Spec rowSpec = GridLayout.spec(pos.y);

                    GridLayout.LayoutParams params2 = new GridLayout.LayoutParams();
                    params2.rowSpec = rowSpec;
                    params2.columnSpec = colSpec;
                    params2.width = gridCellSize;
                    params2.height = gridCellSize;

                    // Create a container to hold our stimuli so we can 'randomize' position inside it.
                    RelativeLayout container = new RelativeLayout(context);
                    container.setLayoutParams(params2);
                    container.addView(v);

                    if (i < halfCount) {
                        binding.trainingActivityLeftGrid.addView(container);
                    } else {
                        binding.trainingActivityRightGrid.addView(container);
                    }
                }

                // Once the last view's layout is finished we can start setting up stimuli
//                final ImageView lastStim = stimuli.get(stimuli.size() - 1);
//                lastStim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        // prevent event loop
//                        lastStim.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                        // START THE TRIAL...
//                        final View cue;
//                        switch (trial.getCueSide()) {
//                            case LEFT:
//                                cue = binding.trainingActivityCueLeft;
//                                break;
//                            case RIGHT:
//                                cue = binding.trainingActivityCueRight;
//                                break;
//                            default:
//                                throw new IllegalStateException(String.format("Trials's cueSide state is invalid: {0}", trial.getCueSide().toString()));
//                        }
//                        cue.setVisibility(View.VISIBLE);
//
//                        new android.os.Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                cue.setVisibility(View.GONE);
//                                for (ImageView stim : stimuli) {
//                                    stim.setVisibility(View.VISIBLE);
//                                }
//
//
//                            }
//                        }, CUE_INTERVAL);
//                    }
//                });
            }
        });
    }

    @NonNull
    private Point getRandomPos(int rowColCount) {
        return new Point(
                        RandomUtils.nextInt(0, rowColCount-1),
                        RandomUtils.nextInt(0, rowColCount-1)
                );
    }

    /**
     * See: https://math.stackexchange.com/questions/466198/algorithm-to-get-the-maximum-size-of-n-squares-that-fit-into-a-rectangle-with-a
     * @param x Width of containing rectangle.
     * @param y Height of containing rectangle.
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
