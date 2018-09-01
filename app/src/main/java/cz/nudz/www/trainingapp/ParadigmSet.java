package cz.nudz.www.trainingapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by artem on 01-Oct-17.
 */

public abstract class ParadigmSet {

    private final static List<ParadigmType> TRAINING_PARADIGM_TYPE_SET = new ArrayList<>(Arrays.asList(
            ParadigmType.COLOR,
            ParadigmType.POSITION));

    private final static List<ParadigmType> TEST_PARADIGM_TYPE_SET = new ArrayList<>(Arrays.asList(
            ParadigmType.COLOR,
            ParadigmType.SHAPE));

    private final static List<ParadigmType> ALL_PARADIGM_TYPE_SET = new ArrayList<>(Arrays.asList(
            ParadigmType.COLOR,
            ParadigmType.POSITION,
            ParadigmType.SHAPE));

    public enum OperationMode {
        TRAINING,
        TEST,
        ALL
    }

    private static OperationMode operationMode;
    private static List<ParadigmType> paradigmSet;

    public static void setOperationMode(final OperationMode mode) {
        switch (mode) {
            case TEST:
                paradigmSet = TEST_PARADIGM_TYPE_SET;
                break;
            case TRAINING:
                paradigmSet = TRAINING_PARADIGM_TYPE_SET;
                break;
            case ALL:
                paradigmSet = ALL_PARADIGM_TYPE_SET;
                break;
            default:
                throw new RuntimeException(String.format(mode == null
                        ? "Mode is null" : "Invalid mode: %s", mode));
        }
        operationMode = mode;
    }
    
    /**
     *
     * @param currentParadigmType
     * @return Returns next paradigm in a fixed sequence or null if there are no more.
     */
    public static ParadigmType getNext(ParadigmType currentParadigmType) {
        int i = paradigmSet.indexOf(currentParadigmType);
        // no paradigm left
        if (i == paradigmSet.size() - 1) {
            return null;
        } else {
            return paradigmSet.get(i + 1);
        }
    }

    public static ParadigmType getAt(int i) {
        return paradigmSet.get(i);
    }

    public static int indexOf(ParadigmType paradigmType) {
        return paradigmSet.indexOf(paradigmType);
    }

    public static List<ParadigmType> getParadigmTypes() {
        return new ArrayList<>(paradigmSet);
    }

    public static OperationMode getOperationMode() {
        return operationMode;
    }

    public static int size() {
        return paradigmSet.size();
    }
}
