package cz.nudz.www.trainingapp;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by artem on 12-Sep-17.
 */

public class TrainingApp extends Application {

    // this should not be leaking since App context is tied to process
    // making it a weak reference just to be sure.
    private static WeakReference<Context> context;

    public static Context getContext() {
        return context.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TrainingApp.context = new WeakReference<>(this);
    }
}
