package cz.nudz.www.trainingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.data.tables.Sequence;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.data.tables.Trial;
import cz.nudz.www.trainingapp.data.tables.User;

/**
 * Created by artem on 19-Sep-17.
 */

public class TrainingAppDbHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = TrainingAppDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TrainingApp.db";
    private final Context context;

    public TrainingAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            logger.info(TAG, "onCreate");
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, TrainingSession.class);
            TableUtils.createTable(connectionSource, Paradigm.class);
            TableUtils.createTable(connectionSource, Sequence.class);
            TableUtils.createTable(connectionSource, Trial.class);

            // Inject admin user
            Repository.createUser("##", getUserDao());
        } catch (SQLException e) {
            logger.error(TAG, "Couldn't create a table", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            logger.info(TAG, "onUpgrade");
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, TrainingSession.class, true);
            TableUtils.dropTable(connectionSource, Paradigm.class, true);
            TableUtils.dropTable(connectionSource, Sequence.class, true);
            TableUtils.dropTable(connectionSource, Trial.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            logger.error(TAG, "Couldn't drop table", e);
            throw new RuntimeException(e);
        }
    }

    // DAOs
    private RuntimeExceptionDao<User, String> userDao;
    private RuntimeExceptionDao<TrainingSession, Integer> trainingSessionDao;
    private RuntimeExceptionDao<Paradigm, Integer> paradigmDao;
    private RuntimeExceptionDao<Sequence, Integer> sequenceDao;
    private RuntimeExceptionDao<Trial, Integer> trialDao;

    public RuntimeExceptionDao<User, String> getUserDao() {
        if (userDao == null){
            userDao = getRuntimeExceptionDao(User.class);
        }
        return userDao;
    }

    public RuntimeExceptionDao<TrainingSession, Integer> getTrainingSessionDao() {
        if (trainingSessionDao == null) {
            trainingSessionDao = getRuntimeExceptionDao(TrainingSession.class);
        }
        return trainingSessionDao;
    }

    public RuntimeExceptionDao<Paradigm, Integer> getParadigmDao() {
        if (paradigmDao == null) {
            paradigmDao = getRuntimeExceptionDao(Paradigm.class);
        }
        return paradigmDao;
    }

    public RuntimeExceptionDao<Sequence, Integer> getSequenceDao() {
        if (sequenceDao == null) {
            sequenceDao = getRuntimeExceptionDao(Sequence.class);
        }
        return sequenceDao;
    }

    public RuntimeExceptionDao<Trial, Integer> getTrialDao() {
        if (trialDao == null) {
            trialDao = getRuntimeExceptionDao(Trial.class);
        }
        return trialDao;
    }
}
