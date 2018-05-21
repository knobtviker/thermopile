package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.factories.ThresholdIntervalFactory;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdLineViewHolder;

import org.joda.time.Interval;
import org.joda.time.Minutes;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdAdapter extends RecyclerView.Adapter<ThresholdLineViewHolder> {
    private final static String TAG = ThresholdAdapter.class.getSimpleName();

    private String timeFormat = "HH:mm";

    private final LayoutInflater layoutInflater;
    private final ImmutableList<String> days;

    private ImmutableList<ThresholdInterval> intervals = ImmutableList.of();

    public ThresholdAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.days = ImmutableList.copyOf(context.getResources().getStringArray(R.array.weekdays));

        setEmptyDays();
    }

    @NonNull
    @Override
    public ThresholdLineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ThresholdLineViewHolder(layoutInflater.inflate(R.layout.item_threshold_line, null));
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
            holder.textViewTemperature.setText(String.valueOf(threshold.temperature));

            buildTime(holder.textViewTimeStart, interval.getStart().toString(timeFormat));
            buildTime(holder.textViewTimeEnd, interval.getEnd().toString(timeFormat));
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

    public void setData(ImmutableList<ThresholdInterval> thresholdIntervals) {
        this.intervals = thresholdIntervals;

        notifyDataSetChanged();
    }

    public String getItemDay(final int firstVisibleItemPosition) {
        return days.get(intervals.get(firstVisibleItemPosition).interval().getStart().getDayOfWeek() - 1);
    }

    public void setTimeFormat(@NonNull final String timeFormat) {
        this.timeFormat = timeFormat;
        notifyDataSetChanged();
    }

    private int calculateWidth(@NonNull final Interval interval) {
        return Minutes.minutesBetween(interval.getStart(), interval.getEnd()).getMinutes();
    }

    private void buildTime(@NonNull final TextView textView, @NonNull final String time) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(time);
//        spannableStringBuilder.setSpan(typefaceSpanMedium, 0, time.indexOf(":"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    private void setEmptyDays() {
        this.intervals = ThresholdIntervalFactory.emptyDays();

        notifyDataSetChanged();
    }
}
