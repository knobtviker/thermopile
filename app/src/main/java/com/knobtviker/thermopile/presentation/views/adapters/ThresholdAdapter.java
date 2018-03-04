package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdLineViewHolder;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by bojan on 13/06/2017.
 */

public class ThresholdAdapter extends RecyclerView.Adapter<ThresholdLineViewHolder> {
    private final static String TAG = ThresholdAdapter.class.getSimpleName();

    private String timeFormat = "HH:mm";

    private final Context context;

    private final LayoutInflater layoutInflater;
    private final int colorTransparent;
    private final ImmutableList<String> days;
    private final CalligraphyTypefaceSpan typefaceSpanMedium;

    private ImmutableList<Pair<Threshold, Interval>> intervals = ImmutableList.of();

    public ThresholdAdapter(@NonNull final Context context) {
        this.context = context;

        this.layoutInflater = LayoutInflater.from(context);
        this.colorTransparent = ContextCompat.getColor(context, android.R.color.transparent);
        this.days = ImmutableList.copyOf(context.getResources().getStringArray(R.array.weekdays));
        this.typefaceSpanMedium = new CalligraphyTypefaceSpan(TypefaceUtils.load(context.getAssets(), "fonts/WorkSans-Medium.ttf"));
    }

    @Override
    public ThresholdLineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ThresholdLineViewHolder(layoutInflater.inflate(R.layout.item_threshold_line, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ThresholdLineViewHolder holder, int position) {
        final Pair<Threshold, Interval> pair = intervals.get(position);

        @Nullable final Threshold threshold = pair.first;
        final Interval interval = pair.second;

        if (threshold != null) {
            holder.viewIndicator.setBackgroundColor(context.getColor(threshold.color()));
            holder.textViewTemperature.setText(String.valueOf(threshold.temperature()));
        } else {
            holder.viewIndicator.setBackgroundResource(R.drawable.empty_interval_line);
            holder.textViewTemperature.setText("");
        }

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.rootLayout);
        constraintSet.constrainWidth(holder.viewIndicator.getId(), calculateWidth(interval));
        constraintSet.applyTo(holder.rootLayout);

        buildTime(holder.textViewTimeStart, interval.getStart().toString(timeFormat));
        buildTime(holder.textViewTimeEnd, interval.getEnd().toString(timeFormat));

//        holder.textViewDay.setText(days.get(interval.getStart().getDayOfWeek()-1));
    }

    @Override
    public void onViewRecycled(@NonNull ThresholdLineViewHolder holder) {

        holder.viewIndicator.setBackgroundColor(colorTransparent);

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return intervals.size();
    }

    public void updateData(@NonNull final RealmResults<Threshold> thresholds) {
        final List<Pair<Threshold, Interval>> intervals = new ArrayList<>();

        thresholds
            .forEach(threshold -> {
                final DateTime start = new DateTime()
                    .withDayOfWeek(threshold.day() + 1) //must be in range of 1 to 7
                    .withHourOfDay(threshold.startHour())
                    .withMinuteOfHour(threshold.startMinute())
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);

                final DateTime end = new DateTime()
                    .withDayOfWeek(threshold.day() + 1)
                    .withHourOfDay(threshold.endHour())
                    .withMinuteOfHour(threshold.endMinute())
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0)
                    .minusMillis(1);

                intervals.add(Pair.create(threshold, new Interval(start, end)));
            });

        IntStream.range(0, intervals.size() - 1)
            .forEach(index -> {
                final Interval current = intervals.get(index).second;
                final Interval next = intervals.get(index + 1).second;
                final Interval gap = current.gap(next);
                if (gap != null) {
                    intervals.add(Pair.create(null, gap));
                }
            });

        intervals.sort((interval, other) -> {
            if (interval.equals(other)) {
                return 0;
            } else {
                return interval.second.isBefore(other.second) ? -1 : 1;
            }
        });

        this.intervals = ImmutableList.copyOf(intervals);

        notifyDataSetChanged();
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
        spannableStringBuilder.setSpan(typefaceSpanMedium, 0, time.indexOf(":"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    public String getItemDay(final int firstVisibleItemPosition) {
        return days.get(intervals.get(firstVisibleItemPosition).second.getStart().getDayOfWeek()-1);
    }
}
