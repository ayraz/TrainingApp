package cz.nudz.www.trainingapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.training.Difficulty;
import cz.nudz.www.trainingapp.training.SequenceFragment;
import cz.nudz.www.trainingapp.training.TrainingActivity;

/**
 * Created by aa250602 on 14/9/2017.
 */

public class TrainingUtils {
    public static List<Rect> generateGridPositions(int gridSize, int cellSize) {
        List<Rect> positions = new ArrayList<>();
        int col = 0;
        while (col * cellSize + cellSize <= gridSize) {
            int row = 0;
            while (row * cellSize + cellSize <= gridSize) {
                positions.add(new Rect(
                        col * cellSize,
                        row * cellSize,
                        col * cellSize + cellSize,
                        row * cellSize + cellSize));
                ++row;
            }
            ++col;
        }
        return positions;
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

    @NonNull
    public static ImageView createStimView(Context context) {
        ImageView v = new ImageView(context);
        v.setVisibility(View.INVISIBLE);
        // we need to set view's id to later find it in the layout..
        v.setId(View.generateViewId());
        return v;
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
