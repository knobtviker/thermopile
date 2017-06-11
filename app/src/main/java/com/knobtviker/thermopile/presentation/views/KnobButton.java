package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.listeners.KnobButtonListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KnobButton extends RelativeLayout implements OnGestureListener {

    private GestureDetector gestureDetector;
    private float angleDown;
    private float angleUp;
    private boolean state = false;
    private int size = 480;
    private KnobButtonListener listener;

    @BindView(R.id.imageview_stator)
    public ImageView imageViewStator;

    @BindView(R.id.imageview_rotor)
    public ImageView imageViewRotor;

    public KnobButton(@NonNull Context context) {
        super(context);

        init(context);
    }

    public KnobButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public KnobButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public KnobButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    public void setListener(@Nullable final KnobButtonListener listener) {
        this.listener = listener;
    }

    public void setState(final boolean state) {
        this.state = state;
        imageViewRotor.setImageResource(state ? R.drawable.rotoron : R.drawable.rotoroff);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.size = getMeasuredHeight();
        super.onMeasure(size, size);
    }

    public boolean onDown(MotionEvent event) {
        float x = event.getX() / ((float) getWidth());
        float y = event.getY() / ((float) getHeight());
        angleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());
        angleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        // if we click up the same place where we clicked down, it's just a button press
        if (!Float.isNaN(angleDown) && !Float.isNaN(angleUp) && Math.abs(angleUp - angleDown) < 10) {
            setState(!state);
            if (listener != null) listener.onStateChange(state);
        }
        return true;
    }

    public void setRotorPosAngle(float deg) {
        if (deg >= 210 || deg <= 150) {
            if (deg > 180) deg = deg - 360;
            Matrix matrix = new Matrix();
            imageViewRotor.setScaleType(ScaleType.MATRIX);
            matrix.postRotate((float) deg, size / 2, size / 2);
            imageViewRotor.setImageMatrix(matrix);
        }
    }

    public void setRotorPercentage(int percentage) {
        int posDegree = percentage * 3 - 150;
        if (posDegree < 0) posDegree = 360 + posDegree;
        setRotorPosAngle(posDegree);
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());
        float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        if (!Float.isNaN(rotDegrees)) {
            // instead of getting 0-> 180, -180 0 , we go for 0 -> 360
            float posDegrees = rotDegrees;
            if (rotDegrees < 0) posDegrees = 360 + rotDegrees;

            // deny full rotation, start start and stop point, and get a linear scale
            if (posDegrees > 210 || posDegrees < 150) {
                // rotate our imageview
                setRotorPosAngle(posDegrees);
                // get a linear scale
                float scaleDegrees = rotDegrees + 150; // given the current parameters, we go from 0 to 300
                // get position percent
                int percent = (int) (scaleDegrees / 3);
                if (listener != null) listener.onRotate(percent);
                return true; //consumed
            } else
                return false;
        } else
            return false; // not consumed
    }

    public void onShowPress(MotionEvent e) {
        //DO NOTHING
    }

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
        //DO NOTHING
    }

    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }

    private void init(@NonNull final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_knob, (ViewGroup) getRootView());
        ButterKnife.bind(this, view);

        setState(state);
        gestureDetector = new GestureDetector(getContext(), this);
    }
}
