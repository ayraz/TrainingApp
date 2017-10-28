package cz.nudz.www.trainingapp.enums;

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
     * Or null if there isn't one.
     */
    public static Difficulty next(Difficulty difficulty) {
        int i = difficulty.ordinal() + 1;
        Difficulty[] values = Difficulty.values();
        if (i < values.length)
            return values[i];
        else
            return null;
    }

    /**
     *
     * @param difficulty
     * @return Returns previous difficulty in order; TWO > ONE, etc.
     * Or null if there isn't one.
     */
    public static Difficulty prev(Difficulty difficulty) {
        int i = difficulty.ordinal() - 1;
        if (i >= 0)
            return Difficulty.values()[i];
        else
            return null;
    }
}
