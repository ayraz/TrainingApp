package cz.nudz.www.trainingapp.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.DataExporter;
import cz.nudz.www.trainingapp.data.Repository;
import cz.nudz.www.trainingapp.databinding.MainActivityBinding;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.login.SignupFragment;
import cz.nudz.www.trainingapp.preferences.PreferenceActivity;
import cz.nudz.www.trainingapp.preferences.PreferenceFragment;
import cz.nudz.www.trainingapp.summary.PerformanceSummaryFragment;
import cz.nudz.www.trainingapp.summary.SessionRecapFragment;
import cz.nudz.www.trainingapp.training.CountDownFragment;
import cz.nudz.www.trainingapp.training.TrainingActivity;
import cz.nudz.www.trainingapp.training.TrainingFragment;
import cz.nudz.www.trainingapp.training.MessageFragment;
import cz.nudz.www.trainingapp.trial.TrialSelectionFragment;
import cz.nudz.www.trainingapp.tutorial.TutorialPagerFragment;
import cz.nudz.www.trainingapp.utils.Utils;

public class MainActivity extends BaseActivity implements
        TrialSelectionFragment.OnTrialSelectedListener,
        TrainingFragment.TrainingFragmentListener,
        MessageFragment.MessageFragmentListener,
        CountDownFragment.CountDownListener,
        SignupFragment.SignupListener {

    private static final String KEY_ACTIVE_OPTION_POS = "KEY_ACTIVE_OPTION_POS";
    private static final String KEY_INITIAL_FRAGMENT = "KEY_INITIAL_FRAGMENT";

    private MainActivityBinding binding;
    private DataExporter dataExporter;
    private MenuCardAdapter menuCardAdapter;
    private int containerId;

    public static void startActivity(Context context, String initialFragmentTag) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_INITIAL_FRAGMENT, initialFragmentTag);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_data:
                if (DataExporter.verifyStoragePermissions(this)) {
                    dataExporter.export(getSessionManager().getUsername());
                }
                return true;
            case R.id.action_settings:
                if (!isFragmentShown(PreferenceFragment.TAG)) {
                    PreferenceActivity.startActivity(this);
                }
                return true;
            case R.id.action_logout:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAppInLockTaskMode()) {
                    stopLockTask();
                }
                getSessionManager().logoutUser();
                return true;
            case R.id.action_create_user: {
                final SignupFragment signupFragment = new SignupFragment();
                signupFragment.show(getSupportFragmentManager(), SignupFragment.TAG);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);

        boolean isAdmin = getPreferenceManager().getIsAdminSession();
        menu.findItem(R.id.action_settings).setVisible(isAdmin);
        menu.findItem(R.id.action_create_user).setVisible(isAdmin);

        getSupportActionBar().setLogo(R.drawable.icons8_filter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        containerId = binding.fragmentContainer.getId();

        dataExporter = new DataExporter(this, new DataExporter.DataExportListener() {
            @Override
            public void onExportStart() {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onExportFinish() {
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onExportProgressUpdate(Integer progress) {
                binding.progressBar.setProgress(progress);
            }
        });

        // make sure we have a logged in user before we can proceed with anything
        if (getSessionManager().checkLogin()) {
            setSupportActionBar(binding.appBar);
            menuCardAdapter = new MenuCardAdapter(this, optionStringId -> {
                // unlock orientation if it was locked
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    FragmentManager.BackStackEntry lastEntry = getLastFragment();
                    if (lastEntry.getName().equals(TrainingFragment.TAG)
                        || lastEntry.getName().equals(CountDownFragment.TAG)) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                boolean isAdmin = getPreferenceManager().getIsAdminSession();
                switch (optionStringId) {
                    case R.string.sideMenuOptionIntro:
                        showFragmentWithAnim(containerId, new HomeFragment(), HomeFragment.TAG);
                        break;
                    case R.string.sideMenuOptionTraining:
                        ParadigmSet.setOperationMode(ParadigmSet.OperationMode.TRAINING);
                        showFragmentWithAnim(containerId, MessageFragment.newInstance(ParadigmSet.getAt(0)), MessageFragment.TAG);
                        break;
                    case R.string.sideMenuOptionTutorial:
                        ParadigmSet.setOperationMode(isAdmin ? ParadigmSet.OperationMode.ALL : ParadigmSet.OperationMode.TRAINING);
                        showFragmentWithAnim(containerId, new TutorialPagerFragment(), TutorialPagerFragment.TAG);
                        break;
                    case R.string.sideMenuOptionTrial:
                        ParadigmSet.setOperationMode(isAdmin ? ParadigmSet.OperationMode.ALL : ParadigmSet.OperationMode.TRAINING);
                        showFragmentWithAnim(containerId, new TrialSelectionFragment(), TrialSelectionFragment.TAG);
                        break;
                    case R.string.lastSessionPerformanceOptionTitle:
                        showFragmentWithAnim(containerId, new SessionRecapFragment(), SessionRecapFragment.TAG);
                        break;
                    case R.string.allSessionsPerformanceOptionTitle:
                        showFragmentWithAnim(containerId, new PerformanceSummaryFragment(), PerformanceSummaryFragment.TAG);
                        break;
                    case R.string.sideMenuOptionTest:
                        ParadigmSet.setOperationMode(ParadigmSet.OperationMode.TEST);
                        showFragmentWithAnim(containerId, MessageFragment.newInstance(ParadigmSet.getAt(0)), MessageFragment.TAG);
                        break;
                }
            });
            binding.menuList.setAdapter(menuCardAdapter);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.menuList.setLayoutManager(layoutManager);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isAppInLockTaskMode()) {
                startLockTask();
            }
            if (getIntent().hasExtra(KEY_INITIAL_FRAGMENT)) {
                switch (getIntent().getStringExtra(KEY_INITIAL_FRAGMENT)) {
                    case SessionRecapFragment.TAG:
                        menuCardAdapter.setActiveOptionPosition(Arrays.asList(2, 0));
                        showFragmentWithAnim(containerId, new SessionRecapFragment(), SessionRecapFragment.TAG);
                        break;
                }
            } else if (savedInstanceState == null) {
                // navigate to welcome fragment
                menuCardAdapter.setActiveOptionPosition(Arrays.asList(0, 0));
                showFragmentWithAnim(containerId, new HomeFragment(), HomeFragment.TAG);
            }
        }
    }

    private FragmentManager.BackStackEntry getLastFragment() {
        return getSupportFragmentManager()
                .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (menuCardAdapter != null) {
            outState.putIntegerArrayList(KEY_ACTIVE_OPTION_POS,
                    new ArrayList<>(menuCardAdapter.getActiveOptionPosition()));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        menuCardAdapter.setActiveOptionPosition(savedInstanceState.getIntegerArrayList(KEY_ACTIVE_OPTION_POS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataExporter.cancel();
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
        showFragmentWithAnimAndHistory(binding.fragmentContainer.getId(),
                TrainingFragment.newInstance(paradigmType, difficulty,
                        getPreferenceManager().getTrialCount(),
                        getPreferenceManager().getPresentationTime()),
                TrainingFragment.TAG);
    }

    @Override
    public void onSequenceFinished(List<Boolean> answers) {
        getSupportFragmentManager().popBackStack();
        Utils.showSequenceFeedback(answers, getPreferenceManager().getTrialCount(), this,
                getSupportFragmentManager().findFragmentByTag(getLastFragment().getName()).getView());
    }

    @Override
    public void startTraining() {
        showFragmentWithAnimAndHistory(containerId,
                CountDownFragment.newInstance(10 * 1000,
                        getString(R.string.trainingStartsInTitle),
                        getString(R.string.startImmediatelyBtnText)),
                CountDownFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        // do nothing, there is a logout option for leaving the app
    }

    @Override
    public void onExpired() {
        navigateToTrainingActivity();
    }

    @Override
    public void onContinue() {
        navigateToTrainingActivity();
    }

    private void navigateToTrainingActivity() {
        TrainingActivity.startActivity(this);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onUserCreated(String username) {
        // create and store new user
        Repository.createUser(username, getHelper().getUserDao());
    }
}
