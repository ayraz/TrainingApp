package cz.nudz.www.trainingapp.enums;

import cz.nudz.www.trainingapp.training.ColorParadigmFragment;
import cz.nudz.www.trainingapp.training.PositionParadigmFragment;
import cz.nudz.www.trainingapp.training.TrainingFragment;
import cz.nudz.www.trainingapp.training.ShapeParadigmFragment;

/**
 * Created by artem on 27-May-17.
 */

public enum ParadigmType {
    COLOR,
    SHAPE,
    POSITION;

    public static TrainingFragment toTrainingFragment(ParadigmType paradigmType) {
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
