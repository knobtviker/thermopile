package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.knobtviker.thermopile.R;

/**
 * Created by bojan on 03/12/2017.
 */

public class ArcView extends View {

    private final int START_ANGLE = 135;
    private final int SWEEP_ANGLE_MAX = 275;

    private int padding = 0;
    private int width = 0;
    private int height = 0;

    @Nullable
    private TypedArray typedArray;

    private float progress = 0.0f;
    private int progressColor = 0;
    private int trackColor = 0;
    private float thickness = 0.0f;

    private Paint paint;
    private RectF arc;

    public ArcView(Context context) {
        super(context);

        obtainTypedArray(context, null);
        attributes();
        setup();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        obtainTypedArray(context, attrs);
        attributes();
        setup();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainTypedArray(context, attrs);
        attributes();
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        arc.set((thickness / 2) + padding, (thickness / 2) + padding, width - padding - (thickness / 2), height - padding - (thickness / 2));

        //First draw full arc as background.
        paint.setColor(trackColor);
        canvas.drawArc(arc, START_ANGLE, SWEEP_ANGLE_MAX, false, paint);

        //Then draw arc progress with actual value.
        paint.setColor(progressColor);
        canvas.drawArc(arc, START_ANGLE, calculateProgressSweepAngle(), false, paint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        final int size = Math.min(width, height);

        this.width = size;
        this.height = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int size = Math.min(width, height);

        setMeasuredDimension(size, size);
    }

    private float calculateProgressSweepAngle() {
        return SWEEP_ANGLE_MAX * progress;
    }

    public void setProgress(final float progress) {
        this.progress = progress;

        invalidate();
        requestLayout();
    }

    public void setProgressColor(@ColorRes final int color) {
        this.progressColor = getContext().getColor(color);

        invalidate();
        requestLayout();
    }

    public float getProgress() {
        return progress;
    }

    private void obtainTypedArray(@NonNull final Context context, @Nullable AttributeSet attrs) {
        typedArray = attrs == null ? null : context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcView, 0, 0);
    }

    private void attributes() {
        if (typedArray != null) {
            progress = typedArray.getFloat(R.styleable.ArcView_progress, 0.0f);
            progressColor = typedArray.getColor(R.styleable.ArcView_progressColor, 0);
            trackColor = typedArray.getColor(R.styleable.ArcView_trackColor, 0);
            thickness = typedArray.getDimensionPixelSize(R.styleable.ArcView_stroke_thickness, 0);

            typedArray.recycle();
        }
    }

    private void setup() {
        //Paint for arc stroke.
        paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);

        arc = new RectF();
    }
}
