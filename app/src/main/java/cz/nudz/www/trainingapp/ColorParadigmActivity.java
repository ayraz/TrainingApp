package cz.nudz.www.trainingapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.utils.ArrayUtils;

public class ColorParadigmActivity extends TrainingActivity {

    private List<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup stimuli/probe colors..
        // The color count covers entirely stimuli count even for hardest difficulty + 1 for color change.
        colors = ArrayUtils.toIntArrayList(getResources().getIntArray(R.array.trialColors));
        Collections.shuffle(colors);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void performChange(ImageView changingStim) {
        // take last color (reserved for change)
        final int changeColor = colors.get(colors.size() - 1);
        changingStim.setColorFilter(changeColor);
    }

    @Override
    protected void initStimuli(List<ImageView> stimuli) {
        for (int i = 0; i < stimuli.size(); ++i) {
            ImageView v = stimuli.get(i);

            // 'clone' drawable so that we can alter color for each.
            Drawable drawable = getResources().getDrawable(R.drawable.square).mutate();

            v.setImageDrawable(drawable);
            v.setColorFilter(colors.get(i));
        }
    }
}
