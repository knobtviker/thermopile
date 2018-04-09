package com.knobtviker.thermopile.presentation.views.viewholders;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 11/11/2017.
 */

public class ThresholdViewHolder {

    @BindView(R.id.root_layout)
    public ConstraintLayout rootLayout;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    public ThresholdViewHolder(@NonNull final View view) {

        ButterKnife.bind(this, view);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setBackground(final int color, final int height) {
        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(height);
        shapeDrawable.setIntrinsicWidth(height);
        shapeDrawable.getPaint().setColor(color);

        final float[] radii = {height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f, height / 2.0f};

        final ShapeDrawable backgroundDrawable = new ShapeDrawable(new RoundRectShape(radii, null, radii));
        backgroundDrawable.setIntrinsicHeight(height);
        backgroundDrawable.getPaint().setColor(rootLayout.getContext().getColor(R.color.background_divider));
        backgroundDrawable.setDither(true);

        textViewTemperature.setBackground(shapeDrawable);
        rootLayout.setBackground(backgroundDrawable);
    }
}
