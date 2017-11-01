package cz.nudz.www.trainingapp.trial;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TrialActivityBinding;
import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.TrainingFragment;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class TrialActivity extends BaseActivity implements TrainingFragment.SequenceFragmentListener {

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
                TrainingFragment.newInstance(paradigmType, Difficulty.ONE, 3)); // TODO: replace with real values..
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
