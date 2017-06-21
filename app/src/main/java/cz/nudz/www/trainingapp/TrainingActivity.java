package cz.nudz.www.trainingapp;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.ArrayUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

import static android.support.constraint.ConstraintSet.*;

public class TrainingActivity extends AppCompatActivity {

    private static final String TAG = TrainingActivity.class.getSimpleName();

    private TrainingActivityBinding binding;
    private ConstraintLayout layout;

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

        // get display's center coordinates
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        Point center = new Point(outSize.x/2, outSize.y/2);
        // test trial
        final Trial trial = new Trial(Paradigm.COLOR, 5, center, 150d, this);

        // First just throw in the view to get them drawn..
        for (ShapeView v : trial.getStimuli()) {
            // we need to set view's id to later find it in the layout..
            v.setId(View.generateViewId());
            layout.addView(v);
        }

        /*
         * Using this callback as an indicator that layout has finished..
         * and I can use views' measures.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Immediately unsubscribe to not get stuck in a loop..
                binding.trainingActivityRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int actionbarHeight = getSupportActionBar().getHeight();

                for (ShapeView v : trial.getStimuli()) {
                    // There is currently a bug with connect's horizontal margins,
                    // here is a workaround: https://stackoverflow.com/questions/44129278/adding-constraints-to-a-view-in-a-constraintlayout-ignore-left-and-right-margins
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    // now we can get actual dimensions...
                    params.setMargins(
                            (v.positionInLayout.x - v.getWidth()/2),
                            (v.positionInLayout.y - v.getHeight() - actionbarHeight),
                            0, 0);
                    params.leftToLeft = PARENT_ID;
                    params.topToTop = PARENT_ID;
                    v.setLayoutParams(params);

                }
            }
        });
    }
}
