package cz.nudz.www.trainingapp.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by artem on 19-Sep-17.
 */

public class TrainingAppDbHelper extends SQLiteOpenHelper {

    private static final String TAG = TrainingAppDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TrainingApp.db";

    public TrainingAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class<?> clazz : TrainingAppTables.class.getDeclaredClasses()) {
            try {
                String sql = clazz.getField("CREATE_TABLE").get(null).toString();
                db.execSQL(sql);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: potentially dangerous update...
        for (Class<?> clazz : TrainingAppTables.class.getDeclaredClasses()) {
            try {
                String table = clazz.getField("TABLE_NAME").get(null).toString();
                db.execSQL("DROP TABLE IF EXISTS " + table);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
