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

import java.util.ArrayList;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.CollectionUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;
import cz.nudz.www.trainingapp.utils.Utils;

public class PositionParadigmFragment extends TrainingFragment {

    private int[] angles = new int[] {0, 45, 90, 135, 225, 315};
    private String shapeName;
    private Drawable drawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shapeName = Utils.getShapeName(R.drawable.rect);
        drawable = getResources().getDrawable(R.drawable.rect).mutate();

        return super.onCreateView(inflater, container, savedInstanceState);
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
            final int color = ContextCompat.getColor(getActivity(), R.color.red);
            v.setTag(new Pair<>(color, shapeName));
            v.setImageDrawable(drawable);
            v.setColorFilter(color);
            v.setRotation(angles[RandomUtils.nextIntExclusive(0, angles.length)]);
        }
    }
}
