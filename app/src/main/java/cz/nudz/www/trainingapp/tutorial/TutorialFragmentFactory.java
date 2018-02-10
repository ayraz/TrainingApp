package cz.nudz.www.trainingapp.tutorial;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.MessageFragment;

/**
 * Created by aa250602 on 10/5/17.
 */

public class TutorialFragmentFactory {

    public static final int PAGE_COUNT_PER_PARADIGM = 8;
    public static final int TOTAL_PAGE_COUNT = PAGE_COUNT_PER_PARADIGM * ParadigmSet.size();

    public static Fragment createTutorialFragment(int position) {
        ParadigmType paradigmType = ParadigmSet.getAt(position / PAGE_COUNT_PER_PARADIGM);
        position %= PAGE_COUNT_PER_PARADIGM;

        Integer drawableId = null;
        switch (position) {
            // start help
            case 0:
                int startHelpText = 0;
                switch (paradigmType) {
                    case SHAPE:
                        startHelpText = R.string.shapeParadigmStartHelp;
                        break;
                    case COLOR:
                        startHelpText = R.string.colorParadigmStartHelp;
                        break;
                    case POSITION:
                        startHelpText = R.string.positionParadigmStartHelp;
                        break;
                }
                return TutorialMessageFragment.newInstance(startHelpText);
            // cue help
            case 1:
                return TutorialImageFragment.newInstance(
                        R.string.cueHelp,
                        R.drawable.cue);
            // short pause
            case 2:
                return TutorialImageFragment.newInstance(
                        R.string.shortPause,
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
                        drawableId
                );
            // longer pause
            case 4:
                return TutorialImageFragment.newInstance(
                        R.string.longerPause,
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
                        drawableId);
            // inter-trial pause
            case 6:
                return TutorialImageFragment.newInstance(
                        R.string.interTrialPauseHelp,
                        R.drawable.pause);
            // now you try
            case 7:
                return TutorialMessageFragment.newInstance(
                        R.string.nowYouTryMessage, R.string.tutorialPrepareForTrialText);
        }
        throw new IllegalArgumentException(String.format(
                "Page properties do not match any setup; paradigm: %s, position: %d",
                paradigmType.toString(),
                position));
    }
}
