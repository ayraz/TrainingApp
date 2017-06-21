package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nudz.www.trainingapp.utils.ArrayUtils;

/**
 * Created by artem on 05-Jun-17.
 */

public class Trial {

    public static final int DIFFICULTY_SCALE_FACTOR = 2;

    private final Paradigm paradigm;
    private final int difficulty;
    private final int stimCount;

    public List<ShapeView> getStimuli() {
        return stimuli;
    }

    private final List<ShapeView> stimuli;
    private final Context ctx;

    public Trial(Paradigm paradigm, int difficulty, Point center, double radius, Context ctx) {
        this.paradigm = paradigm;
        this.difficulty = difficulty;
        this.stimCount = difficulty * DIFFICULTY_SCALE_FACTOR; // <-- just for testing purposes...
        this.ctx = ctx;

        // Setup trial's stimuli...
        Resources res = ctx.getResources();

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

        // Create views..
        final Drawable drawable = res.getDrawable(R.drawable.rect);
        stimuli = new ArrayList<>(stimCount);
        for (int i = 0; i < stimCount; ++i) {
            ShapeView shape = new ShapeView(ctx, drawable, stimPositions.get(i));
            shape.setColorFilter(colors.get(i));
            stimuli.add(shape);
        }
    }
}
