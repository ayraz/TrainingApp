package cz.nudz.www.trainingapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.List;

public class ColorParadigmActivity extends TrainingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void performChange(ImageView changingStim) {
        // take last color (reserved for change)
        final int changeColor = colors.get(colors.size() - 1);
        changingStim.setColorFilter(changeColor);
    }

    @Override
    protected void setStimuliColors(List<ImageView> stimuli) {
        for (int i = 0; i < stimuli.size(); ++i) {
            stimuli.get(i).setColorFilter(colors.get(i));
        }
    }

    @Override
    protected void setStimuliRotations(List<ImageView> stimuli) {

    }

    @Override
    protected void setStimuliShapes(List<ImageView> stimuli) {
        for (ImageView v : stimuli) {
            // 'clone' drawable so that we can alter color for each.
            Drawable drawable = getResources().getDrawable(R.drawable.square).mutate();
            v.setImageDrawable(drawable);
        }
    }
}
