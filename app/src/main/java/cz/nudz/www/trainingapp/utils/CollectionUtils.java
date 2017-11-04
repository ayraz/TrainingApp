package cz.nudz.www.trainingapp.utils;

import com.android.internal.util.Predicate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artem on 15-Jul-17.
 */

public class CollectionUtils {

    /**
     * Filters given collection and returns result as a new list.
     * @param iterable
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(Iterable<T> iterable, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : iterable) {
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Converts int array to an Integer list.
     * @param array
     * @return
     */
    public static List<Integer> toList(int[] array) {
        List<Integer> result = new ArrayList<>(array.length);
        for (int i : array) {
            result.add(i);
        }
        return result;
    }
}
