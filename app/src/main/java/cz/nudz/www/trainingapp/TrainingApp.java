package cz.nudz.www.trainingapp;

import android.app.Application;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.lang.ref.WeakReference;

import cz.nudz.www.trainingapp.data.TrainingAppDbHelper;

/**
 * Created by artem on 12-Sep-17.
 */

public class TrainingApp extends Application {

    // this should not be leaking since it is a self pointer...
    // edit: made a weak reference to be sure.
    private static WeakReference<Context> context;

    private TrainingAppDbHelper dbHelper;

    public static WeakReference<Context> getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TrainingApp.context = new WeakReference<Context>(this);
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
