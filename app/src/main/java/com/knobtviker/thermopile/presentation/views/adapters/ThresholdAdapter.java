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

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.constants.FormatDate;
import com.knobtviker.thermopile.presentation.utils.constants.FormatTime;
import com.knobtviker.thermopile.presentation.utils.constants.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.factories.ThresholdIntervalFactory;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdLineViewHolder;

import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdAdapter extends RecyclerView.Adapter<ThresholdLineViewHolder> {
    private final static String TAG = ThresholdAdapter.class.getSimpleName();


    private final LayoutInflater layoutInflater;
    private final List<String> days;
    private final List<String> daysShort;

    private int unitTemperature;
    private String formatTime;
    private String formatDate;

    private List<ThresholdInterval> intervals = new ArrayList<>(0);

    public ThresholdAdapter(@NonNull final Context context, @UnitTemperature final int unitTemperature, @FormatTime @NonNull final String formatTime, @FormatDate @NonNull final String formatDate) {
        this.layoutInflater = LayoutInflater.from(context);
        this.days = Arrays.asList(context.getResources().getStringArray(R.array.weekdays));
        this.daysShort = Arrays.asList(context.getResources().getStringArray(R.array.weekdays_short));
        this.unitTemperature = unitTemperature;
        this.formatTime = formatTime;
        this.formatDate = formatDate;

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
            holder.textViewTemperature.setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, threshold.temperature))));

            buildTime(holder.textViewTimeStart, interval.getStart().toString(formatTime));
            buildTime(holder.textViewTimeEnd, interval.getEnd().toString(formatTime));
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
        if (formatDate.contains("EEEE")) {
            return days.get(intervals.get(firstVisibleItemPosition).interval().getStart().getDayOfWeek() - 1);
        } else if (formatDate.contains("EE")) {
            return daysShort.get(intervals.get(firstVisibleItemPosition).interval().getStart().getDayOfWeek() - 1);
        } else {
            return days.get(intervals.get(firstVisibleItemPosition).interval().getStart().getDayOfWeek() - 1);
        }
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

    public void setUnitAndFormat(@UnitTemperature final int unitTemperature, @FormatTime @NonNull final String formatTime, @FormatDate @NonNull final String formatDate) {
        this.unitTemperature = unitTemperature;
        this.formatTime = formatTime;
        this.formatDate = formatDate;

        notifyDataSetChanged();
    }
}
