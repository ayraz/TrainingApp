package cz.nudz.www.trainingapp.trial;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TrialActivityBinding;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.TrainingFragment;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class TrialActivity extends BaseActivity implements TrainingFragment.TrainingFragmentListener {

    public static final String KEY_DIFFICULTY = "KEY_DIFFICULTY";

    private TrialActivityBinding binding;

    public static void startActivity(Context context, @NonNull ParadigmType paradigmType, @NonNull Difficulty difficulty) {
        Intent intent = new Intent(context, TrialActivity.class);
        intent.putExtra(KEY_PARADIGM, paradigmType.toString());
        intent.putExtra(KEY_DIFFICULTY, difficulty.toString());
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.trial_activity);

        ParadigmType paradigmType = ParadigmType.valueOf(getIntent().getStringExtra(KEY_PARADIGM));
        Difficulty difficulty = Difficulty.valueOf(getIntent().getStringExtra(KEY_DIFFICULTY));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(
                binding.trialActivityFragmentContainer.getId(),
                TrainingFragment.newInstance(paradigmType, difficulty, 3),
                TrainingFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        stopTrainingCallbacks();
        TrialActivity.super.onBackPressed();
    }

    private void stopTrainingCallbacks() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TrainingFragment.TAG);
        if (fragment != null) {
            // Unregister all handler callbacks to prevent unwanted navigation caused by postDelayed.
            ((TrainingFragment) fragment).removePendingCallbacks();
        }
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        finish();
    }

    @Override
    public void onTrialFinished(int trialCount) {

    }
}
