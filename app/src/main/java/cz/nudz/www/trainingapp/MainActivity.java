package cz.nudz.www.trainingapp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;

import cz.nudz.www.trainingapp.databinding.MainActivityBinding;
import cz.nudz.www.trainingapp.training.Paradigm;
import cz.nudz.www.trainingapp.training.TrainingActivity;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.mainActivityTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingActivity.startActivity(MainActivity.this, Paradigm.COLOR);
            }
        });

        //
        // EVENT HANDLERS
        //
        binding.mainActivityTrialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
