package cz.nudz.www.trainingapp.training;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;
import cz.nudz.www.trainingapp.utils.Utils;

public class ShapeParadigmFragment extends TrainingFragment {

    private static List<Integer> drawableIds = Arrays.asList(
        R.drawable.circle,
        R.drawable.ellipse,
        R.drawable.square,
        R.drawable.rect,
        R.drawable.triangle,
        R.drawable.trapezoid,
        R.drawable.pentagon,
        R.drawable.star,
        R.drawable.parallelogram,
        R.drawable.cross,
        R.drawable.rhombus,
        R.drawable.kite);
    private List<Pair<Drawable, String>> drawablePairs = new ArrayList<>(drawableIds.size());
    private int color;

    @Override
    protected void performChange(final ImageView changingStim) {
        List<Pair<Drawable, String>> filtered = CollectionUtils.filter(this.drawablePairs, drawablePair -> !(drawablePair.first == changingStim.getDrawable()));
        final Pair<Drawable, String> randomPair = filtered.get(RandomUtils.nextIntExclusive(0, filtered.size()));
        Drawable changeDrawable = randomPair.first;
        changingStim.setImageDrawable(changeDrawable);
        changingStim.setTag(new Pair<>(color, randomPair.second));
    }

    @Override
    protected void initStimuli(List<ImageView> stimuli) {
        color = ContextCompat.getColor(getActivity(), R.color.black);

        for (int id : drawableIds) {
            drawablePairs.add(new Pair<>(getResources().getDrawable(id).mutate(), Utils.getShapeName(id)));
        }
        Collections.shuffle(drawablePairs);

        for (int i = 0; i < stimuli.size(); ++i) {
            ImageView v = stimuli.get(i);
            final Pair<Drawable, String> pair = drawablePairs.get(i % drawablePairs.size());
            Drawable drawable = pair.first;
            String shapeName = pair.second;

            v.setTag(new Pair<>(color, shapeName));
            v.setImageDrawable(drawable);
            v.setColorFilter(color);
        }
    }
}
