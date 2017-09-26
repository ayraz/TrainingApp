package cz.nudz.www.trainingapp.database;

import android.content.Context;
import android.os.Handler;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Date;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.database.tables.TrainingSession;
import cz.nudz.www.trainingapp.database.tables.User;
import cz.nudz.www.trainingapp.training.LoginActivity;
import cz.nudz.www.trainingapp.training.TrainingActivity;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

/**
 * Created by artem on 26-Sep-17.
 */
public class TrainingSessionRepository {

    private final Context context;
    private final TrainingAppDbHelper dbHelper;

    public TrainingSessionRepository(Context context, TrainingAppDbHelper helper) {
        this.context = context.getApplicationContext();
        this.dbHelper = helper;
    }

    public TrainingSession createTrainingSession(User user) {
        RuntimeExceptionDao<TrainingSession, Integer> trainingSessionDao = dbHelper.getTrainingSessionDao();
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setUser(user);
        trainingSession.setStartDate(new Date());
        trainingSession.setFinished(false);
        trainingSessionDao.create(trainingSession);
        return trainingSession;
    }
}
