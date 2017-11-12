package cz.nudz.www.trainingapp.training;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.Utils;

public class ColorParadigmFragment extends TrainingFragment {

    private List<Integer> colors;
    private String shapeName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Setup stimuli/probe colors..
        // The color count covers entirely stimuli count even for hardest difficulty + 1 for color change.
        colors = CollectionUtils.toList(getResources().getIntArray(R.array.trialColors));
        shapeName = Utils.getShapeName(R.drawable.square);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void performChange(ImageView changingStim) {
        // take last color (reserved for change)
        final int changeColor = colors.get(colors.size() - 1);
        changingStim.setColorFilter(changeColor);
        changingStim.setTag(new Pair<>(changeColor, shapeName));
    }

    @Override
    protected void initStimuli(List<ImageView> stimuli) {
        Collections.shuffle(colors);

        for (int i = 0; i < stimuli.size(); ++i) {
            ImageView v = stimuli.get(i);
            // 'clone' drawable so that we can alter color for each.
            Drawable drawable = getResources().getDrawable(R.drawable.square).mutate();
            final Integer color = colors.get(i);
            v.setTag(new Pair<>(color, shapeName));
            v.setImageDrawable(drawable);
            v.setColorFilter(color);
        }
    }
}
