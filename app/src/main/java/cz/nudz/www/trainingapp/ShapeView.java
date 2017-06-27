package cz.nudz.www.trainingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * Created by artem on 02-Apr-17.
 */

public class ShapeView extends android.support.v7.widget.AppCompatImageView {

    public final Point positionInLayout;

    public ShapeView(Context context, Drawable shape, Point positionInLayout) {
        super(context);
        this.positionInLayout = positionInLayout;
        this.setImageDrawable(shape);
//        shape.setBounds(0,0,50,25);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int w = resolveSize(shape.getBounds().width(), widthMeasureSpec);
//        int h = resolveSize(shape.getBounds().height(), widthMeasureSpec);
//
//        setMeasuredDimension(w, h);
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        shape.draw(canvas);
//    }
}
