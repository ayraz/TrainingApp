package cz.nudz.www.trainingapp.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by aa250602 on 10/5/17.
 */

public class TutorialFragmentFactory {

    public static final int PAGE_COUNT_PER_PARADIGM = 8;
    public static final int TOTAL_PAGE_COUNT = PAGE_COUNT_PER_PARADIGM * ParadigmSet.size();

    public static TutorialImageFragment createTutorialFragment(int position) {
        ParadigmType paradigmType = ParadigmSet.getAt(position / PAGE_COUNT_PER_PARADIGM);
        position %= PAGE_COUNT_PER_PARADIGM;

        Integer drawableId = null;
        switch (position) {
            // start help
            case 0:
                return TutorialImageFragment.newInstance(
                        R.string.shapeParadigmStartHelp,
                        R.string.nextHelp,
                        null);
            // cue help
            case 1:
                return TutorialImageFragment.newInstance(
                        R.string.cueHelp,
                        R.string.nextPreviousHelp,
                        R.drawable.cue);
            // short pause
            case 2:
                return TutorialImageFragment.newInstance(
                        R.string.shortPause,
                        R.string.nextPreviousHelp,
                        R.drawable.pause);
            // memory help
            case 3:
                switch (paradigmType) {
                    case SHAPE:
                        drawableId = R.drawable.shape_memory_array;
                        break;
                    case COLOR:
                        drawableId = R.drawable.color_memory_array;
                        break;
                    case POSITION:
                        drawableId = R.drawable.position_memory_array;
                        break;
                }
                return TutorialImageFragment.newInstance(
                        R.string.memorizeHelp,
                        R.string.nextPreviousHelp,
                        drawableId
                );
            // longer pause
            case 4:
                return TutorialImageFragment.newInstance(
                        R.string.longerPause,
                        R.string.nextPreviousHelp,
                        R.drawable.pause);
            // change detection
            case 5:
                switch (paradigmType) {
                    case SHAPE:
                        drawableId = R.drawable.shape_test_array;
                        break;
                    case COLOR:
                        drawableId = R.drawable.color_test_array;
                        break;
                    case POSITION:
                        drawableId = R.drawable.position_test_array;
                        break;
                }
                return TutorialImageFragment.newInstance(
                        R.string.identificationHelp,
                        R.string.nextPreviousHelp,
                        drawableId);
            // inter-trial pause
            case 6:
                return TutorialImageFragment.newInstance(
                        R.string.interTrialPauseHelp,
                        R.string.nextPreviousHelp,
                        R.drawable.pause);
            // now you try
            case 7:
                return TutorialImageFragment.newInstance(
                        R.string.nowYouTryMessage,
                        R.string.nextPreviousHelp,
                        null);
        }
        throw new IllegalArgumentException(String.format(
                "Page properties do not match any setup; paradigm: %s, position: %d",
                paradigmType.toString(),
                position));
    }
}
