package cz.nudz.www.trainingapp;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.RandomUtil;

import static android.support.constraint.ConstraintSet.*;

public class TrainingActivity extends AppCompatActivity {

    private TrainingActivityBinding binding;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        final Drawable drawable = getResources().getDrawable(R.drawable.rect);
        v = new ShapeView(this, drawable);
        binding.trainingActivityRootLayout.addView(v);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.trainingActivityRootLayout);
        constraintSet.connect(v.getId(), LEFT, R.id.trainingActivityGuideLeft, RIGHT);
        constraintSet.connect(v.getId(), BOTTOM, R.id.trainingActivityGuideBottom, BOTTOM);
        constraintSet.applyTo(binding.trainingActivityRootLayout);

        /*
         * Using this callback as an indicator that layout has finished..
         * and I can use views' measures.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.trainingActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newLeft = RandomUtil.nextInt(binding.trainingActivityGuideLeft.getLeft(), binding.trainingActivityGuideCenter.getLeft());
                int newTop = RandomUtil.nextInt(binding.trainingActivityRootLayout.getTop() + 50, binding.trainingActivityGuideBottom.getTop());

                /*
                 * This assumes that we always start from..
                 * the corners of the guides without margins
                 */

                v.setX(newLeft);
                v.setY(newTop);
            }
        });
    }
}
