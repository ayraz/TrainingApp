package cz.nudz.www.trainingapp;

import cz.nudz.www.trainingapp.enums.Side;
import cz.nudz.www.trainingapp.enums.Difficulty;

/**
 * Created by artem on 05-Jun-17.
 */

public class Trial {

    private final Side cueSide;
    private final boolean changing;

    /**
     * @param difficulty: one-based, range: 1 to 6.
     */
    public Trial(Difficulty difficulty) {
        this.cueSide = Math.random() < 0.5 ? Side.LEFT : Side.RIGHT;
        this.changing = Math.random() < 0.5 ? false : true;
    }

    public Side getCueSide() {
        return cueSide;
    }

    public boolean isChanging() {
        return changing;
    }
}
