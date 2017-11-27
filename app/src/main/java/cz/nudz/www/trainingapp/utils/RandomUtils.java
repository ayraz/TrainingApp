package cz.nudz.www.trainingapp.utils;

import java.util.Random;

/**
 * Created by artem on 27-May-17.
 */

public final class RandomUtils {

    private static final Random rand = new Random();

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int nextIntInclusive(int min, int max) {
        if (min >= max)
            throw new IllegalArgumentException("Max value must be greater than min.");

        // nextInt is normally exclusive of the top value, so add 1 to make it inclusive
        int randomInt = rand.nextInt((max - min) + 1) + min;
        return randomInt;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, exclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int nextIntExclusive(int min, int max) {
        if (min >= max)
            throw new IllegalArgumentException("Max value must be greater than min.");

        int randomInt = rand.nextInt(max - min) + min;
        return randomInt;
    }

    /**
     * Returns a random boolean value from a normal distribution with mean 0 and standard deviation 1.
     * The results may be either positive or negative, with both being equally likely.
     * @return
     */
    public static boolean nextGaussianBool() {
        return rand.nextGaussian() < 0 ? false : true;
    }
}
