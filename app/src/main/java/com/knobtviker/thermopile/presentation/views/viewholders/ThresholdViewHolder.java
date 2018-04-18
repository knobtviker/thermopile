package com.knobtviker.thermopile.presentation.views.viewholders;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 11/11/2017.
 */

public class ThresholdViewHolder {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({State.STATE_DEFAULT, State.STATE_REMOVE})
    public @interface State {
        int STATE_DEFAULT = 0;
        int STATE_REMOVE = 1;

    }

    @State
    private int state = State.STATE_DEFAULT;

    private final int color;

    private final int height;

    @BindView(R.id.root_layout)
    public ConstraintLayout rootLayout;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.button_remove)
    public ImageButton buttonRemove;


    public static ThresholdViewHolder bind(@NonNull final View view, final int color, @NonNull final String value) {
        return new ThresholdViewHolder(view, color, value);
    }

    private ThresholdViewHolder(@NonNull final View view, final int color, @NonNull final String value) {
        ButterKnife.bind(this, view);

        this.color = color;

        this.height = view.getContext().getResources().getDimensionPixelSize(R.dimen.height_threshold);

        setBackground(view.getContext());
        setValue(value);
        setState(State.STATE_DEFAULT);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setBackground(@NonNull final Context context) {
        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(height);
        shapeDrawable.setIntrinsicWidth(height);
        shapeDrawable.getPaint().setColor(context.getColor(color));

        final float[] radii = {height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f};

        final ShapeDrawable backgroundDrawable = new ShapeDrawable(new RoundRectShape(radii, null, radii));
        backgroundDrawable.setIntrinsicHeight(height);
        backgroundDrawable.getPaint().setColor(context.getColor(R.color.background_divider));
        backgroundDrawable.setDither(true);

        textViewTemperature.setBackground(shapeDrawable);
        buttonRemove.setBackground(shapeDrawable);
        rootLayout.setBackground(backgroundDrawable);
    }

    private void setValue(@NonNull final String value) {
        textViewTemperature.setText(value);
    }

    public void setState(@State final int state) {
        this.state = state;
        switch (state) {
            case State.STATE_DEFAULT:
                textViewTemperature.setVisibility(View.VISIBLE);
                buttonRemove.setVisibility(View.INVISIBLE);
                break;
            case State.STATE_REMOVE:
                textViewTemperature.setVisibility(View.INVISIBLE);
                buttonRemove.setVisibility(View.VISIBLE);
                break;
        }
    }

    @State
    public int getState() {
        return state;
    }
}
