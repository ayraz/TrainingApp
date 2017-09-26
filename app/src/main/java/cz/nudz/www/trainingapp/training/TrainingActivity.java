package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.database.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.database.TrainingSessionRepository;
import cz.nudz.www.trainingapp.database.tables.Paradigm;
import cz.nudz.www.trainingapp.database.tables.Sequence;
import cz.nudz.www.trainingapp.database.tables.TrainingSession;
import cz.nudz.www.trainingapp.database.tables.User;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.utils.TrainingUtils;

public class TrainingActivity extends AppCompatActivity implements
        SequenceFragment.SequenceFragmentListener,
        CountDownFragment.CountDownListener,
        WarningFragment.WarningFragmentListener {

    public static final String KEY_PARADIGM = "KEY_PARADIGM";

    private static final int SEQUENCE_COUNT = 7;

    private TrainingActivityBinding binding;
    private int sequenceCount = 0;
    private SessionManager sessionManager;
    private String username;
    private TrainingApp applicationContext;
    private TrainingAppDbHelper dbHelper;

    private ParadigmType currentParadigmType;
    private Difficulty currentDifficulty = Difficulty.ONE;
    private TrainingSession currentSession;
    private Paradigm currentParadigm;
    private Sequence currentSequence;

    public static void startActivity(Context context, @NonNull ParadigmType paradigmType) {
        Intent intent = new Intent(context, TrainingActivity.class);
        intent.putExtra(KEY_PARADIGM, paradigmType.toString());
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        username = sessionManager.getUserDetails().get(SessionManager.KEY_USERNAME);

        applicationContext = (TrainingApp) getApplicationContext();
        dbHelper = applicationContext.getDbHelper();

        currentParadigmType = ParadigmType.valueOf(getIntent().getStringExtra(KEY_PARADIGM));
        // TODO: Each session/paradigm starts with lowest difficulty.
        showFragment(WarningFragment.newInstance(currentParadigmType, null), WarningFragment.TAG);

        // TODO remove after debug
        binding.paradigm.setText(currentParadigmType.toString());
    }

    @Override
    protected void onDestroy() {
        applicationContext.releaseHelper();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.quitTrainingDialogTitle)
                .setMessage(R.string.quitTrainingDialogMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopTrainingCallbacks();
                        TrainingActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
        .create().show();
    }

    private void stopTrainingCallbacks() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SequenceFragment.TAG);
        if (fragment != null) {
            // Unregister all handler callbacks to prevent unwanted navigation caused by postDelayed.
            ((SequenceFragment) fragment).removePendingCallbacks();
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.trainingActivityFragmentContainer.getId(), fragment, tag);
        transaction.commit();
    }

    @Override
    public void startTraining() {
        try {
            currentSession = initTainingSession();
            currentParadigm = initParadigm(currentSession);
            nextSequence();
        } catch (LoginException e) {
            Log.e(TrainingActivity.class.getSimpleName(), e.getMessage());
            TrainingUtils.showErrorDialog(this, null, getString(R.string.errorNotLoggedIn));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.startActivity(TrainingActivity.this);
                }
            }, 3000);
        }
    }

    private Paradigm initParadigm(TrainingSession trainingSession) {
        RuntimeExceptionDao<Paradigm, Integer> paradigmDao = dbHelper.getParadigmDao();
        Paradigm paradigm = new Paradigm();
        paradigm.setTrainingSession(trainingSession);
        paradigm.setStartDate(new Date());
        paradigm.setParadigmType(currentParadigmType);
        paradigmDao.create(paradigm);
        return paradigm;
    }

    private TrainingSession initTainingSession() throws LoginException {
        RuntimeExceptionDao<User, String> userDao = dbHelper.getUserDao();
        User user = userDao.queryForId(username);
        if (user == null) {
            throw new LoginException("User is not logged in yet on training screen!");
        } else {
            return new TrainingSessionRepository(this, dbHelper).createTrainingSession(user);
        }
    }

    @Override
    public void goBack() {
        super.onBackPressed();
    }

    @Override
    public void onExpired() {
        finish();
    }

    @Override
    public void onContinue() {
        if (sequenceCount < SEQUENCE_COUNT) {
            nextSequence();
        }
        else if (isParadigmFinished()) {
            nextParadigm();
        }
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        sequenceCount += 1;

        if (sequenceCount < SEQUENCE_COUNT) {
            Difficulty newDifficulty = TrainingUtils.adjustDifficulty(answers, currentDifficulty);
            Adjustment adjustment = Adjustment.SAME;
            if (newDifficulty != null) {
                if (newDifficulty.ordinal() > currentDifficulty.ordinal()) {
                    adjustment = Adjustment.RAISED;
                }
                else if (newDifficulty.ordinal() < currentDifficulty.ordinal()) {
                    adjustment = Adjustment.LOWERED;
                }
                currentDifficulty = newDifficulty;
            } else {
                // TODO: handle max level
            }
            showFragment(PauseFragment.newInstance(currentParadigmType, adjustment), PauseFragment.TAG);
        } else if (isTrainingFinished()) {
            // TODO: handle end of training..

        } else if (isParadigmFinished()) {
            // next cannot be null because end of training is handled above..
            showFragment(PauseFragment.newInstance(TrainingApp.nextParadigm(currentParadigmType), null), PauseFragment.TAG);
        }
    }

    @Override
    public void onTrialFinished(int trialCount) {
        // TODO remove after debug
        binding.trialCount.setText(String.format("Trial #: %s", String.valueOf(trialCount+1)));
    }

    private boolean isTrainingFinished() {
        return TrainingApp.nextParadigm(currentParadigmType) == null;
    }

    private boolean isParadigmFinished() {
        return sequenceCount == SEQUENCE_COUNT;
    }

    private void nextSequence() {
        currentSequence = initSequence();

        showFragment(SequenceFragment.newInstance(currentParadigmType, currentDifficulty), SequenceFragment.TAG);
        // TODO remove after debug
        binding.seqCount.setText(String.format("Seq. #: %s", String.valueOf(sequenceCount+1)));
    }

    private Sequence initSequence() {
        RuntimeExceptionDao<Sequence, Integer> sequenceDao = dbHelper.getSequenceDao();
        Sequence sequence = new Sequence();
        sequence.setParadigm(currentParadigm);
        sequence.setStartDate(new Date());
        sequence.setDifficulty(currentDifficulty);
        sequenceDao.create(sequence);
        return sequence;
    }

    private void nextParadigm() {
        ParadigmType next = TrainingApp.nextParadigm(currentParadigmType);
        if (next != null) {
            // reset counter
            sequenceCount = 0;
            currentParadigmType = next;
            currentDifficulty = Difficulty.ONE;
            nextSequence();

            // TODO remove after debug
            binding.paradigm.setText(currentParadigmType.toString());
        } else {
            TrainingUtils.showErrorDialog(this, null, getString(R.string.errorNoParadigmsLeft));
        }
    }

    public Sequence getCurrentSequence() {
        return currentSequence;
    }

    public TrainingAppDbHelper getDbHelper() {
        return dbHelper;
    }
}
