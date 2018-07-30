package cz.nudz.www.trainingapp.utils;

import com.android.internal.util.Predicate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    /**
     * Convert Integer list to int array.
     * @param list
     * @return
     */
    public static int[] toArray(List<Integer> list) {
        final int size = list.size();
        int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static <T, R> List<R> map(List<T> list, Func1<T, R> f) {
        List<R> result = new ArrayList<>(list.size());
        for (T e : list) {
            result.add(f.call(e));
        }
        return result;
    }

    /**
     *
     * @param start inclusive
     * @param end exclusive
     * @return
     */
    public static List<Integer> range(int start, int end) {
        List<Integer> range = new ArrayList<>(end - start);
        for (int i = start; i < end; ++i) {
            range.add(i);
        }
        return range;
    }

    public interface Func1<T, R> {
        R call(T in);
    }
}
