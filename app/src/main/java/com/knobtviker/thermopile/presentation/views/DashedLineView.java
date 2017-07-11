package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.knobtviker.thermopile.R;

/**
 * Created by bojan on 11/07/2017.
 */

public class DashedLineView extends View {
    private final int strokeWidth = 4;
    private final int dashWidth = 8;
    private final int dashGap = 8;

    private Paint paint;
    private Path path;
    private PathEffect effects;

    public DashedLineView(Context context) {
        super(context);

        init(context);
    }

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public DashedLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(context.getResources().getDisplayMetrics().density * strokeWidth);
        //set your own color
        paint.setColor(ContextCompat.getColor(context, R.color.light_gray));
        path = new Path();
        //array is ON and OFF distances in px (8px line then 8px space)
        effects = new DashPathEffect(
            new float[]{
                dashWidth, dashGap,
                dashWidth, dashGap
            },
            0
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setPathEffect(effects);

        final int measuredHeight = getMeasuredHeight();
        final int measuredWidth = getMeasuredWidth();
        if (measuredHeight <= measuredWidth) {
            // horizontal
            path.moveTo(0, 0);
            path.lineTo(measuredWidth, 0);
            canvas.drawPath(path, paint);
        } else {
            // vertical
            path.moveTo(0, 0);
            path.lineTo(0, measuredHeight);
            canvas.drawPath(path, paint);
        }
    }
}
