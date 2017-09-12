package cz.nudz.www.trainingapp.training;

/**
 * Created by artem on 27-May-17.
 */

public enum Paradigm {
    COLOR,
    SHAPE,
    POSITION;

    public static Class<? extends TrainingActivity> toTrainingClass(Paradigm paradigm) {
        switch (paradigm) {
            case COLOR:
                return ColorParadigmActivity.class;
            case SHAPE:
                return ShapeParadigmActivity.class;
            case POSITION:
                return PositionParadigmActivity.class;
            default:
                throw new IllegalArgumentException("No matching paradigm class exists.");
        }
    }
}
