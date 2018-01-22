package cz.nudz.www.trainingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.drm.DrmStore;
import android.support.v4.util.Pair;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.data.tables.Sequence;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.TrainingActivity;

/**
 * Created by artem on 26-Sep-17.
 */
public class TrainingRepository {

    public static final String TAG = TrainingRepository.class.getSimpleName();

    private final Context context;
    private final TrainingAppDbHelper dbHelper;
    private final SessionManager sessionManager;

    private RuntimeExceptionDao<Paradigm, Integer> paradigmDao;
    private RuntimeExceptionDao<Sequence, Integer> sequenceDao;
    private RuntimeExceptionDao<User, String> userDao;
    private RuntimeExceptionDao<TrainingSession, Integer> trainingSessionDao;

    private final String allSessionDataQuery = "SELECT username , " +
            "ts.startDate, " +
            "MAX(s.difficulty) AS maxDifficulty " +
            "FROM User u " +
            "JOIN TrainingSession ts ON ts.user_id = u.username " +
            "JOIN Paradigm p ON p.trainingSession_id = ts.id " +
            "JOIN Sequence s ON s.paradigm_id = p.id " +
            "WHERE u.username = ? AND p.paradigmType = ? AND ts.isFinished = 1 " +
            "GROUP BY ts.id, ts.startDate " +
            "ORDER BY ts.startDate ASC";

    private final String lastSessionDataQuery = "SELECT p.id AS paradigmId, s.difficulty " +
            "FROM User u " +
            "JOIN TrainingSession ts ON ts.user_id = u.username " +
            "JOIN Paradigm p ON p.trainingSession_id = ts.id " +
            "JOIN Sequence s ON s.paradigm_id = p.id " +
            "WHERE u.username = ? AND p.paradigmType = ? AND ts.isFinished = 1 " +
            "ORDER BY ts.startDate DESC, s.id ASC " +
            "LIMIT " + TrainingActivity.DEFAULT_SEQUENCE_COUNT;

    public TrainingRepository(BaseActivity context) {
        this.context = context.getApplicationContext();
        this.dbHelper = context.getHelper();
        this.sessionManager = context.getSessionManager();
        this.paradigmDao = dbHelper.getParadigmDao();
        this.sequenceDao = dbHelper.getSequenceDao();
        this.userDao = dbHelper.getUserDao();
        this.trainingSessionDao = dbHelper.getTrainingSessionDao();
    }

    public TrainingSession startAndStoreTrainingSession() {
        User user = userDao.queryForId(sessionManager.getUsername());
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setUser(user);
        trainingSession.setStartDate(new Date());
        trainingSession.setFinished(false);
        trainingSessionDao.create(trainingSession);
        return trainingSession;
    }

    public void updateParadigm(Paradigm paradigm) {
        paradigmDao.update(paradigm);
    }

    public Paradigm startAndStoreParadigm(TrainingSession trainingSession, ParadigmType paradigmType) {
        Paradigm paradigm = new Paradigm();
        paradigm.setTrainingSession(trainingSession);
        paradigm.setStartDate(new Date());
        paradigm.setParadigmType(paradigmType);
        paradigmDao.create(paradigm);
        return paradigm;
    }

    public void finishAndUpdateParadigm(Paradigm paradigm) {
        paradigm.setEndDate(new Date());
        paradigmDao.update(paradigm);
    }

    public Sequence startAndStoreSequence(Paradigm paradigm, Difficulty difficulty) {
        Sequence sequence = new Sequence();
        sequence.setParadigm(paradigm);
        sequence.setStartDate(new Date());
        sequence.setDifficulty(Difficulty.toInteger(difficulty));
        sequenceDao.create(sequence);
        return sequence;
    }

    public void finishAndUpdateSequence(Sequence sequence) {
        sequence.setEndDate(new Date());
        sequenceDao.update(sequence);
    }

    public void finishAndUpdateSession(TrainingSession trainingSession) {
        trainingSession.setEndDate(new Date());
        trainingSession.setFinished(true);
        trainingSessionDao.update(trainingSession);
    }

    public List<Pair<String, Integer>> getLastSessionParadigmData (String paradigmType) {
        List<Pair<String, Integer>> result = new ArrayList<>(TrainingActivity.DEFAULT_SEQUENCE_COUNT);
        iterateLastSessionParadigm(paradigmType, new Action2() {
            @Override
            public void call(Cursor cursor, int i) {
                result.add(new Pair<>(
                        context.getString(R.string.sequence) + " " + i + ".",
                        cursor.getInt(cursor.getColumnIndex("difficulty"))
                ));
            }
        });
        return result;
    }

    private void iterateLastSessionParadigm(String paradigmType, Action2 action) {
        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(lastSessionDataQuery,
                new String[] { sessionManager.getUsername(), paradigmType })) {
            if (cursor.moveToFirst()) {
                final int paradigmId = cursor.getInt(cursor.getColumnIndex("paradigmId"));
                int i = 1;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), ++i) {
                    // if at any point we get a sequence from different paradigm, the query must be wrong.
                    if (paradigmId != cursor.getInt(cursor.getColumnIndex("paradigmId")))
                        throw new IllegalStateException("Sequences are from distinct paradigms; broken query.");
                    action.call(cursor, i);
                }
            }
        }
    }

    public List<Pair<String, Integer>> getAllSessionParadigmData(String paradigmType) {
        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(allSessionDataQuery,
            new String[]{sessionManager.getUsername(), paradigmType})) {

            List<Pair<String, Integer>> results = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    results.add(new Pair<>(
                            // TODO: uh oh, perhaps find a better way to do this...
                            new SimpleDateFormat("dd/MM/yyyy").format(
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                            cursor.getString(cursor.getColumnIndex("startDate")))),
                            cursor.getInt(cursor.getColumnIndex("maxDifficulty"))
                    ));
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            return results;
        }
    }

    public boolean doManySessionsExist() {
        return getTrainingSessionDao().countOf() >= 2;
    }

    private void iterateAllLastSessionParadigms(Action2 action) {
        for (ParadigmType type : ParadigmType.values()) {
            iterateLastSessionParadigm(type.toString(), action);
        }
    }

    public boolean isShortPauseCondition() {
        // final array hack to simulate closure
        final boolean[] longPause = {false};
        final int ALLOWED_PAUSE = 15; // seconds
        iterateAllLastSessionParadigms((cursor, sequenceCount) -> {
            final long pauseDurationMillis = cursor.getLong(
                    cursor.getColumnIndex("pauseDurationMillis"));
            longPause[0] = longPause[0] || ((pauseDurationMillis / 1000) > ALLOWED_PAUSE);
            // all sequence records reference same paradigm, check only first
//            break;
        });
        return !longPause[0];
    }

    /**
     * The user increased the difficulty level in each paradigm at least once during
     * the training session.
     */
    public boolean isAllParadigmImprovedCondition() {
        final boolean[] colorRaised = {false};
        final boolean positionRaised = false;
        final boolean shapeRaised = false;

        int difficulty = Difficulty.toInteger(Difficulty.ONE);
        iterateLastSessionParadigm(ParadigmType.COLOR.toString(), (cursor, sequenceCount) -> {
            colorRaised[0] = colorRaised[0]
                    || (cursor.getInt(cursor.getColumnIndex("difficulty")) > difficulty);
        });

        difficulty = Difficulty.toInteger(Difficulty.ONE);
        iterateLastSessionParadigm(ParadigmType.POSITION.toString(), (cursor, sequenceCount) -> {
            colorRaised[0] = colorRaised[0]
                    || (cursor.getInt(cursor.getColumnIndex("difficulty")) > difficulty);
        });

        difficulty = Difficulty.toInteger(Difficulty.ONE);
        iterateLastSessionParadigm(ParadigmType.COLOR.toString(), (cursor, sequenceCount) -> {
            colorRaised[0] = colorRaised[0]
                    || (cursor.getInt(cursor.getColumnIndex("difficulty")) > difficulty);
        });

        return colorRaised[0] && positionRaised && shapeRaised;
    }

    private interface Action2 {

        void call(Cursor cursor, int sequenceCount);
    }

    public RuntimeExceptionDao<Paradigm, Integer> getParadigmDao() {
        return paradigmDao;
    }

    public RuntimeExceptionDao<Sequence, Integer> getSequenceDao() {
        return sequenceDao;
    }

    public RuntimeExceptionDao<User, String> getUserDao() {
        return userDao;
    }

    public RuntimeExceptionDao<TrainingSession, Integer> getTrainingSessionDao() {
        return trainingSessionDao;
    }
}
