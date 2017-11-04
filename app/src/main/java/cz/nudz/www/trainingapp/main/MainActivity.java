package cz.nudz.www.trainingapp.main;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.data.DataExporter;
import cz.nudz.www.trainingapp.databinding.MainActivityBinding;

public class MainActivity extends BaseActivity {

    private MainActivityBinding binding;
    private DataExporter dataExporter;

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

            MenuCardAdapter menuCardAdapter = new MenuCardAdapter(this, optionStringId -> {
                switch (optionStringId) {
                    case R.string.introOptionTitle:
                        showFragment(new HomeFragment(), HomeFragment.TAG);
                        break;
                    case R.string.trainingOptionTitle:
                        break;
                    case R.string.tutorialOptionTitle:
                        break;
                    case R.string.trialOptionTitle:
                        break;
                    case R.string.lastSessionPerformanceOptionTitle:
                        break;
                    case R.string.allSessionsPerformanceOptionTitle:
                        break;
                }
            });
            binding.menuList.setAdapter(menuCardAdapter);
            binding.menuList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            // if permissions are not granted, leave...
            if (result != PackageManager.PERMISSION_GRANTED) return;
        }
        dataExporter.export(getSessionManager().getUsername());
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), fragment, tag);
        transaction.commit();
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ModeSelectionFragment();
                case 1:
                default:
                    return new PerformanceSummaryFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.modeSelectionTitle);
                case 1:
                default:
                    return getString(R.string.performanceSummaryTitle);
            }
        }
    }

}
