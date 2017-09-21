package cz.nudz.www.trainingapp.training;

/**
 * Created by artem on 21-Sep-17.
 */

public enum Difficulty {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;

    /**
     *
     * @param difficulty
     * @return Integer corresponding to the actual name of enum; ONE = 1, etc.
     */
    public static int toInteger(Difficulty difficulty) {
        return difficulty.ordinal() + 1;
    }

    /**
     *
     * @param difficulty
     * @return Returns next difficulty in order; ONE > TWO, etc.
     */
    public static Difficulty next(Difficulty difficulty) {
        return Difficulty.values()[difficulty.ordinal() + 1];
    }
}
