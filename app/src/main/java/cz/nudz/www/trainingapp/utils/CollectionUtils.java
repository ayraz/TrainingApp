package cz.nudz.www.trainingapp.utils;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artem on 15-Jul-17.
 */

public class CollectionUtils {

    public static <T> List<T> filterList(List<T> list, Predicate p) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (p.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
