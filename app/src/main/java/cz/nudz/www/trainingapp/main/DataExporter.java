package cz.nudz.www.trainingapp.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * Created by P8P67 on 10/28/2017.
 */

public class DataExporter {

    private final String TAG = DataExporter.class.getSimpleName();
    private final AppCompatActivity activity;
    private final TrainingAppDbHelper dbHelper;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public DataExporter(BaseActivity activity) {
        this.activity = activity;
        this.dbHelper = activity.getHelper();
    };

    /**
     * Exports user training data in excel compatible format.
     */
    public void export(String username) {
        verifyStoragePermissions(activity);
        String baseDir;
        if (isExternalStorageWritable()) {
            baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            baseDir = activity.getFilesDir().getAbsolutePath();
        }
        String fileName = "TrainingData.csv";
        String filePath = baseDir + File.separator + fileName;

        File f = new File(filePath);
        try {
            final int colCount = 14;
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT username AS [Username], " +
                    "ts.startDate AS [Session Start Date], ts.endDate AS [Session End Date], ts.isFinished AS [Is Finished], " +
                    "p.startDate AS [Paradigm Start Date], p.endDate AS [Paradigm End Date], p.paradigmType AS [Paradigm Type], p.pauseDurationMillis AS [Paradigm Pause], " +
                    "s.startDate AS [Sequence Start Date], s.endDate AS [Sequence End Date], s.difficulty AS [Difficulty], " +
                    "ta.isChangingTrial AS [Trial Changed], ta.isCorrect AS [Trial Answer Correct], ta.responseTimeMillis AS [Trial Response Time] " +
                    "FROM User u " +
                    "JOIN TrainingSession ts ON ts.user_id = u.username " +
                    "JOIN Paradigm p ON p.trainingSession_id = ts.id " +
                    "JOIN Sequence s ON s.paradigm_id = p.id " +
                    "JOIN TrialAnswer ta ON ta.sequence_id = s.id " +
                    "WHERE u.username = ?", new String[]{username});

            CSVWriter writer = new CSVWriter(new FileWriter(filePath , false), ';');
            try {
                writer.writeNext(cursor.getColumnNames());
                while (cursor.moveToNext()) {
                    String[] row = new String[colCount];
                    for (int i = 0; i < colCount; ++i) {
                        String value = cursor.getString(i);
                        if (!Utils.isNullOrEmpty(value)) {
                            row[i] = value;
                        } else {
                            row[i] = "N/A";
                        }
                    }
                    writer.writeNext(row);
                }
            } finally {
                cursor.close();
                writer.close();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Utils.showErrorDialog(activity, null, e.getMessage());
            return;
        }

        Toast toast = Toast.makeText(activity, R.string.dataExportSuccess, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Checks if external storage is available for read and write.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     *
     * @see <a href="https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android">details</a>
     */
    public static void verifyStoragePermissions(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
