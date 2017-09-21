package cz.nudz.www.trainingapp;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.nudz.www.trainingapp.database.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.training.Paradigm;

/**
 * Created by artem on 12-Sep-17.
 */

public class TrainingApp extends Application {

    private final static List<Paradigm> paradigmSet = new ArrayList<>(Arrays.asList(
            Paradigm.COLOR,
            Paradigm.POSITION,
            Paradigm.SHAPE));

    private TrainingAppDbHelper dbHelper;

    public static int indexOfParadigm(Paradigm paradigm) {
        return paradigmSet.indexOf(paradigm);
    }

    /**
     *
     * @param currentParadigm
     * @return Returns next paradigm in a fixed sequence or null if there are no more.
     */
    public static Paradigm nextParadigm(Paradigm currentParadigm) {
        int i = paradigmSet.indexOf(currentParadigm);
        // no paradigmSet left
        if (i == paradigmSet.size() - 1) {
            return null;
        } else {
            return paradigmSet.get(i + 1);
        }
    }

    public TrainingAppDbHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, TrainingAppDbHelper.class);
        }
        return dbHelper;
    }

    public void releaseHelper() {
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }
}
