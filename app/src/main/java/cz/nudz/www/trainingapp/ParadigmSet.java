package cz.nudz.www.trainingapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by artem on 01-Oct-17.
 */

public abstract class ParadigmSet {

    private final static List<ParadigmType> PARADIGM_TYPE_SET = new ArrayList<>(Arrays.asList(
            ParadigmType.COLOR,
            ParadigmType.POSITION,
            ParadigmType.SHAPE));

    /**
     *
     * @param currentParadigmType
     * @return Returns next paradigm in a fixed sequence or null if there are no more.
     */
    public static ParadigmType getNext(ParadigmType currentParadigmType) {
        int i = PARADIGM_TYPE_SET.indexOf(currentParadigmType);
        // no paradigm left
        if (i == PARADIGM_TYPE_SET.size() - 1) {
            return null;
        } else {
            return PARADIGM_TYPE_SET.get(i + 1);
        }
    }

    public static int indexOf(ParadigmType paradigmType) {
        return PARADIGM_TYPE_SET.indexOf(paradigmType);
    }

    public static List<ParadigmType> getParadigmTypes() {
        return new ArrayList<>(PARADIGM_TYPE_SET);
    }

    public static int size() {
        return PARADIGM_TYPE_SET.size();
    }
}
