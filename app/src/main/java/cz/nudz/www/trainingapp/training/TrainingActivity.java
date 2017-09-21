package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.databinding.TrainingActivityBinding;

public class TrainingActivity extends AppCompatActivity
        implements SequenceFragment.SequenceFragmentListener, CountDownFragment.CountDownListener {

    public static final String KEY_PARADIGM = "KEY_PARADIGM";

    private static final int SEQUENCE_COUNT = 7;

    private TrainingActivityBinding binding;
    private Paradigm currentParadigm;
    private int sequenceCount;

    public static void startActivity(Context context, @NonNull Paradigm paradigm) {
        Intent intent = new Intent(context, TrainingActivity.class);
        intent.putExtra(KEY_PARADIGM, paradigm.toString());
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.training_activity);

        currentParadigm = Paradigm.valueOf(getIntent().getStringExtra(KEY_PARADIGM));
        // TODO: Each session/paradigm starts with lowest difficulty.
        showFragment(PauseFragment.newInstance(currentParadigm, null), PauseFragment.TAG);

        // TODO remove after debug
        binding.paradigm.setText(currentParadigm.toString());
        binding.seqCount.setText(String.format("Seq. #: %s", String.valueOf(0)));
        binding.trialCount.setText(String.format("Trial #: %s", String.valueOf(0)));
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
    public void onExpired() {
        finish();
    }

    @Override
    public void onContinue() {
        // NEXT SEQUENCE
        if (sequenceCount < SEQUENCE_COUNT) {
            runSequence();
        }
        // NEXT PARADIGM
        else if (isParadigmFinished()) {
            nextParadigm();
        }
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        if (isFirstSequence()) {
            runSequence();
        } else {
            if (sequenceCount < SEQUENCE_COUNT) {
                showFragment(PauseFragment.newInstance(currentParadigm, Difficulty.SAME), PauseFragment.TAG);
            } else if (isTrainingFinished()) {
                // TODO: handle end of training..
            } else if (isParadigmFinished()) {
                showFragment(PauseFragment.newInstance(TrainingApp.nextParadigm(currentParadigm), null), PauseFragment.TAG);
            }
        }
    }

    @Override
    public void onTrialFinished(int trialCount) {
        // TODO remove after debug
        binding.trialCount.setText(String.format("Trial #: %s", String.valueOf(trialCount+1)));
    }

    private boolean isFirstSequence() {
        return sequenceCount == 0 && TrainingApp.indexOfParadigm(currentParadigm) == 0;
    }

    private boolean isTrainingFinished() {
        return TrainingApp.nextParadigm(currentParadigm) == null;
    }

    private boolean isParadigmFinished() {
        return sequenceCount == SEQUENCE_COUNT;
    }

    private void runSequence() {
        showFragment(SequenceFragment.newInstance(currentParadigm, 1), SequenceFragment.TAG);
        sequenceCount += 1;

        // TODO remove after debug
        binding.seqCount.setText(String.format("Seq. #: %s", String.valueOf(sequenceCount)));
    }

    private void nextParadigm() {
        Paradigm next = TrainingApp.nextParadigm(currentParadigm);
        if (next != null) {
            // reset counter
            sequenceCount = 0;
            currentParadigm = next;
            runSequence();

            // TODO remove after debug
            binding.paradigm.setText(currentParadigm.toString());
        }
    }
}
