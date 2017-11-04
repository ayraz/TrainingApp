package cz.nudz.www.trainingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.data.tables.Sequence;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.data.tables.User;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by artem on 26-Sep-17.
 */
public class TrainingRepository {

    public static final String TAG = TrainingRepository.class.getSimpleName();

    private final Context context;
    private final TrainingAppDbHelper dbHelper;

    private RuntimeExceptionDao<Paradigm, Integer> paradigmDao;
    private RuntimeExceptionDao<Sequence, Integer> sequenceDao;
    private RuntimeExceptionDao<User, String> userDao;
    private RuntimeExceptionDao<TrainingSession, Integer> trainingSessionDao;

    public TrainingRepository(Context context, TrainingAppDbHelper helper) {
        this.context = context.getApplicationContext();
        this.dbHelper = helper;
        this.paradigmDao = dbHelper.getParadigmDao();
        this.sequenceDao = dbHelper.getSequenceDao();
        this.userDao = dbHelper.getUserDao();
        this.trainingSessionDao = dbHelper.getTrainingSessionDao();
    }

    public TrainingSession startAndStoreTrainingSession(String username) {
        User user = userDao.queryForId(username);
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
        Paradigm p = paradigmDao.queryForId(paradigm.getId());
        p.setEndDate(new Date());
        paradigmDao.update(p);
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
        Sequence seq = sequenceDao.queryForId(sequence.getId());
        seq.setEndDate(new Date());
        sequenceDao.update(seq);
    }

    public void finishAndUpdateSession(TrainingSession trainingSession) {
        TrainingSession ts = trainingSessionDao.queryForId(trainingSession.getId());
        ts.setEndDate(new Date());
        ts.setFinished(true);
        trainingSessionDao.update(ts);
    }

    public List<SessionData> getParadigmSessionData(String username, ParadigmType paradigmType) {
        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT username , " +
                "ts.startDate, " +
                "MAX(s.difficulty) AS maxDifficulty " +
                "FROM User u " +
                "JOIN TrainingSession ts ON ts.user_id = u.username " +
                "JOIN Paradigm p ON p.trainingSession_id = ts.id " +
                "JOIN Sequence s ON s.paradigm_id = p.id " +
                "WHERE u.username = ? AND p.paradigmType = ? AND ts.isFinished = 1 " +
                "GROUP BY ts.id, ts.startDate " +
                "ORDER BY ts.startDate ASC",
                new String[]{username, paradigmType.toString()})) {

                List<SessionData> results = new ArrayList<>();
                while (cursor.moveToNext()) {
                    try {
                        results.add(new SessionData(
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("startDate"))),
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

    public static class SessionData {
        public SessionData(Date sessionDate, int maxDifficulty) {
            this.sessionDate = sessionDate;
            this.maxDifficulty = maxDifficulty;
        }

        public Date sessionDate;
        public int maxDifficulty;
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
