package cz.nudz.www.trainingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.RandomUtils;

import static android.support.constraint.ConstraintSet.PARENT_ID;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "x: " + x + " y: " + y);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        layout = binding.trainingActivityRootLayout;
        context = this;

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
                // prevent event loop
                binding.trainingActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final Trial trial = new Trial(Paradigm.COLOR, 6, context);
                // First just throw in the views to get them drawn
                // so that we have actual sizes to work with..
                final List<Stimulus> stimuli = trial.getStimuli();
                for (Stimulus stim : stimuli) {
                    // we need to set view's id to later find it in the layout..
                    stim.view.setId(View.generateViewId());
                    stim.view.setVisibility(View.INVISIBLE);
                    layout.addView(stim.view);
                }
                final Stimulus lastStim = stimuli.get(stimuli.size()-1);

                // Once the last view's layout is finished we can start setting up stimuli
                lastStim.view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // prevent event loop
                        lastStim.view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // SETUP...
                        // get display's center coordinates using fixation
                        Point center = new Point();
                        ImageView fixation = binding.trainingActivityFixationPoint;
                        center.x = (int) (fixation.getX() + fixation.getWidth() / 2);
                        center.y = (int) (fixation.getY() + fixation.getHeight() / 2);

                        // get drawing boundaries for figures
                        float leftBound = binding.trainingActivityGuideLeft.getX();
                        float rightBound = binding.trainingActivityGuideRight.getX();

                        trial.setupStimuli(lastStim.view.getWidth(), lastStim.view.getHeight(), center, 280d, leftBound, rightBound);

                        for (Stimulus stim : stimuli) {
                            // There is currently a bug with connect's horizontal margins,
                            // here is a workaround: https://stackoverflow.com/questions/44129278/adding-constraints-to-a-view-in-a-constraintlayout-ignore-left-and-right-margins
                            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            // now we can get actual dimensions...
                            params.setMargins(
                                    (stim.position.x - stim.view.getWidth()/2),
                                    (stim.position.y - stim.view.getHeight()/2),
                                    0, 0);
                            params.leftToLeft = PARENT_ID;
                            params.topToTop = PARENT_ID;
                            stim.view.setLayoutParams(params);
                        }

                        // START THE TRIAL...
                        final View cue;
                        switch (trial.cueSide) {
                            case LEFT:
                                cue = binding.trainingActivityCueLeft;
                                break;
                            case RIGHT:
                                cue = binding.trainingActivityCueRight;
                                break;
                            default:
                                throw new IllegalStateException(String.format("Trials's cueSide state is invalid: {0}", trial.cueSide.toString()));
                        }
                        cue.setVisibility(View.VISIBLE);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cue.setVisibility(View.GONE);
                                for (Stimulus stim : stimuli) {
                                    stim.view.setVisibility(View.VISIBLE);
                                }


                            }
                        }, CUE_INTERVAL);
                    }
                });
            }
        });
    }
}
