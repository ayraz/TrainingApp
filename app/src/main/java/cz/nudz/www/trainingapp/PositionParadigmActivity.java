package cz.nudz.www.trainingapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cz.nudz.www.trainingapp.utils.RandomUtils;

public class PositionParadigmActivity extends TrainingActivity {

    private Drawable drawable;
    private int[] angles = new int[] {0, 45, 90, 135, 225, 315};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        drawable = getResources().getDrawable(R.drawable.rect).mutate();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void performChange(ImageView changingStim) {
        int current = (int) changingStim.getRotation();

        // filter current position
        List<Integer> filtered = new ArrayList<>(angles.length - 1);
        for (int i = 0; i < angles.length; ++i) {
            if (angles[i] != current) filtered.add(angles[i]);
        }
        // change angle
        changingStim.setRotation(filtered.get(RandomUtils.nextIntExclusive(0, filtered.size())));
    }

    @Override
    protected void initStimuli(List<ImageView> stimuli) {
        for (ImageView v : stimuli) {
            // 'clone' drawable so that we can alter color for each.
            v.setImageDrawable(drawable);
            v.setColorFilter(ContextCompat.getColor(this, R.color.red));
            v.setRotation(angles[RandomUtils.nextIntExclusive(0, angles.length)]);
        }
    }
}
