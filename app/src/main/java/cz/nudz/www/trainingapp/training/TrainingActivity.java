package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.data.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.data.TrainingRepository;
import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.data.tables.Sequence;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.enums.Adjustment;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.utils.Utils;

public class TrainingActivity extends BaseActivity implements
        TrainingFragment.TrainingFragmentListener,
        CountDownFragment.CountDownListener {

    public static final String KEY_PARADIGM = "KEY_PARADIGM";
    public static final int DEFAULT_SEQUENCE_COUNT = 7;
    public static final int DEFAULT_TRIAL_COUNT = 20;

    private TrainingActivityBinding binding;
    private int sequenceCount = 0;
    private String username;
    private TrainingRepository trainingRepository;

    private ParadigmType currentParadigmType;
    // each paradigm starts with lowest difficulty.
    private Difficulty currentDifficulty = Difficulty.ONE;
    private TrainingSession currentSession;
    private Paradigm currentParadigm;
    private Sequence currentSequence;
    private Date paradigmPauseStartTime;
    private int containerId;

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

        enableImmersiveMode();

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        username = getSessionManager().getUserDetails().get(SessionManager.KEY_USERNAME);
        trainingRepository = new TrainingRepository(this, getDbHelper());
        currentParadigmType = ParadigmType.valueOf(getIntent().getStringExtra(KEY_PARADIGM));
        containerId = binding.trainingActivityFragmentContainer.getId();

        currentSession = trainingRepository.startAndStoreTrainingSession(username);
        currentParadigm = trainingRepository.startAndStoreParadigm(currentSession, currentParadigmType);
        nextSequence();

        // TODO remove after debug
        binding.paradigm.setText(currentParadigmType.toString());
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.quitTrainingDialogTitle)
                .setMessage(R.string.quitTrainingDialogMessage)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    stopTrainingCallbacks();
                    TrainingActivity.super.onBackPressed();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void stopTrainingCallbacks() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TrainingFragment.TAG);
        if (fragment != null) {
            // Unregister all handler callbacks to prevent unwanted navigation caused by postDelayed.
            ((TrainingFragment) fragment).removePendingCallbacks();
        }
    }

    @Override
    public void onExpired() {
        finish();
    }

    @Override
    public void onContinue() {
        if (sequenceCount < DEFAULT_SEQUENCE_COUNT) {
            nextSequence();
        } else if (isParadigmFinished()) {
            long paradigmPauseDuration = (new Date()).getTime() - paradigmPauseStartTime.getTime();
            currentParadigm.setPauseDurationMillis(paradigmPauseDuration);
            trainingRepository.updateParadigm(currentParadigm);

            nextParadigm();
        }
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        sequenceCount += 1;
        trainingRepository.finishAndUpdateSequence(currentSequence);

        // PARADIGM FINISHED
        if (isParadigmFinished()) {
            trainingRepository.finishAndUpdateParadigm(currentParadigm);
            // SESSION FINISHED
            if (isTrainingFinished()) {
                trainingRepository.finishAndUpdateSession(currentSession);
                super.onBackPressed();
                // TODO: handle end of training properly..
            } else {
                paradigmPauseStartTime = new Date();
                // next cannot be null because end of training is handled above..
                showFragment(containerId, PauseFragment.newInstance(ParadigmSet.getNext(currentParadigmType), null), PauseFragment.TAG);
            }
        // SEQUENCE FINISHED
        } else if (sequenceCount < DEFAULT_SEQUENCE_COUNT) {
            Difficulty newDifficulty = Utils.adjustDifficulty(answers, currentDifficulty);
            Adjustment adjustment = Adjustment.SAME;
            if (newDifficulty != null) {
                if (newDifficulty.ordinal() > currentDifficulty.ordinal()) {
                    adjustment = Adjustment.RAISED;
                } else if (newDifficulty.ordinal() < currentDifficulty.ordinal()) {
                    adjustment = Adjustment.LOWERED;
                }
                currentDifficulty = newDifficulty;
            } else {
                // TODO: handle max level
            }
            showFragment(containerId, PauseFragment.newInstance(currentParadigmType, adjustment), PauseFragment.TAG);
        }
    }

    @Override
    public void onTrialFinished(int trialCount) {
        // TODO remove after debug
        binding.trialCount.setText(String.format("Trial #: %s", String.valueOf(trialCount + 1)));
    }

    private boolean isTrainingFinished() {
        return ParadigmSet.getNext(currentParadigmType) == null;
    }

    private boolean isParadigmFinished() {
        return sequenceCount == DEFAULT_SEQUENCE_COUNT;
    }

    private void nextSequence() {
        currentSequence = trainingRepository.startAndStoreSequence(currentParadigm, currentDifficulty);

        showFragment(containerId, TrainingFragment.newInstance(currentParadigmType, currentDifficulty), TrainingFragment.TAG);
        // TODO remove after debug
        binding.seqCount.setText(String.format("Seq. #: %s", String.valueOf(sequenceCount + 1)));
    }

    private void nextParadigm() {
        ParadigmType next = ParadigmSet.getNext(currentParadigmType);
        if (next != null) {
            // reset counter
            sequenceCount = 0;
            currentParadigmType = next;
            currentDifficulty = Difficulty.ONE;

            currentParadigm = trainingRepository.startAndStoreParadigm(currentSession, next);

            nextSequence();

            // TODO remove after debug
            binding.paradigm.setText(currentParadigmType.toString());
        } else {
            Utils.showErrorDialog(this, null, getString(R.string.errorNoParadigmsLeft));
        }
    }

    public Sequence getCurrentSequence() {
        return currentSequence;
    }

    public TrainingAppDbHelper getDbHelper() {
        return getHelper();
    }
}
