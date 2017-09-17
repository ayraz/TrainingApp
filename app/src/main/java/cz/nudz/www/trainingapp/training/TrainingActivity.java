package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    public static void startActivity(Context context, Paradigm paradigm) {
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
        nextSequence();
    }

    @Override
    public void onBackPressed() {
        stopTrainingCallbacks();
        super.onBackPressed();
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
    public void onSequenceFinished(List<Boolean> answers) {
        if (sequenceCount == 0) {
            nextSequence();
        } else {
            if (sequenceCount < SEQUENCE_COUNT) {
                showFragment(PauseFragment.newInstance(currentParadigm, Adjustment.SAME), PauseFragment.TAG);
            }
            else if (isParadigmFinished()) {
                showFragment(PauseFragment.newInstance(currentParadigm, null), PauseFragment.TAG);
            }
        }
    }

    private boolean isParadigmFinished() {
        return sequenceCount == SEQUENCE_COUNT;
    }

    private void nextSequence() {
        showFragment(SequenceFragment.newInstance(currentParadigm, 1), SequenceFragment.TAG);
        sequenceCount += 1;
    }

    @Override
    public void onExpired() {
        finish();
    }

    @Override
    public void onContinue() {
        // NEXT SEQUENCE
        if (sequenceCount < SEQUENCE_COUNT) {
            nextSequence();
        }
        // NEXT PARADIGM
        else if (isParadigmFinished()) {
            nextParadigm();
        }
    }

    private void nextParadigm() {
        Paradigm next = TrainingApp.nextParadigm(currentParadigm);
        // TODO: handle end of training..
        if (next != null) {
            // reset counter
            sequenceCount = 0;
            showFragment(SequenceFragment.newInstance(next, 1), SequenceFragment.TAG);
        }
    }
}
