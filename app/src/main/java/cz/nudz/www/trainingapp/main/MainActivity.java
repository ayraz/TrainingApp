package cz.nudz.www.trainingapp.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.SessionManager;
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

        // make sure we have a logged in user before we can proceed with anything
        if (getSessionManager().checkLogin()) {
            setSupportActionBar(binding.appBar);
            binding.viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
            binding.tabLayout.setupWithViewPager(binding.viewPager);

            dataExporter = new DataExporter(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        dataExporter.export(getSessionManager().getUsername());
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
