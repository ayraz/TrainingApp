package cz.nudz.www.trainingapp;

import android.app.Application;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.nudz.www.trainingapp.database.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.training.ParadigmType;

/**
 * Created by artem on 12-Sep-17.
 */

public class TrainingApp extends Application {

    // this should not be leaking since it is a self pointer...
    // edit: made a weak reference to be sure.
    private static WeakReference<Context> context;

    // TODO: move this paradigm stuff to another singleton..
    private final static List<ParadigmType> PARADIGM_TYPE_SET = new ArrayList<>(Arrays.asList(
            ParadigmType.COLOR,
            ParadigmType.POSITION,
            ParadigmType.SHAPE));

    private TrainingAppDbHelper dbHelper;

    public static WeakReference<Context> getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TrainingApp.context = new WeakReference<Context>(this);
    }

    /**
     *
     * @param currentParadigmType
     * @return Returns next paradigm in a fixed sequence or null if there are no more.
     */
    public static ParadigmType nextParadigm(ParadigmType currentParadigmType) {
        int i = PARADIGM_TYPE_SET.indexOf(currentParadigmType);
        // no paradigmSet left
        if (i == PARADIGM_TYPE_SET.size() - 1) {
            return null;
        } else {
            return PARADIGM_TYPE_SET.get(i + 1);
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

    public static int indexOfParadigm(ParadigmType paradigmType) {
        return PARADIGM_TYPE_SET.indexOf(paradigmType);
    }
}
