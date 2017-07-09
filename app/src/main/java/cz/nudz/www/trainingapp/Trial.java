package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.utils.ArrayUtils;
import cz.nudz.www.trainingapp.utils.RandomUtils;

/**
 * Created by artem on 05-Jun-17.
 */

public class Trial {

    public final Side cueSide;

    private final Paradigm paradigm;
    private final int difficulty;
    private final int stimCount;
    private final Resources res;

    public List<Stimulus> getStimuli() {
        return stimuli;
    }

    private final List<Stimulus> stimuli;
    private final Context ctx;

    public Trial(Paradigm paradigm, int difficulty, Context ctx) {
        this.ctx = ctx;
        this.res = ctx.getResources();
        this.paradigm = paradigm;
        this.difficulty = difficulty;
        this.stimCount = (2 + (difficulty - 1)) * 6;
        this.cueSide = Math.random() < 0.5 ? Side.LEFT : Side.RIGHT;

        // Create views..
        stimuli = new ArrayList<>(stimCount);
        for (int i = 0; i < stimCount; ++i) {
            // 'clone' drawable so that we can alter color for each.
            Drawable drawable = res.getDrawable(R.drawable.rect).mutate();
            ImageView v = new ImageView(ctx);
            v.setImageDrawable(drawable);
            Stimulus s = new Stimulus(v);
            stimuli.add(s);
        }
    }

    /**
     * @param width
     * @param height
     * @param center Center of the parent's root layout (screen).
     * @param radius Desired distance from specified center.
     * @param leftBound
     * @param rightBound
     */
    public void setupStimuli(int width, int height, Point center, double radius, float leftBound, float rightBound) {
        // Generate positions, assuming root layout..
        List<Point> stimPositions = new ArrayList<>(stimCount);
        for (int i = 0; i < stimCount; ++i) {
            double angle = Math.toRadians(((double) 360 / stimCount) * i);
            Point pos = new Point(
                    (int) Math.floor(Math.sin(angle) * radius + center.x),
                    (int) Math.floor(Math.cos(angle) * radius + center.y));
            stimPositions.add(pos);
        }
        Collections.shuffle(stimPositions);

        // Setup stimuli/probe colors..
        List<Integer> colors = ArrayUtils.toIntArrayList(res.getIntArray(R.array.trialColors));
        Collections.shuffle(colors);

        for (int i = 0; i < stimCount; ++i) {
            Stimulus stim = stimuli.get(i);
            stim.position = stimPositions.get(i);
//            stim.view.setColorFilter(colors.get(i % colors.size()));
        }
    }

    public static enum Side {
        RIGHT,
        LEFT;
    }
}
