package cz.nudz.www.trainingapp;

import android.graphics.Point;
import android.widget.ImageView;

/**
 * Created by artem on 30-Jun-17.
 */

public class Stimulus {
    public final ImageView view;
    public Point position;

    public Stimulus(ImageView view) {
        this.view = view;
    }
}
