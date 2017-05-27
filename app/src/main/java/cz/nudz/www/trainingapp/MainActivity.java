package cz.nudz.www.trainingapp;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.Random;
import cz.nudz.www.trainingapp.databinding.MainActivityBinding;
import cz.nudz.www.trainingapp.utils.RandomUtil;

import static android.support.constraint.ConstraintSet.*;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        final Drawable drawable = getResources().getDrawable(R.drawable.rect);
        v = new ShapeView(this, drawable);
        binding.mainActivityRootLayout.addView(v);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.mainActivityRootLayout);
        constraintSet.connect(v.getId(), LEFT, R.id.guide_left, RIGHT);
        constraintSet.connect(v.getId(), BOTTOM, R.id.guide_bottom, BOTTOM);
        constraintSet.applyTo(binding.mainActivityRootLayout);

        /*
         * Using this callback as an indicator that layout has finished..
         * and I can use views' measures.
         * see: https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
         */
        binding.mainActivityRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newLeft = RandomUtil.nextInt(binding.guideLeft.getLeft(), binding.guideCenter.getLeft());
                int newTop = RandomUtil.nextInt(binding.mainActivityRootLayout.getTop() + 50, binding.guideBottom.getTop());

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
