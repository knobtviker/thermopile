package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class ModeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.view_color_indicator)
    public View viewColorIndicator;

    @BindView(R.id.textview_name)
    public TextView textViewName;

    @BindView(R.id.textview_time_start)
    public TextView textViewStart;

    @BindView(R.id.textview_time_end)
    public TextView textViewEnd;

    @BindView(R.id.textview_days_monday)
    public TextView textViewDaysMonday;

    @BindView(R.id.textview_days_tuesday)
    public TextView textViewDaysTuesday;

    @BindView(R.id.textview_days_wednesday)
    public TextView textViewDaysWednesday;

    @BindView(R.id.textview_days_thursday)
    public TextView textViewDaysThursday;

    @BindView(R.id.textview_days_friday)
    public TextView textViewDaysFriday;

    @BindView(R.id.textview_days_saturday)
    public TextView textViewDaysSaturday;

    @BindView(R.id.textview_days_sunday)
    public TextView textViewDaysSunday;

    public ModeViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
