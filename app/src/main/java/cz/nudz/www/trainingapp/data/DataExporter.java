package cz.nudz.www.trainingapp.data;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.utils.Utils;

/**
 * Created by P8P67 on 10/28/2017.
 */

public class DataExporter {

    private AsyncTask<String, Integer, Void> task;

    public interface DataExportListener {

        void onExportStart();

        void onExportFinish();

        void onExportProgressUpdate(Integer progress);
    }

    private final String TAG = DataExporter.class.getSimpleName();
    private final AppCompatActivity activity;
    private final TrainingAppDbHelper dbHelper;
    private final DataExportListener listener;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int COLUMN_COUNT = 20;
    private String queryString = "SELECT username AS [username], " +
            "ts.id AS [sessionId], ts.startDate AS [sessionStartDate], ts.endDate AS [sessionEndDate], ts.isFinished AS [isSessionFinished], " +
            "p.id AS [paradigmId], p.startDate AS [paradigmStartDate], p.endDate AS [paradigmEndDate], p.paradigmType AS [paradigmType], p.pauseDurationMillis AS [paradigmPauseDuration], " +
            "s.id AS [sequenceId], s.startDate AS [sequenceStartDate], s.endDate AS [sequenceEndDate], s.difficulty AS [difficulty], " +
            "t.id AS [trialId], t.isChangingTrial AS [trialChanged], t.isCorrect AS [trialResponse], t.responseTimeMillis AS [trialResponseTime], t.stimuliJSON, t.changedStimulusJSON " +
            "FROM User u " +
            "JOIN TrainingSession ts ON ts.user_id = u.username " +
            "JOIN Paradigm p ON p.trainingSession_id = ts.id " +
            "JOIN Sequence s ON s.paradigm_id = p.id " +
            "JOIN Trial t ON t.sequence_id = s.id " +
            "WHERE u.username = ?";


    public DataExporter(BaseActivity activity, DataExportListener listener) {
        this.activity = activity;
        this.dbHelper = activity.getHelper();
        this.listener = listener;
    }

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

        task = new AsyncExport().execute(username, filePath);
    }

    public void cancel() {
        if (task != null) {
            task.cancel(true);
        }
    }

    /**
     * Checks if external storage is available for read and write.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     *
     * @return Whether we have permission to write to external storage
     * @see <a href="https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android">details</a>
     */
    public static boolean verifyStoragePermissions(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    private class AsyncExport extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String username = strings[0];
            String filePath = strings[1];
            try {
                try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(queryString, new String[]{username});
                     CSVWriter writer = new CSVWriter(new FileWriter(filePath, false), ';')) {
                    writer.writeNext(cursor.getColumnNames());
                    final int count = cursor.getCount();
                    int counter = 0;
                    while (cursor.moveToNext()) {
                        String[] row = new String[COLUMN_COUNT];
                        for (int i = 0; i < COLUMN_COUNT; ++i) {
                            String value = cursor.getString(i);
                            if (!Utils.isNullOrEmpty(value)) {
                                row[i] = value;
                            } else {
                                row[i] = "N/A";
                            }
                        }
                        writer.writeNext(row);
                        publishProgress((int) ((counter / (float) count) * 100));
                        ++counter;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                Utils.showErrorDialog(activity, null, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            listener.onExportProgressUpdate(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onExportStart();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.onExportFinish();
            Toast toast = Toast.makeText(activity, R.string.dataExportSuccess, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
