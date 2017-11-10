package cz.nudz.www.trainingapp.tutorial;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.databinding.TutorialPagerActivityBinding;
import cz.nudz.www.trainingapp.enums.ParadigmType;

public class TutorialPagerFragment extends Fragment {

    public static final String TAG = TutorialPagerFragment.class.getSimpleName();

    private ViewPager pager;
    private TutorialPagerAdapter pagerAdapter;
    private TutorialPagerActivityBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tutorial_pager_activity, container, false);

        pager = binding.tutorialActivityPager;
        pagerAdapter = new TutorialPagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                if (position + 1 == TutorialFragmentFactory.TOTAL_PAGE_COUNT) {
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

        return binding.getRoot();
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
            return TutorialFragmentFactory.TOTAL_PAGE_COUNT;
        }
    }
}
