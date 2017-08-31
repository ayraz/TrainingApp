package cz.nudz.www.trainingapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.android.internal.util.Predicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

public class ShapeParadigmActivity extends TrainingActivity {

    private List<Drawable> drawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                getResources().getDrawable(R.drawable.diamond).mutate(),
                getResources().getDrawable(R.drawable.kite).mutate());

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void performChange(final ImageView changingStim) {
        List<Drawable> filtered = CollectionUtils.filterList(this.drawables, new Predicate<Drawable>() {
            @Override
            public boolean apply(Drawable drawable) {
                return !(drawable == changingStim.getDrawable());
            }
        });
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
            v.setColorFilter(ContextCompat.getColor(this, R.color.black));
        }
    }
}
