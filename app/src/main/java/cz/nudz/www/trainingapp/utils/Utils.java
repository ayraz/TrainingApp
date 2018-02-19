package cz.nudz.www.trainingapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import cz.nudz.www.trainingapp.BaseActivity;
import cz.nudz.www.trainingapp.R;
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

    public static void setViewsVisibility(int visibility, View... views) {
        for (View v : views) {
            v.setVisibility(visibility);
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
    public static void showAlertDialog(AppCompatActivity context, @Nullable String title, @NonNull String message) {
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

    /**
     * Gets shape name by drawable id.
     */
    public static String getShapeName(int drawableShapeId) {
        switch (drawableShapeId) {
            case R.drawable.circle:
                return "circle";
            case R.drawable.ellipse:
                return "ellipse";
            case R.drawable.square:
                return "square";
            case R.drawable.rect:
                return "rect";
            case R.drawable.triangle:
                return "triangle";
            case R.drawable.trapezoid:
                return "trapezoid";
            case R.drawable.pentagon:
                return "pentagon";
            case R.drawable.star:
                return "star";
            case R.drawable.parallelogram:
                return "parallelogram";
            case R.drawable.cross:
                return "cross";
            case R.drawable.rhombus:
                return "rhombus";
            case R.drawable.kite:
                return "kite";
            default:
                throw new IllegalArgumentException(String.format("Drawable with id: %d, does not match any shape.", drawableShapeId));
        }
    }

    public static void adjustToastPosition(Activity activity, View root, Toast toast) {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            final int activityWidth = activity.getWindow().getDecorView().getRootView().getWidth();
            toast.setGravity(
                    Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,
                    (activityWidth - root.getWidth()) / 2,
                    32);
        }
    }

    public static AlertDialog adjustDialogPosition(Activity activity, View root, AlertDialog dialog) {
        final int activityWidth = activity.getWindow().getDecorView().getRootView().getWidth();
        final WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        attributes.x = (activityWidth - root.getWidth()) / 2;
        return dialog;
    }

    public static void showSequenceFeedback(List<Boolean> answers, int trialCount,
                                      Activity activity, View root) {
        int correctCount = 0;
        for (Boolean a : answers) {
            if (a != null && a) ++correctCount;
        }
        Utils.adjustDialogPosition(activity, root, new AlertDialog.Builder(activity)
                .setMessage(Html.fromHtml(String.format(
                        activity.getString(R.string.correntTrialAnswerRatio),
                        correctCount, trialCount)))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {})
                .create())
                .show();
    }
}
