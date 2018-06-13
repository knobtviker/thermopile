package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.knobtviker.thermopile.R;

import java.util.stream.IntStream;

import timber.log.Timber;

public class TemperatureGradientView extends View {

    private int width = 0;
    private int height = 0;

    @Nullable
    private TypedArray typedArray;

    private Paint paint;

    private int backgroundColor;

    private Path path;

    public TemperatureGradientView(Context context) {
        super(context);

//        obtainTypedArray(context, null);
//        attributes();
        setup(context);
    }

    public TemperatureGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        obtainTypedArray(context, null);
//        attributes();
        setup(context);
    }

    public TemperatureGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        obtainTypedArray(context, null);
//        attributes();
        setup(context);
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
    private void setup(@NonNull final Context context) {
        backgroundColor = context.getColor(R.color.background);

        paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(backgroundColor);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));


        path = new Path();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        this.width = width;
        this.height = height;

        Timber.i("onSizeChanged(width, height, oldWidth, oldHeight): %d %d %d %d", width, height, oldWidth, oldHeight);
        //onSizeChanged(width, height, oldWidth, oldHeight): 736 40 0 0

        path.reset();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        IntStream
            .range(0, 3)
            .forEach(
                i -> IntStream
                    .range(0, 48)
                    .forEach(j -> path.addCircle((j * 16) + 8, (i * 16) + 8, 6, Path.Direction.CW))
            );

//        path.toggleInverseFillType();
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Bitmap transparentBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//        transparentBitmap.eraseColor(Color.TRANSPARENT);
//        canvas.setBitmap(transparentBitmap);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
//        canvas.drawColor(backgroundColor);
        canvas.drawPath(path, paint);
    }
}
