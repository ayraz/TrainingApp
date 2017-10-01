package cz.nudz.www.trainingapp;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import cz.nudz.www.trainingapp.databinding.TrialActivityBinding;
import cz.nudz.www.trainingapp.training.Difficulty;
import cz.nudz.www.trainingapp.training.ParadigmType;
import cz.nudz.www.trainingapp.training.SequenceFragment;
import cz.nudz.www.trainingapp.training.TrainingActivity;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class TrialActivity extends AppCompatActivity implements SequenceFragment.SequenceFragmentListener {

    private TrialActivityBinding binding;

    public static void startActivity(Context context, @NonNull ParadigmType paradigmType) {
        Intent intent = new Intent(context, TrialActivity.class);
        intent.putExtra(KEY_PARADIGM, paradigmType.toString());
        // do not add activity to navigation stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.trial_activity);

        ParadigmType paradigmType = ParadigmType.valueOf(getIntent().getStringExtra(KEY_PARADIGM));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(binding.trialActivityFragmentContainer.getId(),
                SequenceFragment.newInstance(paradigmType, Difficulty.ONE, 3)); // TODO: replace with real values..
        fragmentTransaction.commit();
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        finish();
    }

    @Override
    public void onTrialFinished(int trialCount) {

    }
}
