package cz.nudz.www.trainingapp.training;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.internal.util.Predicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

public class ShapeParadigmFragment extends TrainingFragment {

    private List<Drawable> drawables;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        drawables = Arrays.asList(
                getResources().getDrawable(R.drawable.circle).mutate(),
                getResources().getDrawable(R.drawable.ellipse).mutate(),
                getResources().getDrawable(R.drawable.square).mutate(),
                getResources().getDrawable(R.drawable.rect).mutate(),
                getResources().getDrawable(R.drawable.triangle).mutate(),
                getResources().getDrawable(R.drawable.trapezoid).mutate(),
                getResources().getDrawable(R.drawable.pentagon).mutate(),
                getResources().getDrawable(R.drawable.star).mutate(),
                getResources().getDrawable(R.drawable.parallelogram).mutate(),
                getResources().getDrawable(R.drawable.cross).mutate(),
                getResources().getDrawable(R.drawable.rhombus).mutate(),
                getResources().getDrawable(R.drawable.kite).mutate());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void performChange(final ImageView changingStim) {
        List<Drawable> filtered = CollectionUtils.filterList(this.drawables, drawable -> !(drawable == changingStim.getDrawable()));
        Drawable changeDrawable = filtered.get(RandomUtils.nextIntExclusive(0, filtered.size()));
        changingStim.setImageDrawable(changeDrawable);
    }

    @Override
    protected void initStimuli(List<ImageView> stimuli) {
        Collections.shuffle(drawables);
        for (int i = 0; i < stimuli.size(); ++i) {
            ImageView v = stimuli.get(i);
            Drawable drawable = drawables.get(i % drawables.size());

            v.setImageDrawable(drawable);
            v.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
        }
    }
}
