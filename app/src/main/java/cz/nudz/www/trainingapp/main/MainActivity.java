package cz.nudz.www.trainingapp.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
import cz.nudz.www.trainingapp.trial.TrialSelectionFragment;
import cz.nudz.www.trainingapp.databinding.MainActivityBinding;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.TrainingActivity;
import cz.nudz.www.trainingapp.tutorial.TutorialPagerActivity;

public class MainActivity extends BaseActivity {

    private MainActivityBinding binding;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_data:
                DataExporter dataExporter = new DataExporter(this);
                dataExporter.export(getSessionManager().getUserDetails().get(SessionManager.KEY_USERNAME));
                return true;
            case R.id.action_logout:
                getSessionManager().logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        setSupportActionBar(binding.appBar);

        binding.mainActivityTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingActivity.startActivity(MainActivity.this, ParadigmType.COLOR);
            }
        });

        binding.mainActivityTutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialPagerActivity.startActivity(MainActivity.this);
            }
        });

        binding.mainActivityTrialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrialSelectionFragment trialSelectionFragment = new TrialSelectionFragment();
                trialSelectionFragment.show(getSupportFragmentManager(), "");
            }
        });
    }
}
