package cz.nudz.www.trainingapp.training;

/**
 * Created by artem on 27-May-17.
 */

public enum ParadigmType {
    COLOR,
    SHAPE,
    POSITION;

    public static SequenceFragment toTrainingFragment(ParadigmType paradigmType) {
        switch (paradigmType) {
            case COLOR:
                return new ColorParadigmFragment();
            case SHAPE:
                return new ShapeParadigmFragment();
            case POSITION:
                return new PositionParadigmFragment();
            default:
                throw new IllegalArgumentException("No matching paradigm class exists.");
        }
    }
}
