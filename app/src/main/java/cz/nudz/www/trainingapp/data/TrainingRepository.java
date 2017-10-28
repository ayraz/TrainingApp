package cz.nudz.www.trainingapp.data;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Date;

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
