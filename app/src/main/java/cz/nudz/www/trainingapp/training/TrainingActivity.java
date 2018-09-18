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
import cz.nudz.www.trainingapp.data.TrainingAppDbHelper;
import cz.nudz.www.trainingapp.data.Repository;
import cz.nudz.www.trainingapp.data.tables.Paradigm;
import cz.nudz.www.trainingapp.data.tables.Sequence;
import cz.nudz.www.trainingapp.data.tables.TrainingSession;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;
import cz.nudz.www.trainingapp.enums.Adjustment;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.main.MainActivity;
import cz.nudz.www.trainingapp.summary.SessionRecapFragment;
import cz.nudz.www.trainingapp.utils.Utils;

public class TrainingActivity extends BaseActivity implements
        TrainingFragment.TrainingFragmentListener,
        CountDownFragment.CountDownListener,
        QuestionnaireFragment.QuestionnaireListener,
        BadgeFragment.BadgeFragmentListener,
        ThankYouFragment.ThankYouFragmentListener {

    public static final String KEY_PARADIGM = "KEY_PARADIGM";
    public static final int DEFAULT_SEQUENCE_COUNT = 7;
    public static final int DEFAULT_TRIAL_COUNT = 20;

    private TrainingActivityBinding binding;
    private int containerId;
    private Repository repository;

    private ParadigmType paradigmType;
    // each paradigm starts with lowest difficulty.
    private Difficulty difficulty = Difficulty.ONE;
    private TrainingSession currentSession;
    private Paradigm paradigm;
    private Sequence sequence;
    private Date paradigmPauseStartTime;
    private int sequenceCount = 0;

    public static void startActivity(@NonNull final Context context) {
        Intent intent = new Intent(context, TrainingActivity.class);
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableImmersiveMode();

        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);
        containerId = binding.trainingActivityFragmentContainer.getId();
        repository = new Repository(this);

        paradigmType = ParadigmSet.getAt(0);

        currentSession = repository.startAndStoreTrainingSession();
        paradigm = repository.startAndStoreParadigm(currentSession, paradigmType);
        nextSequence();
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
            paradigm.setPauseDurationMillis(paradigmPauseDuration);
            repository.updateParadigm(paradigm);

            nextParadigm();
        }
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        sequenceCount += 1;
        repository.finishAndUpdateSequence(sequence);

        // PARADIGM FINISHED
        if (isParadigmFinished()) {
            repository.finishAndUpdateParadigm(paradigm);
            // SESSION FINISHED
            if (isTrainingFinished()) {
                repository.finishAndUpdateSession(currentSession);
                if (repository.doManySessionsExist()) {
                    BadgeFragment badgeFragment = new BadgeFragment();
                    badgeFragment.show(getSupportFragmentManager(), BadgeFragment.TAG);
                } else {
                    showQuestionnaire();
                }
            } else {
                paradigmPauseStartTime = new Date();
                // next cannot be null because end of training is handled above..
                showFragmentWithAnim(containerId, PauseFragment.newInstance(
                        ParadigmSet.getNext(paradigmType)), PauseFragment.TAG);
            }
        // SEQUENCE FINISHED
        } else if (sequenceCount < DEFAULT_SEQUENCE_COUNT) {
            Difficulty newDifficulty = Utils.adjustDifficulty(answers, difficulty);
            Adjustment adjustment = Adjustment.SAME;
            if (newDifficulty != null) {
                if (newDifficulty.ordinal() > difficulty.ordinal()) {
                    adjustment = Adjustment.RAISED;
                } else if (newDifficulty.ordinal() < difficulty.ordinal()) {
                    adjustment = Adjustment.LOWERED;
                }
                difficulty = newDifficulty;
            } else {
                // TODO: handle max level
            }
            showFragmentWithAnim(containerId, PauseFragment.newInstance(
                    paradigmType, adjustment, sequenceCount, newDifficulty), PauseFragment.TAG);
        }
    }

    @Override
    public void onTrialFinished(int trialCount) {
        // TODO remove after debug
        binding.trialCount.setText(String.format("Trial #: %s", String.valueOf(trialCount + 1)));
    }

    private boolean isTrainingFinished() {
        return ParadigmSet.getNext(paradigmType) == null;
    }

    private boolean isParadigmFinished() {
        return sequenceCount == DEFAULT_SEQUENCE_COUNT;
    }

    private void nextSequence() {
        sequence = repository.createAndStoreSequence(paradigm, difficulty);

        showFragment(containerId,
                TrainingFragment.newInstance(paradigmType, difficulty), TrainingFragment.TAG);
        // TODO remove after debug
        binding.seqCount.setText(String.format("Seq. #: %s", String.valueOf(sequenceCount + 1)));
    }

    private void nextParadigm() {
        ParadigmType next = ParadigmSet.getNext(paradigmType);
        if (next != null) {
            // reset counter
            sequenceCount = 0;
            paradigmType = next;
            difficulty = Difficulty.ONE;

            paradigm = repository.startAndStoreParadigm(currentSession, next);

            nextSequence();

            // TODO remove after debug
            binding.paradigmCount.setText(paradigmType.toString());
        } else {
            Utils.showAlertDialog(this, null, getString(R.string.errorNoParadigmsLeft));
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    public TrainingAppDbHelper getDbHelper() {
        return getHelper();
    }

    @Override
    public void onQuestionnairSubmission(int effort, int difficulty) {
        currentSession.setEffortAnswer(effort);
        currentSession.setDifficultyAnswer(difficulty);
        getDbHelper().getTrainingSessionDao().update(currentSession);

        // End training session.
        showFragmentWithAnim(containerId, ThankYouFragment.newInstance(false), ThankYouFragment.TAG);
    }

    @Override
    public void onOkClick() {
        showQuestionnaire();
    }

    private void showQuestionnaire() {
        if (ParadigmSet.getOperationMode() == ParadigmSet.OperationMode.TEST) {
            showFragmentWithAnim(containerId, ThankYouFragment.newInstance(true), ThankYouFragment.TAG);
        } else {
            showFragmentWithAnim(containerId, new QuestionnaireFragment(), QuestionnaireFragment.TAG);
        }
    }

    @Override
    public void returnToTraining() {
        TrainingActivity.super.onBackPressed();
    }

    @Override
    public void proceedToResults() {
        MainActivity.startActivity(this, SessionRecapFragment.TAG);
    }
}
