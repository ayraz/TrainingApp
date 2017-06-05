package cz.nudz.www.trainingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by artem on 02-Apr-17.
 */

public class ShapeView extends android.support.v7.widget.AppCompatImageView {

    private Drawable shape;

    public ShapeView(Context context, Drawable shape) {
        super(context);
        this.shape = shape;
//        shape.setBounds(0,0,50,25);
        this.setImageDrawable(shape);
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
