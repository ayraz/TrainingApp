package cz.nudz.www.trainingapp.tutorial;

import android.support.v4.app.Fragment;

import cz.nudz.www.trainingapp.ParadigmSet;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.TrainingApp;
import cz.nudz.www.trainingapp.enums.Difficulty;
import cz.nudz.www.trainingapp.enums.ParadigmType;
import cz.nudz.www.trainingapp.training.CountDownFragment;
import cz.nudz.www.trainingapp.training.TrainingFragment;

/**
 * Created by aa250602 on 10/5/17.
 */

public class TutorialFragmentFactory {

    public static final int PAGE_COUNT_PER_PARADIGM = 10;
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
                        startHelpText = R.string.startHelpShapeParadigm;
                        break;
                    case COLOR:
                        startHelpText = R.string.startHelpColorParadigm;
                        break;
                    case POSITION:
                        startHelpText = R.string.startHelpPositionParadigm;
                        break;
                }
                return TutorialMessageFragment.newInstance(startHelpText);
            // cue help
            case 1:
                int cueHelp = 0;
                switch (paradigmType) {
                    case SHAPE:
                        cueHelp = R.string.cueHelpShapeParadigm;
                        break;
                    case COLOR:
                        cueHelp = R.string.cueHelpColorParadigm;
                        break;
                    case POSITION:
                        cueHelp = R.string.cueHelpPositionParadigm;
                        break;
                }
                return TutorialImageFragment.newInstance(
                        cueHelp,
                        R.drawable.cue);
            // short pause
            case 2:
                return TutorialImageFragment.newInstance(
                        R.string.shortPause,
                        R.drawable.pause);
            // memory help
            case 3:
                int memorizeHelp = 0;
                switch (paradigmType) {
                    case SHAPE:
                        drawableId = R.drawable.shape_memory_array;
                        memorizeHelp = R.string.memorizeShapeHelp;
                        break;
                    case COLOR:
                        drawableId = R.drawable.color_memory_array;
                        memorizeHelp = R.string.memorizeColorHelp;
                        break;
                    case POSITION:
                        drawableId = R.drawable.position_memory_array;
                        memorizeHelp = R.string.memorizePositionHelp;
                        break;
                }
                return TutorialImageFragment.newInstance(
                        memorizeHelp,
                        drawableId
                );
            // longer pause
            case 4:
                return TutorialImageFragment.newInstance(
                        R.string.longerPause,
                        R.drawable.pause);
            // change detection
            case 5:
                int identificationHelp = 0;
                switch (paradigmType) {
                    case SHAPE:
                        drawableId = R.drawable.shape_test_array;
                        identificationHelp = R.string.identificationHelpShapeParadigm;
                        break;
                    case COLOR:
                        drawableId = R.drawable.color_test_array;
                        identificationHelp = R.string.identificationHelpColorParadigm;
                        break;
                    case POSITION:
                        drawableId = R.drawable.position_test_array;
                        identificationHelp = R.string.identificationHelpPositionParadigm;
                        break;
                }
                return TutorialImageFragment.newInstance(
                        identificationHelp,
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
            case 8:
                return CountDownFragment.newInstance(5 * 1000,
                        TrainingApp.getContext().getString(R.string.trainingStartsInTitle),
                        TrainingApp.getContext().getString(R.string.startImmediatelyBtnText));
            case 9:
                return TrainingFragment.newInstance(paradigmType, Difficulty.ONE,
                        TutorialPagerFragment.TUTORIAL_TRIAL_COUNT);
        }
        throw new IllegalArgumentException(String.format(
                "Page properties do not match any setup; paradigm: %s, position: %d",
                paradigmType.toString(),
                position));
    }

    public static int getIconByParadigm(ParadigmType type) {
        switch (type) {
            case COLOR:
                return R.drawable.color_icon;
            case POSITION:
                return R.drawable.position_icon;
            case SHAPE:
                return R.drawable.shape_icon;
            default:
                return 0;
        }
    }
}
