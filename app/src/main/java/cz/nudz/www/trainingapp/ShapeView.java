package cz.nudz.www.trainingapp;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * Created by artem on 02-Apr-17.
 */

public class ShapeView extends android.support.v7.widget.AppCompatImageView {

    public final Point position;

    public ShapeView(Context context, Drawable shape, Point position) {
        super(context);
        this.position = position;
        this.setImageDrawable(shape);
//        view.setBounds(0,0,50,25);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int w = resolveSize(view.getBounds().width(), widthMeasureSpec);
//        int h = resolveSize(view.getBounds().height(), widthMeasureSpec);
//
//        setMeasuredDimension(w, h);
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        view.draw(canvas);
//    }
}
