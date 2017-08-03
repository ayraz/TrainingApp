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

    private final Side cueSide;
    private final Paradigm paradigm;
    private final int difficulty;
    private final int stimCount;
    private final Resources res;
    private final Context ctx;
    private final boolean changing;

    /**
     *
     * @param paradigm
     * @param difficulty: one-based, range: 1 to 6.
     * @param ctx
     */
    public Trial(Paradigm paradigm, int difficulty, Context ctx) {
        assert difficulty >= 1 && difficulty <= 6;

        this.ctx = ctx;
        this.res = ctx.getResources();
        this.paradigm = paradigm;
        this.difficulty = difficulty;
        this.stimCount = (1 + difficulty) * 2;
        this.cueSide = Math.random() < 0.5 ? Side.LEFT : Side.RIGHT;
        this.changing = Math.random() < 0.5 ? false : true;
    }

    public int getStimCount() {
        return stimCount;
    }

    public Side getCueSide() {
        return cueSide;
    }

    public boolean isChanging() {
        return changing;
    }
}
