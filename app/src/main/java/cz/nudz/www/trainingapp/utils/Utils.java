package cz.nudz.www.trainingapp.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import cz.nudz.www.trainingapp.dialogs.AlertDialogFragment;
import cz.nudz.www.trainingapp.dialogs.ErrorDialogFragment;
import cz.nudz.www.trainingapp.dialogs.YesNoDialogFragment;
import cz.nudz.www.trainingapp.enums.Difficulty;

/**
 * Created by aa250602 on 14/9/2017.
 */

public class Utils {

    /**
     * Sets the background for a view while preserving its current padding.
     *
     * @param view View to receive the new background.
     * @param color Background color.
     */
    public static void setBackgroundAndKeepPadding(View view, int color) {
        int top = view.getPaddingTop();
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingBottom();

        view.setBackgroundColor(color);
        view.setPadding(left, top, right, bottom);
    }

    /**
     * See: https://math.stackexchange.com/questions/466198/algorithm-to-get-the-maximum-size-of-n-squares-that-fit-into-a-rectangle-with-a
     * @param x Width of containing grid.
     * @param y Height of containing grid.
     * @param n Number of rectangles to be contained within the grid.
     * @return Optimal containing square size.
     */
    public static int optimalContainingSquareSize(int x, int y, int n) {
        double sx, sy;

        double px = Math.ceil(Math.sqrt(n * x / y));
        if (Math.floor(px * y / x) * px < n)  // does not fit, y/(x/px)=px*y/x
            sx = y / Math.ceil(px * y / x);
        else
            sx = x / px;

        double py = Math.ceil(Math.sqrt(n * y / x));
        if (Math.floor(py * x / y) * py < n)  // does not fit
            sy = x / Math.ceil(x * py / y);
        else
            sy = y / py;

        return (int) Math.max(sx, sy);
    }

    /**
     * Calculates stimuli count based on sequence's difficulty.
     * @param difficulty
     * @return
     */
    public static int getStimCount(Difficulty difficulty) {
        return (1 + Difficulty.toInteger(difficulty)) * 2;
    }

    /**
     *
     * @param answers
     * @param currentDifficulty
     * @return Returns difficulty adjusted according to answers.
     * Null if user has reached max difficulty and threshold for it being raised.
     */
    public static Difficulty adjustDifficulty(List<Boolean> answers, Difficulty currentDifficulty) {
        int raiseThreshold = (int) (answers.size() * 0.9);
        int lowerThreshold = (int) (answers.size() * 0.5);
        int correctCount = 0;
        for (Boolean answer : answers) {
            if (answer != null && answer) {
                correctCount += 1;
            }
        }
        Difficulty adjustment = currentDifficulty;
        if (correctCount >= raiseThreshold) {
            adjustment = Difficulty.next(currentDifficulty);
        } else if (correctCount < lowerThreshold && currentDifficulty.ordinal() != 0) {
            adjustment = Difficulty.prev(currentDifficulty);
        }
        return adjustment;
    }

    public static void setViewsVisible(boolean visible, View... views) {
        for (View v : views) {
            v.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        }
    }

    public static void enableViews(boolean enable, View... views) {
        for (View v : views) {
            v.setEnabled(enable);
        }
    }

    /**
     * Checks if given text is null, null string, or empty after trimming.
     * @param text
     * @return
     */
    public static boolean isNullOrEmpty(String text) {
        return (text == null
                || text.isEmpty()
                || text.trim().equals("null")
                || text.trim().length() <= 0);
    }

    /**
     * Shows a generic error dialog with a single 'OK' button.
     * @param context
     * @param title
     * @param message
     */
    public static void showErrorDialog(AppCompatActivity context, @Nullable String title, @NonNull String message) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        AlertDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(title, message);
        errorDialogFragment.show(fragmentManager, ErrorDialogFragment.TAG);
    }

    /**
     * Shows a generic YesNo dialog, caller must implement YesNoDialogFragmentListener interface.
     * @param context
     * @param title
     * @param message
     */
    public static void showYesNoDialog(AppCompatActivity context, @Nullable String title, @NonNull String message) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        AlertDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(title, message);
        yesNoDialogFragment.show(fragmentManager, YesNoDialogFragment.TAG);
    }
}
