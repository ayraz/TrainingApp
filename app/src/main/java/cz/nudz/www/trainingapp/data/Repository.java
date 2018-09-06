package cz.nudz.www.trainingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class Repository {

    public static final String TAG = Repository.class.getSimpleName();

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

    public Repository(BaseActivity context) {
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

    public Sequence createAndStoreSequence(Paradigm paradigm, Difficulty difficulty) {
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
        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(lastSessionDataQuery,
                new String[] { sessionManager.getUsername(), paradigmType })) {
            List<Pair<String, Integer>> result = new ArrayList<>(TrainingActivity.DEFAULT_SEQUENCE_COUNT);
            if (cursor.moveToFirst()) {
                final int paradigmId = cursor.getInt(cursor.getColumnIndex("paradigmId"));
                int i = 1;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), ++i) {
                    // if at any point we get a sequence from different paradigm, the query must be wrong.
                    if (paradigmId != cursor.getInt(cursor.getColumnIndex("paradigmId")))
                        throw new IllegalStateException("Sequences are from distinct paradigms; broken query.");

                    result.add(new Pair<>(
                            context.getString(R.string.sequence) + " " + i + ".",
                            cursor.getInt(cursor.getColumnIndex("difficulty"))
                    ));
                }
            }
            return result;
        }
    }

    public List<Pair<String, Integer>> getAllSessionParadigmData(String paradigmType) {
        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(allSessionDataQuery,
            new String[]{sessionManager.getUsername(), paradigmType})) {

            List<Pair<String, Integer>> results = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    results.add(new Pair<>(
                            // TODO: perhaps find a better way to do this
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
        try {
            return getTrainingSessionDao().queryBuilder()
                    .join(getCurrentUserQB()).where().eq("isFinished", true)
                    .countOf() >= 2;
        } catch (SQLException e) {
            Log.e(TAG, "Could not get finished session count.");
            return false;
        }
    }

    private Paradigm getLastParadigm(ParadigmType type) {
        try {
            QueryBuilder<Paradigm, Integer> pQb = getFinishedParadigmByType(type);
            return pQb.queryForFirst();
        } catch (SQLException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Paradigm getPenultimateParadigm(ParadigmType type) {
        try {
            QueryBuilder<Paradigm, Integer> pQb = getFinishedParadigmByType(type);
            List<Paradigm> query = pQb.limit(2L).query();
            // take second record
            return query.get(1);
        } catch (SQLException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private QueryBuilder<Paradigm, Integer> getFinishedParadigmByType(ParadigmType type) throws SQLException {
        QueryBuilder<User, String> uQb = getCurrentUserQB();
        QueryBuilder<TrainingSession, Integer> tsQb = getTrainingSessionDao().queryBuilder();
        tsQb.join(uQb).where().eq("isFinished", true);
        tsQb.orderBy("startDate", false);
        QueryBuilder<Paradigm, Integer> pQb = getParadigmDao().queryBuilder();
        pQb.join(tsQb).where().eq("paradigmType", type);
        return pQb;
    }

    @NonNull
    private QueryBuilder<User, String> getCurrentUserQB() throws SQLException {
        QueryBuilder<User, String> uQb = getUserDao().queryBuilder();
        uQb.where().eq("username", sessionManager.getUsername());
        return uQb;
    }

    private List<Sequence> getSequences(int paradigmId) {
        try {
            return getSequenceDao().queryBuilder()
                    .where().eq("paradigm_id", paradigmId).query();
        } catch (SQLException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * During all inter-paradigm pauses the user hasn’t taken a pause which was
     * longer than 15 seconds.
     * @return
     */
    public boolean hasOnlyShortPauses() {
        boolean longPause = false;
        final int ALLOWED_PAUSE = 15; // seconds
        for (ParadigmType type : ParadigmType.values()) {
            Paradigm lastParadigm = getLastParadigm(type);
            if (lastParadigm != null) {
                longPause = longPause || ((lastParadigm.getPauseDurationMillis() / 1000) > ALLOWED_PAUSE);
            }
        }
        return !longPause;
    }

    /**
     * The user increased the difficulty level in each paradigm at least once during
     * the training session.
     */
    public boolean hasRaisedDiffInAllParadigms() {
        boolean allRaised = true;
        for (ParadigmType type : ParadigmType.values()) {
            Paradigm lastParadigm = getLastParadigm(type);
            if (lastParadigm != null) {
                List<Sequence> currSeq = getSequences(lastParadigm.getId());
                int currMaxDiff = sequenceMaxDifficulty(currSeq);
                allRaised = allRaised && (currMaxDiff > Difficulty.toInteger(Difficulty.ONE));
            }
        }
        return allRaised;
    }

    /**
     * The user increased the difficulty level in each paradigm at least once during
     * the training session compared to previous paradigm.
     */
    public boolean hasImprovedInAllParadigms() {
        boolean allImproved = true;
        for (ParadigmType type : ParadigmType.values()) {
            Paradigm lastParadigm = getLastParadigm(type);
            if (lastParadigm != null) {
                List<Sequence> currSeq = getSequences(lastParadigm.getId());
                List<Sequence> prevSeq = getSequences(getPenultimateParadigm(type).getId());
                int currMaxDiff = sequenceMaxDifficulty(currSeq);
                int prevMaxDiff = sequenceMaxDifficulty(prevSeq);
                allImproved = allImproved && (currMaxDiff > prevMaxDiff);
            }
        }
        return allImproved;
    }

    /**
     * The aggregate results of this session are same or better than those of the
     * previous.
     * @return
     */
    public boolean isCurrentSessionSameOrBetter() {
        int currSum = 0;
        int prevSum = 0;
        for (ParadigmType type : ParadigmType.values()) {
            Paradigm lastParadigm = getLastParadigm(type);
            if (lastParadigm != null) {
                List<Sequence> currSeq = getSequences(lastParadigm.getId());
                List<Sequence> prevSeq = getSequences(getPenultimateParadigm(type).getId());
                currSum += sequenceMaxDifficulty(currSeq);
                prevSum += sequenceMaxDifficulty(prevSeq);
            }
        }
        return prevSum <= currSum;
    }

    private int sequenceMaxDifficulty(List<Sequence> sequences) {
        int max = 1;
        for (Sequence s : sequences) {
            if (s.getDifficulty() > max) {
                max = s.getDifficulty();
            }
        }
        return max;
    }

    public static User createUser(final String username, RuntimeExceptionDao<User, String> userDao) {
        User newUser = new User();
        newUser.setUsername(username);
        Date now = new Date();
        newUser.setRegistrationDate(now);
        newUser.setLastLoginDate(now);
        userDao.create(newUser);
        return newUser;
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
