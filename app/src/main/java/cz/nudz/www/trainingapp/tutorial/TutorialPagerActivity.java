package cz.nudz.www.trainingapp.tutorial;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialPagerActivityBinding;
import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.enums.ParadigmType;

public class TutorialPagerActivity extends BaseActivity {

    private static final int PAGE_COUNT = 8;

    private ViewPager pager;
    private TutorialPagerAdapter pagerAdapter;
    private TutorialPagerActivityBinding binding;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TutorialPagerActivity.class);
        context.startActivity(intent);
    }

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

        binding.tutorialActivityPrevBtn.setOnClickListener(v -> pager.setCurrentItem(getCurrentPage() - 1));

        binding.tutorialActivityNextBtn.setOnClickListener(v -> pager.setCurrentItem(getCurrentPage() + 1));
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
            // TODO: remove hardcoded paradigm type
            return TutorialFragmentFactory.createTutorialFragment(ParadigmType.SHAPE, position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
