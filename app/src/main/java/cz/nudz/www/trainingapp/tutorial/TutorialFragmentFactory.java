package cz.nudz.www.trainingapp.tutorial;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.enums.ParadigmType;

/**
 * Created by aa250602 on 10/5/17.
 */

public class TutorialFragmentFactory {

    public static TutorialImageFragment createTutorialFragment(ParadigmType type, int tutorialPosition) {
        int drawableResId = 0;
        switch (tutorialPosition) {
            // start help
            case 0:
                return TutorialImageFragment.newInstance(
                        R.string.shapeParadigmStartHelp,
                        R.string.nextHelp,
                        null);
            // cue help
            case 1:
                switch (type) {
                    case SHAPE:
                        drawableResId = R.drawable.test_pic_cue;
                        break;
                    case COLOR:
                        break;
                    case POSITION:
                        break;
                }
                return TutorialImageFragment.newInstance(
                        R.string.cueHelp,
                        R.string.nextPreviousHelp,
                        drawableResId);
            // short pause
            case 2:
                return TutorialImageFragment.newInstance(
                        R.string.shortPause,
                        R.string.nextPreviousHelp,
                        R.drawable.test_pic_pause);
            // memory help
            case 3:
                switch (type) {
                    case SHAPE:
                        drawableResId = R.drawable.test_pic_items;
                        break;
                    case COLOR:
                        break;
                    case POSITION:
                        break;
                }
                return TutorialImageFragment.newInstance(
                        R.string.memorizeHelp,
                        R.string.nextPreviousHelp,
                        drawableResId
                );
            // longer pause
            case 4:
                return TutorialImageFragment.newInstance(
                        R.string.longerPause,
                        R.string.nextPreviousHelp,
                        R.drawable.test_pic_pause);
            // change detection
            case 5:
                switch (type) {
                    case SHAPE:
                        drawableResId = R.drawable.test_pic_change;
                        break;
                    case COLOR:
                        break;
                    case POSITION:
                        break;
                }
                return TutorialImageFragment.newInstance(
                        R.string.identificationHelp,
                        R.string.nextPreviousHelp,
                        drawableResId);
            // inter-trial pause
            case 6:
                return TutorialImageFragment.newInstance(
                        R.string.interTrialPauseHelp,
                        R.string.nextPreviousHelp,
                        R.drawable.test_pic_pause);
            // now you try
            case 7:
                return TutorialImageFragment.newInstance(
                        R.string.nowYouTryMessage,
                        R.string.nextPreviousHelp,
                        null);
        }
        return new TutorialImageFragment();
    }
}
