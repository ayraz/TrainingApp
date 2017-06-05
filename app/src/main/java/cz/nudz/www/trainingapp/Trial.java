package cz.nudz.www.trainingapp;

import android.content.Intent;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by artem on 05-Jun-17.
 */

public class Trial {

    public static final int STIM_CENTER_MARGIN = 50; // should this be random?

    private final Paradigm paradigm;
    private final int difficulty;
    private final int stimuliNum;

    private final List<Integer> stimuliColors;
    private List<Integer> probeColors;

    private final List<Point> stimuliTopLeftPositions;

    public Trial(Paradigm paradigm, int difficulty, Integer[] colors) {
        this.paradigm = paradigm;
        this.difficulty = difficulty;
        this.stimuliNum = difficulty * 2; // <-- just for testing purposes...

        // setup stimuli positions...
        stimuliTopLeftPositions = new ArrayList<>(stimuliNum);
        for (int i = 0; i < stimuliNum; ++i) {
            double angle = Math.toRadians((double) 360 / stimuliNum * i);
            Point pos = new Point(Math.cos(angle) + STIM_CENTER_MARGIN, Math.sin(angle) + STIM_CENTER_MARGIN);
            stimuliTopLeftPositions.add(pos);
        }
        Collections.shuffle(stimuliTopLeftPositions);

        // setup stimuli/probe colors..
        stimuliColors = Arrays.asList(colors);
        Collections.shuffle(stimuliColors);
    }

    public static class Point {

        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
