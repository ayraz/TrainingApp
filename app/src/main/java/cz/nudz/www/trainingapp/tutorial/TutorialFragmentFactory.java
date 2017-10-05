package cz.nudz.www.trainingapp.tutorial;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.training.ParadigmType;

/**
 * Created by aa250602 on 10/5/17.
 */

public class TutorialFragmentFactory {

    public static TutorialImageFragment createTutorialFragment(ParadigmType type, int tutorialPosition) {
        switch (tutorialPosition) {
            case 0:
                return TutorialImageFragment.newInstance(
                        (R.string.shapeParadigmStartHelp),
                        (R.string.tutorialFragmentNextHelp));
            case 1:
                return TutorialImageFragment.newInstance(
                        (R.string.tutorialFragmentCueHelp),
                        (R.string.tutorialFragmentNextPreviousHelp));
            case 2:
                return TutorialImageFragment.newInstance(
                        (R.string.tutorialFragmentSmallPause),
                        (R.string.tutorialFragmentNextPreviousHelp));

        }
        return new TutorialImageFragment();
    }
}
