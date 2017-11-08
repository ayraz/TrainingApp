package cz.nudz.www.trainingapp.main;

import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.DataExporter;
import cz.nudz.www.trainingapp.databinding.MainActivityBinding;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.summary.PerformanceSummaryFragment;
import cz.nudz.www.trainingapp.summary.SessionRecapFragment;
import cz.nudz.www.trainingapp.training.TrainingFragment;
import cz.nudz.www.trainingapp.trial.TrialSelectionFragment;
import cz.nudz.www.trainingapp.utils.CollectionUtils;

public class MainActivity extends BaseActivity implements
        TrialSelectionFragment.OnTrialSelectedListener,
        TrainingFragment.TrainingFragmentListener {

    private static final String KEY_ACTIVE_OPTION_POS = "KEY_ACTIVE_OPTION_POS";

    private MainActivityBinding binding;
    private DataExporter dataExporter;
    private MenuCardAdapter menuCardAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_data:
                if (DataExporter.verifyStoragePermissions(this)) {
                    dataExporter.export(getSessionManager().getUsername());
                }
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
        dataExporter = new DataExporter(this);

        // make sure we have a logged in user before we can proceed with anything
        if (getSessionManager().checkLogin()) {
            setSupportActionBar(binding.appBar);

            menuCardAdapter = new MenuCardAdapter(this, optionStringId -> {
                switch (optionStringId) {
                    case R.string.introOptionTitle:
                        showFragment(binding.fragmentContainer.getId(), new HomeFragment(), HomeFragment.TAG);
                        break;
                    case R.string.trainingOptionTitle:
                        break;
                    case R.string.tutorialOptionTitle:
                        break;
                    case R.string.trialOptionTitle:
                        showFragment(binding.fragmentContainer.getId(), new TrialSelectionFragment(), TrialSelectionFragment.TAG);
                        break;
                    case R.string.lastSessionPerformanceOptionTitle:
                        showFragment(binding.fragmentContainer.getId(), new SessionRecapFragment(), SessionRecapFragment.TAG);
                        break;
                    case R.string.allSessionsPerformanceOptionTitle:
                        showFragment(binding.fragmentContainer.getId(), new PerformanceSummaryFragment(), PerformanceSummaryFragment.TAG);
                        break;
                }
            });
            binding.menuList.setAdapter(menuCardAdapter);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.menuList.setLayoutManager(layoutManager);

            // navigate to welcome fragment via fake click
            if (savedInstanceState == null) {
                binding.menuList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((MenuCardAdapter) binding.menuList.getAdapter()).clickHome();
                        binding.menuList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(KEY_ACTIVE_OPTION_POS, new ArrayList<>(menuCardAdapter.getActiveOptionPosition()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        menuCardAdapter.setActiveOptionPosition(savedInstanceState.getIntegerArrayList(KEY_ACTIVE_OPTION_POS));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            // if permissions are not granted, leave...
            if (result != PackageManager.PERMISSION_GRANTED) return;
        }
        dataExporter.export(getSessionManager().getUsername());
    }

    @Override
    public void onTrialSelected(ParadigmType paradigmType, Difficulty difficulty) {

        showAndStackFragment(binding.fragmentContainer.getId(),
                TrainingFragment.newInstance(paradigmType, difficulty, 3),
                TrainingFragment.TAG);
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        getSupportFragmentManager().popBackStack();
    }
}
