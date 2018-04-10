package com.knobtviker.thermopile.presentation.views.viewholders;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.listeners.ScheduleListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;

/**
 * Created by bojan on 11/11/2017.
 */

public class ThresholdViewHolder {

    private final long id;

    private final int color;

    private final ScheduleListener listener;

    private final int height;

    @BindView(R.id.root_layout)
    public ConstraintLayout rootLayout;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.button_remove)
    public ImageButton buttonRemove;

    public static void bind(@NonNull final View view, final long id, final int color, @NonNull final String value, @NonNull final ScheduleListener listener) {
        new ThresholdViewHolder(view, id, color, value, listener);
    }

    private ThresholdViewHolder(@NonNull final View view, final long id, final int color, @NonNull final String value, @NonNull final ScheduleListener listener) {
        ButterKnife.bind(this, view);

        this.id = id;
        this.color = color;
        this.listener = listener;

        this.height = view.getContext().getResources().getDimensionPixelSize(R.dimen.height_threshold);

        setBackground(view.getContext());
        setValue(value);
    }

    @OnClick({R.id.root_layout}) //, R.id.button_remove
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.root_layout:
                listener.showThreshold(id);
                break;
//            case R.id.button_remove:
//                listener.removeThreshold(id);
//                break;
        }
    }

    @OnLongClick({R.id.root_layout, R.id.textview_temperature})
    public boolean onLongClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.root_layout:
            case R.id.textview_temperature:
                rootLayout.requestFocus();
                return true;
            default:
                return false;
        }
    }

    @OnFocusChange({R.id.root_layout})
    public void onFocusChanged(@NonNull final View view, final boolean isFocused) {
        switch (view.getId()) {
            case R.id.root_layout:
                textViewTemperature.setVisibility(isFocused ? View.INVISIBLE : View.VISIBLE);
                buttonRemove.setVisibility(isFocused ? View.VISIBLE : View.INVISIBLE);
        }
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
}
