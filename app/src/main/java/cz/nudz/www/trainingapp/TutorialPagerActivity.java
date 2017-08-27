package cz.nudz.www.trainingapp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import cz.nudz.www.trainingapp.databinding.TutorialPagerActivityBinding;

public class TutorialPagerActivity extends AppCompatActivity {

    private static final int PAGE_COUNT = 8;

    private ViewPager pager;
    private TutorialPagerAdapter pagerAdapter;
    private TutorialPagerActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.tutorial_pager_activity);

        pager = binding.tutorialActivityPager;
        pagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                if (position + 1 == PAGE_COUNT) {
                    binding.tutorialActivityNextBtn.setEnabled(false);
                } else {
                    binding.tutorialActivityNextBtn.setEnabled(true);
                }

                if (position == 0) {
                    binding.tutorialActivityPrevBtn.setEnabled(false);
                } else {
                    binding.tutorialActivityPrevBtn.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });

        binding.tutorialActivityPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(getCurrentPage() - 1);
            }
        });

        binding.tutorialActivityNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(getCurrentPage() + 1);
            }
        });
    }

    private int getCurrentPage() {
        return pager.getCurrentItem();
    }

    private class TutorialPagerAdapter extends FragmentStatePagerAdapter {

        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TutorialFragment().newInstance(
                            (R.string.tutorialFragmentStartHelp),
                            (R.string.tutorialFragmentNextHelp));
                case 1:
                    return new TutorialFragment().newInstance(
                            (R.string.tutorialFragmentCueHelp),
                            (R.string.tutorialFragmentNextPreviousHelp));
                case 2:
                    return new TutorialFragment().newInstance(
                            (R.string.tutorialFragmentSmallPause),
                            (R.string.tutorialFragmentNextPreviousHelp));

            }
            return new TutorialFragment();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}