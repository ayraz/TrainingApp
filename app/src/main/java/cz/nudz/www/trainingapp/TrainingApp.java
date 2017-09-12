package cz.nudz.www.trainingapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.nudz.www.trainingapp.training.ColorParadigmActivity;
import cz.nudz.www.trainingapp.training.Paradigm;
import cz.nudz.www.trainingapp.training.PositionParadigmActivity;
import cz.nudz.www.trainingapp.training.ShapeParadigmActivity;
import cz.nudz.www.trainingapp.training.TrainingActivity;

/**
 * Created by artem on 12-Sep-17.
 */

public class TrainingApp extends Application {

    private final static List<Paradigm> paradigms = new ArrayList<>(Arrays.asList(
            Paradigm.COLOR,
            Paradigm.POSITION,
            Paradigm.SHAPE));

    /**
     *
     * @param currentParadigm
     * @return Returns next paradigm in a fixed sequence or null if there are no more.
     */
    public static Paradigm nextParadigmActivity(Paradigm currentParadigm) {
        int i = paradigms.lastIndexOf(currentParadigm);
        // no paradigms left
        if (i == paradigms.size() - 1)
            return null;
        else
            return paradigms.get(i + 1);
    }
}
