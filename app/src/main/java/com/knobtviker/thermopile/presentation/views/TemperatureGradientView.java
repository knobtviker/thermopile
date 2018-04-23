package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class TemperatureGradientView extends View {

    @Nullable
    private TypedArray typedArray;

    public TemperatureGradientView(Context context) {
        super(context);

//        obtainTypedArray(context, null);
//        attributes();
//        setup();
    }

    public TemperatureGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        obtainTypedArray(context, null);
//        attributes();
//        setup();
    }

    public TemperatureGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        obtainTypedArray(context, null);
//        attributes();
//        setup();
    }

//    private void obtainTypedArray(@NonNull final Context context, @Nullable AttributeSet attrs) {
//        typedArray = attrs == null ? null : context.getTheme().obtainStyledAttributes(attrs, R.styleable.TemperatureGradientView, 0, 0);
//    }
//
//    private void attributes() {
//        if (typedArray != null) {
//            startColor = typedArray.getColor(R.styleable.TemperatureGradientView_startColor, 0);
//            centerColor = typedArray.getColor(R.styleable.TemperatureGradientView_centerColor, 0);
//            endColor = typedArray.getColor(R.styleable.TemperatureGradientView_endColor, 0);
//            spacing = typedArray.getDimensionPixelSize(R.styleable.TemperatureGradientView_spacing, 0);
//            radius = typedArray.getDimensionPixelSize(R.styleable.TemperatureGradientView_radius, 0);
//
//            typedArray.recycle();
//        }
//    }
//
//    private void setup() {
//        //Paint for arc stroke.
//        paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
//        paint.setStrokeWidth(thickness);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setAntiAlias(true);
//        paint.setDither(true);
//
//        arc = new RectF();
//    }
}
