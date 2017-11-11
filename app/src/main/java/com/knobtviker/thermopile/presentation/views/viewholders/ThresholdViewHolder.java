package com.knobtviker.thermopile.presentation.views.viewholders;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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

    public void setBackground(final int color, final int height) {
        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(height);
        shapeDrawable.setIntrinsicWidth(height);
        shapeDrawable.getPaint().setColor(color);

        final GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {color, 0x00000000});
        gradientDrawable.setAlpha(211);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(height/2.0f);

        textViewTemperature.setBackground(shapeDrawable);
        rootLayout.setBackground(gradientDrawable);
    }
}
