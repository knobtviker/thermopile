package com.knobtviker.thermopile.presentation.views.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.factories.ThresholdIntervalFactory;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdLineViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdAdapter extends RecyclerView.Adapter<ThresholdLineViewHolder> {

    private final List<String> days;
    private final List<String> daysShort;

    //TODO: Fix this because this should not be initialised and then overwritten later
    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @NonNull
    private String formatTime = FormatTime.HH_MM;

    @NonNull
    private String formatDate = FormatDate.EEEE_DD_MM_YYYY;

    private List<ThresholdInterval> intervals = new ArrayList<>(0);

    public ThresholdAdapter(@NonNull final List<String> days, @NonNull final List<String> daysShort) {
        this.days = days;
        this.daysShort = daysShort;

        setEmptyDays();
    }

    @NonNull
    @Override
    public ThresholdLineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ThresholdLineViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_threshold_line, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ThresholdLineViewHolder holder, int position) {
        final ThresholdInterval item = intervals.get(position);

        final Threshold threshold = item.threshold();
        final Interval interval = item.interval();

        holder.textViewTimeStart.setVisibility(threshold != null ? View.VISIBLE : View.GONE);
        holder.textViewTimeEnd.setVisibility(threshold != null ? View.VISIBLE : View.GONE);
        if (threshold != null) {
            holder.viewIndicator.setBackgroundColor(Color.parseColor(threshold.color));
            holder.textViewTemperature
                .setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, threshold.temperature))));

            buildTime(
                holder.textViewTimeStart,
                DateTimeKit.format(interval.getStart().toEpochMilli(), formatTime)
            );
            buildTime(
                holder.textViewTimeEnd,
                DateTimeKit.format(interval.getEnd().toEpochMilli(), formatTime)
            );
        } else {
            holder.viewIndicator.setBackgroundResource(R.drawable.empty_interval_line);
            holder.textViewTemperature.setText("");
        }

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.rootLayout);
        constraintSet.constrainWidth(holder.viewIndicator.getId(), calculateWidth(interval));
        constraintSet.applyTo(holder.rootLayout);
    }

    @Override
    public void onViewRecycled(@NonNull ThresholdLineViewHolder holder) {

        holder.viewIndicator.setBackgroundResource(R.drawable.empty_interval_line);
        holder.textViewTimeStart.setVisibility(View.GONE);
        holder.textViewTimeEnd.setVisibility(View.GONE);

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return intervals.size();
    }

    public void setData(@NonNull final List<ThresholdInterval> thresholdIntervals) {
        this.intervals = thresholdIntervals;

        notifyDataSetChanged();
    }

    public String getItemDay(final int firstVisibleItemPosition) {
        final int dayOfWeek = DateTimeKit.dayOfWeek(intervals.get(firstVisibleItemPosition).interval()) - 1;
        if (formatDate.contains("EEEE")) {
            return days.get(dayOfWeek);
        } else if (formatDate.contains("EE")) {
            return daysShort.get(dayOfWeek);
        } else {
            return days.get(dayOfWeek);
        }
    }

    private int calculateWidth(@NonNull final Interval interval) {
        return (int) DateTimeKit.minutesBetween(interval.getStart(), interval.getEnd());
    }

    private void buildTime(@NonNull final TextView textView, @NonNull final String time) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(time);
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    private void setEmptyDays() {
        this.intervals = ThresholdIntervalFactory.emptyDays();

        notifyDataSetChanged();
    }

    public void setUnitAndFormat(
        @UnitTemperature final int unitTemperature,
        @FormatTime @NonNull final String formatTime,
        @FormatDate @NonNull final String formatDate
    ) {
        this.unitTemperature = unitTemperature;
        this.formatTime = formatTime;
        this.formatDate = formatDate;

        notifyDataSetChanged();
    }
}
