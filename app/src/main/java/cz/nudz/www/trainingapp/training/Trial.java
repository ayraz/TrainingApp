package cz.nudz.www.trainingapp.training;

import cz.nudz.www.trainingapp.training.Side;

/**
 * Created by artem on 05-Jun-17.
 */

public class Trial {

    private final Side cueSide;
    private final int difficulty;
    private final int stimCount;
    private final boolean changing;

    /**
     * @param difficulty: one-based, range: 1 to 6.
     */
    public Trial(int difficulty) {
        assert difficulty >= 1 && difficulty <= 6;

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

    public int getDifficulty() {
        return difficulty;
    }
}