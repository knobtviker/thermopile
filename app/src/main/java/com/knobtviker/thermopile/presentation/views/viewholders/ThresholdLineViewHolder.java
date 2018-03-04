package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.root_layout)
    public ConstraintLayout rootLayout;

    @BindView(R.id.view_indicator)
    public View viewIndicator;

    @BindView(R.id.textview_time_start)
    public TextView textViewTimeStart;

    @BindView(R.id.textview_time_end)
    public TextView textViewTimeEnd;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    public ThresholdLineViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
