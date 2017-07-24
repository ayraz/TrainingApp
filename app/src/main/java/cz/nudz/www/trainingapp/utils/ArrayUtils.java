package cz.nudz.www.trainingapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artem on 06-Jun-17.
 *
 * Because java generics are amazing.
 */

public class ArrayUtils {

    public static List<Integer> toIntArrayList(int[] a) {
        List<Integer> list = new ArrayList<>(a.length);
        for (int i : a) {
            list.add(i);
        }
        return list;
    }
}
