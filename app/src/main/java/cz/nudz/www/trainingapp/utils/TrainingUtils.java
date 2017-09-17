package cz.nudz.www.trainingapp.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

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
    public static int getStimCount(int difficulty) {
        return (1 + difficulty) * 2;
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
}
