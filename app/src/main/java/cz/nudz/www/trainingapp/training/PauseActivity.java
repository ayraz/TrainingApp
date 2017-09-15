package cz.nudz.www.trainingapp.training;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.PauseActivityBinding;

import static cz.nudz.www.trainingapp.training.TrainingActivity.KEY_PARADIGM;

public class PauseActivity extends AppCompatActivity implements CountDownFragment.OnCountDownListener {

    private static final String KEY_DIFFICULTY_STATE = "KEY_DIFFICULTY_STATE";
    private static final String KEY_PAUSE_TYPE = "KEY_PAUSE_TYPE";

    private PauseActivityBinding binding;
    private Paradigm currentParadigm;
    private boolean isSequencePause;
    private DifficultyAdjustment difficultyAdjustment;

    public static void startActivity(Context context, @NonNull Paradigm paradigm, boolean isSequencePause, @Nullable DifficultyAdjustment difficultyAdjustment) {
        Intent intent = new Intent(context, PauseActivity.class);
        intent.putExtra(KEY_PARADIGM, paradigm.toString());
        if (difficultyAdjustment != null)
            intent.putExtra(KEY_DIFFICULTY_STATE, difficultyAdjustment.toString());
        intent.putExtra(KEY_PAUSE_TYPE, isSequencePause);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pause_activity);

        currentParadigm = Paradigm.valueOf(getIntent().getStringExtra(KEY_PARADIGM));
        if (getIntent().hasExtra(KEY_PAUSE_TYPE))
            isSequencePause = getIntent().getBooleanExtra(KEY_PAUSE_TYPE, false);
        else
            throw new IllegalStateException("Pause type must be present.");

        if (getIntent().hasExtra(KEY_DIFFICULTY_STATE))
            difficultyAdjustment = DifficultyAdjustment.valueOf(getIntent().getStringExtra(KEY_DIFFICULTY_STATE));
        else if (isSequencePause)
            throw new IllegalStateException("Difficulty state must be present during sequence pause.");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        WarningFragment warningFragment = WarningFragment.newInstance(getHelpTextForParadigm());
        transaction.add(R.id.pauseActivityMessageContainer, warningFragment);

        CountDownFragment countDownFragment = CountDownFragment.newInstance(isSequencePause);
        transaction.add(R.id.pauseActivityCountDownContainer, countDownFragment);

        transaction.commit();
    }

    private int getHelpTextForParadigm() {
        if (isSequencePause) {
            // TODO: add real values
            switch (difficultyAdjustment) {
                case LOWERED:
                    return 0;
                case SAME:
                    return 0;
                case RAISED:
                    return 0;
            }
        } else {
            switch (currentParadigm) {
                case COLOR:
                    return R.string.colorParadigmStartHelp;
                case SHAPE:
                    return R.string.shapeParadigmStartHelp;
                case POSITION:
                    return R.string.positionParadigmStartHelp;
            }
        }
        return R.string.genericErrorMessage;
    }

    @Override
    public void onCountDownExpired() {
        finish();
    }

    @Override
    public void onContinueClicked() {
        TrainingActivity.startActivity(this, currentParadigm);
    }
}
